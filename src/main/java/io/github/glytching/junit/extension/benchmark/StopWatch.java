package io.github.glytching.junit.extension.benchmark;

import java.util.concurrent.TimeUnit;

import static java.lang.System.nanoTime;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

/** A simple wrapper for use in reporting test elapsed time in a chosen {@link TimeUnit}. */
public class StopWatch {

  private long start;

  /** Constructs a StopWatch with a start time equal to the system's current nano time. */
  public StopWatch() {
    this.start = nanoTime();
  }

  /**
   * Returns the duration of since this instance was created. The duration will be converted into
   * the given {@code timeUnit}.
   *
   * @param timeUnit the units in which the duration is returned
   * @return The elapsed time converted to the specified units
   */
  public long duration(TimeUnit timeUnit) {
    return timeUnit.convert(nanoTime() - start, NANOSECONDS);
  }
}
