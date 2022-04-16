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
 * A poor man's for-comprehension/do-notation
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
  public <T> T pull(Supplier<Stream<T>> stream) {
    return pullIterator(Stream.of(stream).flatMap(Supplier::get).iterator());
  }

  /**
   * Contribute a new axis to the iteration. Assumed to be invoked consistently across runs.
   */
  public <T> T pull(Iterable<T> iterable) {
    return pullIterator(iterable.iterator());
  }

  @SuppressWarnings("unchecked")
  private <T> T pullIterator(Iterator<T> iterator) {
    index++;

    if (index == iterators.size()) {
      iterators.add(iterator);
    }
    int n = values.size();

    if (index == n) {
      values.add(iterators.get(index).next());
    } else if (index == n - 1) {
      values.set(index, iterators.get(index).next());
    }
    return (T) values.get(index);
  }

  public static <T> Stream<T> yieldAll(Function<StatefulCartesian, T> iteration) {
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
          values.remove(i);
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
