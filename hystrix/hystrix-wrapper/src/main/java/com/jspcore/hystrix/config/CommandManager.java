package com.jspcore.hystrix.config;

import org.apache.commons.configuration.Configuration;

import java.util.List;

public class CommandManager {
  private final Configuration hystrixConfiguration;
  public final static boolean PREFIX_COMMAND_NAMES_WITH_GROUP_KEY = false;

  public CommandManager(Configuration hystrixConfiguration) {
    this.hystrixConfiguration = hystrixConfiguration;
  }

  public void install(CommandGroupConfiguration configuration) {
    CommandGroupConfiguration.ThreadpoolConfiguration threadPool = configuration.getThreadPool();
    if (threadPool != null) {
      installThreadPoolProperty(hystrixConfiguration, "coreSize",
        threadPool.getCoreSize(),
        configuration.getGroupKey());
      installThreadPoolProperty(hystrixConfiguration, "maxQueueSize",
        threadPool.getMaxQueueSize(),
        configuration.getGroupKey());
      installThreadPoolProperty(hystrixConfiguration, "queueSizeRejectionThreshold",
        threadPool.getQueueSizeRejectionThreshold(),
        configuration.getGroupKey());
    }
    List<CommandConfiguration> commandConfigurations = configuration.getCommands();
    if (commandConfigurations != null) {
      for (CommandConfiguration commandConfiguration : commandConfigurations) {
        install(configuration, commandConfiguration);
      }
    }
  }

  private void install(CommandGroupConfiguration groupConfiguration, CommandConfiguration commandConfiguration) {
    if (commandConfiguration.getCircuitBreaker() != null) {
      installCommandProperty(hystrixConfiguration, "circuitBreaker.enabled",
        commandConfiguration.getCircuitBreaker().getEnabled(),
        groupConfiguration.getGroupKey(),
        commandConfiguration.getCommandKey());
      installCommandProperty(hystrixConfiguration, "circuitBreaker.requestVolumeThreshold",
        commandConfiguration.getCircuitBreaker().getRequestVolumeThreshold(),
        groupConfiguration.getGroupKey(),
        commandConfiguration.getCommandKey());
      if (commandConfiguration.getCircuitBreaker().getSleepWindow() != null) {
        installCommandProperty(hystrixConfiguration, "circuitBreaker.sleepWindowInMilliseconds",
          (int) commandConfiguration.getCircuitBreaker().getSleepWindow().toMilliseconds(),
          groupConfiguration.getGroupKey(),
          commandConfiguration.getCommandKey());
      }
      installCommandProperty(hystrixConfiguration, "circuitBreaker.errorThresholdPercentage",
        commandConfiguration.getCircuitBreaker().getErrorThresholdPercentage(),
        groupConfiguration.getGroupKey(),
        commandConfiguration.getCommandKey());
      installCommandProperty(hystrixConfiguration, "circuitBreaker.forceOpen",
        commandConfiguration.getCircuitBreaker().getForceOpen(),
        groupConfiguration.getGroupKey(),
        commandConfiguration.getCommandKey());
      installCommandProperty(hystrixConfiguration, "circuitBreaker.forceClosed",
        commandConfiguration.getCircuitBreaker().getForceClosed(),
        groupConfiguration.getGroupKey(),
        commandConfiguration.getCommandKey());
    }

    if (commandConfiguration.getMetrics() != null
      && commandConfiguration.getMetrics().getHealthSnapshotInterval() != null) {
      installCommandProperty(hystrixConfiguration, "metrics.healthSnapshot.intervalInMilliseconds",
        (int) commandConfiguration.getMetrics().getHealthSnapshotInterval().toMilliseconds(),
        groupConfiguration.getGroupKey(),
        commandConfiguration.getCommandKey());
    }

    if (commandConfiguration.getExecution() != null) {
      installCommandProperty(hystrixConfiguration, "execution.isolation.strategy",
        commandConfiguration.getExecution().getIsolationStrategy(),
        groupConfiguration.getGroupKey(),
        commandConfiguration.getCommandKey());
      if (commandConfiguration.getExecution().getIsolationThreadTimeout() != null) {
        installCommandProperty(hystrixConfiguration, "execution.isolation.thread.timeoutInMilliseconds",
          (int) commandConfiguration.getExecution().getIsolationThreadTimeout().toMilliseconds(),
          groupConfiguration.getGroupKey(),
          commandConfiguration.getCommandKey());
      }
      installCommandProperty(hystrixConfiguration, "execution.isolation.semaphore.maxConcurrentRequests",
        commandConfiguration.getExecution().getIsolationSemaphoreMaxConcurrentRequests(),
        groupConfiguration.getGroupKey(),
        commandConfiguration.getCommandKey());
    }
  }

  private void installThreadPoolProperty(Configuration configuration, String name, Object property, String groupKey) {
    if (property != null) {
      configuration.setProperty(
        "hystrix.threadpool." + groupKey + "." + name,
        property
      );
    }
  }

  private void installCommandProperty(Configuration configuration, String name, Object property, String groupKey, String commandKey) {
    if (property != null) {
      configuration.setProperty(
        "hystrix.command." + commandKey(groupKey, commandKey) + "." + name,
        property
      );
    }
  }


  public static String commandKey(String groupKey, String commandKey) {
    if (PREFIX_COMMAND_NAMES_WITH_GROUP_KEY) {
      return new StringBuilder(groupKey).append("_").append(commandKey).toString();
    } else {
      return commandKey;
    }
  }
}
