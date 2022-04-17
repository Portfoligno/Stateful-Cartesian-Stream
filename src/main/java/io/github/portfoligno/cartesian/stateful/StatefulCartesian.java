package io.github.portfoligno.cartesian.stateful;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Spliterators.spliteratorUnknownSize;

/**
 * A poor man's for-comprehension/do-notation for stream composition
 */
public class StatefulCartesian {
  private final List<Iterator<?>> iterators = Lists.newArrayList();
  private final List<Object> values = Lists.newArrayList();
  private int index;

  private StatefulCartesian() {
  }

  /**
   * Contribute a new axis to the iteration. Assumed to be invoked consistently across runs.
   */
  public <T> T pull(Supplier<? extends Stream<T>> stream) {
    return pull(() -> stream.get().iterator());
  }

  /**
   * Contribute a new axis to the iteration. Assumed to be invoked consistently across runs.
   */
  @SuppressWarnings("unchecked")
  public <T> T pull(Iterable<T> iterable) {
    int i = index = 1 + index;

    if (i == iterators.size()) {
      iterators.add(iterable.iterator());
    }
    int n = values.size();

    if (i == n) {
      values.add(iterators.get(i).next());
    } else if (i == n - 1) {
      values.set(i, iterators.get(i).next());
    }
    return (T) values.get(i);
  }

  public static <T> Stream<T> stream(Function<StatefulCartesian, T> iteration) {
    StatefulCartesian state = new StatefulCartesian();
    Iterator<T> iterator = new AbstractIterator<T>() {
      @Override
      protected T computeNext() {
        return state.computeNext(this::endOfData, iteration);
      }
    };

    return StreamSupport.stream(spliteratorUnknownSize(iterator, 0), false);
  }

  private <T> T computeNext(Supplier<T> endOfData, Function<StatefulCartesian, T> iteration) {
    while (true) {
      if (index == -2) {
        return endOfData.get();
      }
      index = -1;

      try {
        T t = iteration.apply(this);

        if (iterators.isEmpty()) {
          index = -2;
        }
        return t;
      } catch (NoSuchElementException e) {
        int i = index;

        if (iterators.size() - 1 == i && !iterators.get(i).hasNext()) {
          iterators.remove(i);

          if (values.size() - 1 == i) {
            values.remove(i);
          }
        } else {
          throw e;
        }
        if (iterators.isEmpty()) {
          index = -2;
        }
      }
    }
  }
}
