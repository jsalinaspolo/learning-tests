package com.jspcore.hystrix.config;

import io.dropwizard.util.Duration;
import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

public class CommandManagerTest {

  private Configuration configurationMock;
  private CommandManager commandManager;

  private static final String GROUP_KEY = "testGroup";
  private static final String TEST2_COMMAND = "test2Command";
  public static final String TEST1_COMMAND = "test1Command";
  private static final String COMMAND_PREFIX = "hystrix.command.";

  @Before
  public void setup() {
    configurationMock = mock(Configuration.class);
    commandManager = new CommandManager(configurationMock);
  }

  @Test
  public void installsCommandGroup() throws Exception {
    // given

    CommandGroupConfiguration commandGroupConfiguration =
      new TestCommandGroupConfiguration(
        new CommandGroupConfiguration.ThreadpoolConfiguration(10, 20, 30),
        null,
        null
      );

    // when
    commandManager.install(commandGroupConfiguration);

    // then
    verify(configurationMock).setProperty("hystrix.threadpool." + GROUP_KEY + ".coreSize", 10);
    verify(configurationMock).setProperty("hystrix.threadpool." + GROUP_KEY + ".maxQueueSize", 20);
    verify(configurationMock).setProperty("hystrix.threadpool." + GROUP_KEY + ".queueSizeRejectionThreshold", 30);
    verifyNoMoreInteractions(configurationMock);
  }

  @Test
  public void doesNotInstallThreadPoolIfItIsNull() throws Exception {
    // given
    CommandGroupConfiguration commandGroupConfiguration =
      new TestCommandGroupConfiguration(null, null, null);

    // when
    commandManager.install(commandGroupConfiguration);

    // then
    verifyZeroInteractions(configurationMock);
  }

  @Test
  public void doesNotInstallAnyNullThreadPoolProperties() throws Exception {
    // given
    CommandGroupConfiguration commandGroupConfiguration =
      new TestCommandGroupConfiguration(
        new CommandGroupConfiguration.ThreadpoolConfiguration(null, null, null),
        null,
        null
      );

    // when
    commandManager.install(commandGroupConfiguration);

    // then
    verifyZeroInteractions(configurationMock);
  }

  @Test
  public void doesNotInstallAnyNullCommandProperties() throws Exception {
    // given
    CommandConfiguration.CircuitBreaker circuitBreaker =
      new CommandConfiguration.CircuitBreaker(null, null, null, null, null, null);
    CommandConfiguration.Metrics metrics = new CommandConfiguration.Metrics(null);
    CommandConfiguration.Execution execution = new CommandConfiguration.Execution(null, null, null);

    CommandGroupConfiguration commandGroupConfiguration =
      new TestCommandGroupConfiguration(
        null,
        new Test1CommandConfiguration(circuitBreaker, metrics, execution),
        new Test2CommandConfiguration(circuitBreaker, metrics, execution)
      );

    // when
    commandManager.install(commandGroupConfiguration);

    // then
    verifyZeroInteractions(configurationMock);
  }

  @Test
  public void installsFullCommandConfigurationFromCommandGroup() throws Exception {
    // given
    CommandConfiguration.CircuitBreaker circuitBreaker =
      new CommandConfiguration.CircuitBreaker(false, 10, Duration.milliseconds(20), 30, true, false);
    CommandConfiguration.Metrics metrics = new CommandConfiguration.Metrics(Duration.milliseconds(40));
    CommandConfiguration.Execution execution = new CommandConfiguration.Execution(CommandConfiguration.IsolationStrategy.THREAD, Duration.milliseconds(50), null);
    Test1CommandConfiguration commandConfiguration =
      new Test1CommandConfiguration(circuitBreaker, metrics, execution);
    CommandGroupConfiguration commandGroupConfiguration =
      new TestCommandGroupConfiguration(
        null,
        commandConfiguration,
        null
      );

    // when
    commandManager.install(commandGroupConfiguration);

    //then
    verify(configurationMock).setProperty(fullPropertyKey("circuitBreaker.enabled"), false);
    verify(configurationMock).setProperty(fullPropertyKey("circuitBreaker.requestVolumeThreshold"), 10);
    verify(configurationMock).setProperty(fullPropertyKey("circuitBreaker.sleepWindowInMilliseconds"), 20);
    verify(configurationMock).setProperty(fullPropertyKey("circuitBreaker.errorThresholdPercentage"), 30);
    verify(configurationMock).setProperty(fullPropertyKey("circuitBreaker.forceOpen"), true);
    verify(configurationMock).setProperty(fullPropertyKey("circuitBreaker.forceClosed"), false);
    verify(configurationMock).setProperty(fullPropertyKey("metrics.healthSnapshot.intervalInMilliseconds"), 40);
    verify(configurationMock).setProperty(fullPropertyKey("execution.isolation.strategy"), CommandConfiguration.IsolationStrategy.THREAD);
    verify(configurationMock).setProperty(fullPropertyKey("execution.isolation.thread.timeoutInMilliseconds"), 50);
    verifyNoMoreInteractions(configurationMock);
  }

  @Test
  public void installsCommandConfigurationWithNullCircuitBreakerNullMetricsAndNullExecution() throws Exception {
    // given
    CommandGroupConfiguration commandGroupConfiguration =
      new TestCommandGroupConfiguration(
        null,
        new Test1CommandConfiguration(null, null, null),
        null
      );

    // when
    commandManager.install(commandGroupConfiguration);

    //then
    verifyZeroInteractions(configurationMock);
  }

  @Test
  public void installsCommandConfigurationOfAllCommandsInCommandGroup() throws Exception {
    // given
    CommandGroupConfiguration commandGroupConfiguration =
      new TestCommandGroupConfiguration(
        null,
        new Test1CommandConfiguration(null, new CommandConfiguration.Metrics(Duration.milliseconds(387)), null),
        new Test2CommandConfiguration(null, null, new CommandConfiguration.Execution(CommandConfiguration.IsolationStrategy.THREAD, Duration.milliseconds(836), null))
      );
    // when
    commandManager.install(commandGroupConfiguration);

    //then
    verify(configurationMock)
      .setProperty(fullPropertyKey("metrics.healthSnapshot.intervalInMilliseconds"), 387);
    verify(configurationMock).setProperty(fullPropertyKey("execution.isolation.strategy", TEST2_COMMAND), CommandConfiguration.IsolationStrategy.THREAD);
    verify(configurationMock)
      .setProperty(fullPropertyKey("execution.isolation.thread.timeoutInMilliseconds",
        TEST2_COMMAND), 836);
    verifyNoMoreInteractions(configurationMock);
  }

  @Test
  public void doesNotInstallCommandGroupConfigurationPropertiesIfAllOfThemAreNull() throws Exception {
    // given
    CommandGroupConfiguration commandGroupConfiguration =
      new TestCommandGroupConfiguration(null, null, null);

    // when
    commandManager.install(commandGroupConfiguration);

    //then
    verifyZeroInteractions(configurationMock);
  }

  @Test
  public void doesInstallCommandConfigurationForSemaphoreStrategy() throws Exception {
    // given
    CommandGroupConfiguration commandGroupConfiguration =
      new TestCommandGroupConfiguration(
        null,
        new Test1CommandConfiguration(null, null, new CommandConfiguration.Execution(CommandConfiguration.IsolationStrategy.THREAD, Duration.milliseconds(100), 0)),
        new Test2CommandConfiguration(null, null, new CommandConfiguration.Execution(CommandConfiguration.IsolationStrategy.SEMAPHORE, Duration.milliseconds(0), 200)));

    // when
    commandManager.install(commandGroupConfiguration);

    //then
    verify(configurationMock).setProperty(fullPropertyKey("execution.isolation.strategy", TEST1_COMMAND), CommandConfiguration.IsolationStrategy.THREAD);
    verify(configurationMock)
      .setProperty(fullPropertyKey("execution.isolation.thread.timeoutInMilliseconds",
        TEST1_COMMAND), 100);
    verify(configurationMock)
      .setProperty(fullPropertyKey("execution.isolation.semaphore.maxConcurrentRequests",
        TEST1_COMMAND), 0);

    verify(configurationMock).setProperty(fullPropertyKey("execution.isolation.strategy", TEST2_COMMAND), CommandConfiguration.IsolationStrategy.SEMAPHORE);
    verify(configurationMock)
      .setProperty(fullPropertyKey("execution.isolation.thread.timeoutInMilliseconds",
        TEST2_COMMAND), 0);
    verify(configurationMock)
      .setProperty(fullPropertyKey("execution.isolation.semaphore.maxConcurrentRequests",
        TEST2_COMMAND), 200);

    verifyNoMoreInteractions(configurationMock);
  }

  private String fullPropertyKey(String keySuffix) {
    return fullPropertyKey(keySuffix, TEST1_COMMAND);
  }

  private String fullPropertyKey(String keySuffix, String commandKey) {
    return COMMAND_PREFIX + commandKey + "." + keySuffix;
  }

}
