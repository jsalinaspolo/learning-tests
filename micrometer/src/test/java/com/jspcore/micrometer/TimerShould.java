package com.jspcore.micrometer;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class TimerShould {

  private static final String MY_TIMER = "my.timer";
  private final MeterRegistry registry = new SimpleMeterRegistry();

  @Test
  public void record_count() {
    Timer timer = Timer.builder(MY_TIMER)
      .register(registry);

    timer.record(() -> "something");
    timer.record(() -> "something");

    assertThat(registry.timer(MY_TIMER).count()).isEqualTo(2);
  }

  @Test
  public void record_time_elapsed() {
    Timer timer = Timer.builder(MY_TIMER)
      .register(registry);

    timer.record(() -> waitFor(TimeUnit.MILLISECONDS, 50));

    assertThat(registry.timer(MY_TIMER).totalTime(MILLISECONDS)).isGreaterThan(50);
  }

  @Test
  public void record_time_elapsed_when_exception() {
    Timer timer = Timer.builder(MY_TIMER)
      .register(registry);

    catchThrowable(() -> timer.record(() -> {
      waitFor(TimeUnit.MILLISECONDS, 50);
      throw new RuntimeException();
    }));

    assertThat(registry.timer(MY_TIMER).totalTime(MILLISECONDS)).isGreaterThan(50);
  }

  private void waitFor(TimeUnit timeUnit, int unit) {
    try {
      timeUnit.sleep(unit);
    } catch (InterruptedException e) {
      throw new RuntimeException();
    }
  }
}
