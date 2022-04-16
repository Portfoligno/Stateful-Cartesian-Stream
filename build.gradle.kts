plugins {
  maven
  `java-library`
}
tasks.wrapper {
  gradleVersion = "6.9.2"
}

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}
repositories {
  mavenCentral()
}
dependencies {
  implementation("com.google.guava", "guava", "31.1-jre") {
    exclude("com.google.code.findbugs", "jsr305")
    exclude("org.checkerframework", "checker-qual")
    exclude("com.google.errorprone", "error_prone_annotations")
    exclude("com.google.j2objc", "j2objc-annotations")
  }
}
