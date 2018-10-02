package com.jspcore.learning.commands;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;

public class ExceptionThrowingCommand extends HystrixCommand<String> implements ReportingCommand {
  private long startTime = System.currentTimeMillis();
  private boolean runWasCalled;
  private boolean fallbackWasCalled;

  public ExceptionThrowingCommand(String commandKey) {
    super(Setter
      .withGroupKey(HystrixCommandGroupKey.Factory.asKey(GROUP_KEY))
      .andCommandKey(HystrixCommandKey.Factory.asKey(commandKey))
      .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
        .withFallbackEnabled(true)));
  }

  @Override
  protected String run() throws Exception {
    startTime = System.currentTimeMillis();
    runWasCalled = true;
    System.out.println(Thread.currentThread().getName() + " run - throwing Runtime");
    throw new RuntimeException("run fail");
  }

  @Override
  protected String getFallback() {
    fallbackWasCalled = true;
    System.out.println(
      Thread.currentThread().getName() + " : fallback. It took " + (System.currentTimeMillis() - startTime) +
        "ms to get here");
    return FALLBACK;
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
