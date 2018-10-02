package com.jspcore.hystrix.config;

public class Test1CommandConfiguration extends CommandConfiguration {
  public Test1CommandConfiguration() {
    super("test1Command");
  }

  public Test1CommandConfiguration(CircuitBreaker circuitBreaker, Metrics metrics, Execution execution) {
    super("test1Command", circuitBreaker, metrics, execution);
  }
}
