package com.jspcore.hystrix;

public class KillSwitchWithoutFallbackException extends RuntimeException {
  public KillSwitchWithoutFallbackException(String message) {
    super(message);
  }
}
