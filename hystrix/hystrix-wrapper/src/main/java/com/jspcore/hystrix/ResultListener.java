package com.jspcore.hystrix;

public interface ResultListener<T> {
  void onResult(T successValue);
}
