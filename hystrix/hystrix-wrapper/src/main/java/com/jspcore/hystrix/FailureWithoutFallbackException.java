package com.jspcore.hystrix;

import com.google.common.base.Optional;

public class FailureWithoutFallbackException extends RuntimeException {
  public FailureWithoutFallbackException(Exception e) {
    super("Failure occurred and no fallback was defined: " + e.getMessage(), e);
  }

  public <T extends Throwable> Optional<T> causeByType(Class<T> klass) {
    Throwable nextCause = this.getCause();
    while (nextCause != null) {
      if (nextCause.getClass() == klass) {
        return Optional.of((T) nextCause);
      }
      nextCause = nextCause.getCause();
    }
    return Optional.absent();
  }

  public <T extends Throwable> boolean hasCauseByType(Class<T> klass) {
    return causeByType(klass).isPresent();
  }
}
