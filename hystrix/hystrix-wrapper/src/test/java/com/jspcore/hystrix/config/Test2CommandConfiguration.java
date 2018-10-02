package com.jspcore.hystrix.config;

public class Test2CommandConfiguration extends CommandConfiguration {
  public Test2CommandConfiguration() {
    super("test2Command");
  }

  public Test2CommandConfiguration(CommandConfiguration.CircuitBreaker circuitBreaker, CommandConfiguration.Metrics metrics, CommandConfiguration.Execution execution) {
    super("test2Command", circuitBreaker, metrics, execution);
  }
}
