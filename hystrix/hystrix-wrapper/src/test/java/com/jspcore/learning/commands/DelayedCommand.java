package com.jspcore.learning.commands;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;

public class DelayedCommand extends HystrixCommand<String> implements ReportingCommand {
  private int waitBeforeReturnInMs;
  private boolean runWasCalled;
  private boolean fallbackWasCalled;

  public DelayedCommand(String commandKey, int waitBeforeReturnInMs) {
    this(GROUP_KEY, commandKey, waitBeforeReturnInMs);
  }

  public DelayedCommand(String groupKey, String commandKey, int waitBeforeReturnInMs) {
    super(Setter
      .withGroupKey(HystrixCommandGroupKey.Factory.asKey(groupKey))
      .andCommandKey(HystrixCommandKey.Factory.asKey(commandKey))
      .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
        .withFallbackEnabled(true)));
    this.waitBeforeReturnInMs = waitBeforeReturnInMs;
  }

  @Override
  protected String run() throws Exception {
    runWasCalled = true;
    System.out.println(
      Thread.currentThread().getName() + " run - start - waiting for " + waitBeforeReturnInMs + "ms");
    Thread.sleep(waitBeforeReturnInMs);
    System.out.println(Thread.currentThread().getName() + " run - end");
    return SUCCESS;
  }

  @Override
  protected String getFallback() {
    fallbackWasCalled = true;
    System.out.println(Thread.currentThread().getName() + " : fallback");
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
