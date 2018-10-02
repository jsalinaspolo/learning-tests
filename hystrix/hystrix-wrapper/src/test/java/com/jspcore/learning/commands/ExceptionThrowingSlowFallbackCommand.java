package com.jspcore.learning.commands;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;

public class ExceptionThrowingSlowFallbackCommand extends HystrixCommand<String> implements ReportingCommand {
  private int waitBeforeFallbackReturnsInMs;
  private boolean runWasCalled;
  private boolean fallbackWasCalled;

  public ExceptionThrowingSlowFallbackCommand(String commandKey, int waitBeforeFallbackReturnsInMs) {
    super(Setter
      .withGroupKey(HystrixCommandGroupKey.Factory.asKey(GROUP_KEY))
      .andCommandKey(HystrixCommandKey.Factory.asKey(commandKey))
      .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
        .withFallbackEnabled(true)));
    this.waitBeforeFallbackReturnsInMs = waitBeforeFallbackReturnsInMs;
  }

  @Override
  protected String run() throws Exception {
    runWasCalled = true;
    System.out.println(Thread.currentThread().getName() + " run - throwing Runtime");
    throw new RuntimeException("run fail");
  }

  @Override
  protected String getFallback() {
    fallbackWasCalled = true;
    try {
      long start = System.currentTimeMillis();
      System.out.println(start + " " + Thread.currentThread().getName() + " fallback - start - waiting for " +
        waitBeforeFallbackReturnsInMs + "ms");
      Thread.sleep(waitBeforeFallbackReturnsInMs);
      System.out.println(Thread.currentThread().getName() + " fallback - end, took: " +
        (System.currentTimeMillis() - start));

    } catch (InterruptedException e) {
      System.out.println(Thread.currentThread().getName() + "fallback interrupted");
      e.printStackTrace();
    }
    return Thread.currentThread().getName() + " fallback";
  }

  public boolean runWasCalled() {
    return runWasCalled;
  }

  public boolean fallbackWasCalled() {
    return fallbackWasCalled;
  }

  @Override
  public boolean hasCircuitBroken() {
    return getExecutionException() != null &&
      "Hystrix circuit short-circuited and is OPEN".equals(getExecutionException().getMessage());
  }
}
