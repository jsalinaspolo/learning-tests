package com.jspcore.hystrix;

import com.google.common.base.Optional;

public interface FailureListener<T> {
  void onFailure(Throwable t, Optional<T> fallback);
}
