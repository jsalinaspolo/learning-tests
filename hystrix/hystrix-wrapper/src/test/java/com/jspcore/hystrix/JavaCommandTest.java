package com.jspcore.hystrix;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Optional;
import com.jspcore.hystrix.config.CommandConfiguration;
import com.jspcore.hystrix.config.CommandGroupConfiguration;
import com.jspcore.hystrix.config.TestCommandGroupConfiguration;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import org.assertj.guava.api.Assertions;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;

public class JavaCommandTest {

  private final CommandGroupConfiguration commandGroupConfiguration = new TestCommandGroupConfiguration();
  private final MetricRegistry metricRegistry = new MetricRegistry();

  @Test
  public void queueCommandIsSuccessful() throws Exception {
    Future<String> result = javaCommandWithFallback(Optional.absent()).queue();
    assertEquals("done", result.get());
  }

  @Test
  public void queueCommandRunFallbackWhenFail() throws Exception {
    Future<String> result = javaCommandThatFailsWithFallback(Optional.of("fallback")).queue();
    assertEquals("fallback", result.get());
  }

  @Test
  public void queueCommandWithListenerIsExecutedWhenFail() throws Exception {
    ReportingFailListener failureListener = new ReportingFailListener();

    Future<String> queue = javaCommandThatFailsWithFallback(Optional.absent()).withFailListener(failureListener)
      .queue();

    Thread.sleep(100);
    Assertions.assertThat(failureListener.fallback).isAbsent();
    assertThat(failureListener.t).isInstanceOf(HystrixRuntimeException.class).hasCause(new RuntimeException("error"));
    assertThatThrownBy(queue::get).isInstanceOf(ExecutionException.class)
      .hasCauseInstanceOf(HystrixRuntimeException.class);
  }

  @Test
  public void queueCommandWithListenerAndFallbackBothAreExecutedWhenFail() throws Exception {
    ReportingFailListener failureListener = new ReportingFailListener();

    Future<String> queue = javaCommandThatFailsWithFallback(Optional.of("fallback")).withFailListener(failureListener)
      .queue();

    Thread.sleep(100);
    Assertions.assertThat(failureListener.fallback).isEqualTo(Optional.of("fallback"));
    assertEquals("fallback", queue.get());
  }

  private static class ReportingFailListener implements FailureListener<String> {
    private Optional<String> fallback;
    private Throwable t;

    @Override
    public void onFailure(Throwable t, Optional<String> fallback) {
      this.t = t;
      this.fallback = fallback;
    }
  }

  private CommandConfiguration commandConfiguration() {
    return commandGroupConfiguration.getCommands().get(0);
  }

  private JavaCommand<String> javaCommandThatFailsWithFallback(Optional<String> fallbackValue) {
    return new JavaCommand<>(metricRegistry, commandGroupConfiguration,
      commandConfiguration(),
      () -> {
        throw new RuntimeException("error");
      },
      fallbackValue,
      Optional.absent()
    );
  }

  private JavaCommand<String> javaCommandWithFallback(Optional<String> fallbackValue) {
    return new JavaCommand<>(metricRegistry, commandGroupConfiguration,
      commandConfiguration(),
      () -> "done",
      fallbackValue,
      Optional.absent()
    );
  }
}
