package com.jspcore.hystrix;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Optional;
import com.jspcore.hystrix.config.CommandConfiguration;
import com.jspcore.hystrix.config.CommandGroupConfiguration;

public class JavaCommand<T> extends Command<T> {
  protected Action<T> action;

  JavaCommand(MetricRegistry metricRegistry,
              CommandGroupConfiguration client,
              CommandConfiguration command,
              Action<T> action) {
    this(metricRegistry,
      client,
      command,
      action,
      Optional.<T>absent(),
      Optional.<FailureListener<T>>absent());
  }

  JavaCommand(MetricRegistry metricRegistry,
              CommandGroupConfiguration client,
              CommandConfiguration command,
              Action<T> action,
              Optional<T> fallbackValue,
              Optional<FailureListener<T>> failureListener) {
    super(metricRegistry, client, command, fallbackValue, failureListener);
    this.action = action;
  }

  @Override
  protected void onFailure(Throwable failedExecutionException, Optional<T> result) {
    if (failureListener.isPresent()) {
      failureListener.get().onFailure(failedExecutionException, result);
    }
  }

  @Override
  protected T runAction() throws Exception {
    return action.run();
  }

  @Override
  public Command<T> withFallbackTo(T fallbackValue) {
    return new JavaCommand<T>(metricRegistry,
      client,
      command,
      action,
      Optional.of(fallbackValue),
      failureListener);
  }

  @Override
  public Command<T> withFailListener(FailureListener<T> failureListener) {
    return new JavaCommand<T>(
      metricRegistry,
      client,
      command,
      action,
      fallbackValue,
      Optional.of(failureListener));
  }

}
