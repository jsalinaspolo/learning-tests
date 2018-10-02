package com.jspcore.learning;

import com.jspcore.learning.commands.SimpleCommand;
import com.netflix.config.ConfigurationManager;
import com.netflix.hystrix.HystrixCommand;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class ArchaiusHystrixPropertiesTest {
  private String commandKey;

  @Before
  public void reset() {
    commandKey = Utils.uniqueCommandKey();
    Utils.hystrixReset();
  }

  @Test
  public void hystrixPropertyCanBeSet() throws Exception {
    Utils.setConfig(isolationThreadTimeoutPropertyName(), 1);

    HystrixCommand<String> command = new SimpleCommand(commandKey);

    assertEquals(1, command.getProperties().executionTimeoutInMilliseconds().get().intValue());
  }

  @Test
  public void hystrixPropertyRevertsToDefaultWhenTryingToOverride() throws Exception {
    Utils.setConfig(isolationThreadTimeoutPropertyName(), 1);
    Utils.setConfig(isolationThreadTimeoutPropertyName(), 2);

    HystrixCommand<String> command = new SimpleCommand(commandKey);

    int defaultTimeout = 1000;
    assertEquals(defaultTimeout, command.getProperties().executionTimeoutInMilliseconds().get().intValue());
  }

  @Test
  public void hystrixPropertyCanBeOverriddenIfItIsClearedInBetweenSetAttempts() throws Exception {
    Utils.setConfig(isolationThreadTimeoutPropertyName(), 1);
    ConfigurationManager.getConfigInstance().clearProperty(isolationThreadTimeoutPropertyName());
    Utils.setConfig(isolationThreadTimeoutPropertyName(), 2);

    HystrixCommand<String> command = new SimpleCommand(commandKey);

    assertEquals(2, command.getProperties().executionTimeoutInMilliseconds().get().intValue());
  }

  @Test
  public void commandPropertiesOfCommandsWithTheSameNameInDifferentCommandGroupsAreNotIndependent() throws Exception {
    HystrixCommand<String> command1 = new SimpleCommand("firstGroup", commandKey);
    HystrixCommand<String> command2 = new SimpleCommand("secondGroup", commandKey);

    Utils.setConfig(isolationThreadTimeoutPropertyName(), 1);

    assertEquals(1, command1.getProperties().executionTimeoutInMilliseconds().get().intValue());
    assertEquals(1, command2.getProperties().executionTimeoutInMilliseconds().get().intValue());
    assertEquals(
      command1.getProperties().executionTimeoutInMilliseconds().get().intValue(),
      command2.getProperties().executionTimeoutInMilliseconds().get().intValue()
    );
  }

  private String isolationThreadTimeoutPropertyName() {
    return "hystrix.command." + commandKey + ".execution.isolation.thread.timeoutInMilliseconds";
  }
}
