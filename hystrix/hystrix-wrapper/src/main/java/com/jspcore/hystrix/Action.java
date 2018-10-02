package com.jspcore.hystrix;

public interface Action<T> {
  T run();
}
