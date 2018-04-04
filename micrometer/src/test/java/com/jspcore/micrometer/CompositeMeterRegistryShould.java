package com.jspcore.micrometer;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class CompositeMeterRegistryShould {

  private static final String MY_COUNTER = "my.counter";
  private final CompositeMeterRegistry registry = new CompositeMeterRegistry();

  @Test
  public void increments_are_NOOP_untile_there_is_registry_in_composite() {
    Counter counter = Counter.builder(MY_COUNTER).register(registry);
    counter.increment();

    SimpleMeterRegistry simpleRegistry = new SimpleMeterRegistry();
    registry.add(simpleRegistry);
    counter.increment();

    assertThat(registry.counter(MY_COUNTER).count()).isEqualTo(2);
    assertThat(simpleRegistry.counter(MY_COUNTER).count()).isEqualTo(1);
  }
}
