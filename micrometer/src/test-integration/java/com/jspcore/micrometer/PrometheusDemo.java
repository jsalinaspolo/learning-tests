package com.jspcore.micrometer;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.junit.Test;

import java.time.Duration;

public class PrometheusDemo {

  private static final String MY_COUNTER = "my.counter";
  private static final String MY_TIMER = "my.timer";
  private final CompositeMeterRegistry registry = new CompositeMeterRegistry();

  private void metricsJVMandSystem() {
    new ClassLoaderMetrics().bindTo(registry);
    new JvmMemoryMetrics().bindTo(registry);
    new JvmGcMetrics().bindTo(registry);
    new ProcessorMetrics().bindTo(registry);
    new JvmThreadMetrics().bindTo(registry);
  }

  @Test
  public void increments() throws InterruptedException {
    registry.add(new PrometheusMeterRegistry(PrometheusConfig.DEFAULT));
    new Application().start(registry);
    metricsJVMandSystem();

    Counter counter = Counter.builder(MY_COUNTER).register(registry);
    Timer timer = Timer.builder(MY_TIMER)
      .publishPercentileHistogram()
      .publishPercentiles(0.50, 0.95, 0.99)
      .register(registry);
    while (true) {
      Thread.sleep(10000);
      counter.increment();
      timer.record(Duration.ofSeconds(1));
    }
  }
}
