package com.jspcore.learning;

import com.jspcore.learning.commands.DelayedCommand;
import com.jspcore.learning.commands.ExceptionThrowingCommand;
import com.jspcore.learning.commands.ReportingCommand;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class HystrixCircuitBreakerCheckIntervalTest {
  private String commandKey;

  @Before
  public void initialise() {
    commandKey = Utils.uniqueCommandKey();
    Utils.hystrixReset();
    Utils.setTimeout(commandKey, Utils.HYSTRIX_TIMEOUT);
  }

  @Test
  public void callDoesNotPassThroughWhenCheckIntervalPassedAfterTheCircuitShouldHaveOpened() throws Exception {
    //given
    Utils.setThresholdsToCircuitBreakAfterOneFailure(commandKey);

    int checkInterval = 10;
    Utils.setConfig("hystrix.command." + commandKey + ".metrics.healthSnapshot.intervalInMilliseconds", checkInterval);

    ReportingCommand command = new ExceptionThrowingCommand(commandKey);
    command.execute();
    assertTrue(command.runWasCalled());

    Thread.sleep(checkInterval + 30);

    //when
    command = new ExceptionThrowingCommand(commandKey);
    command.execute();

    //then
    command = new ExceptionThrowingCommand(commandKey);
    command.execute();
    assertFalse(command.runWasCalled());
  }

  @Test
  public void callPassesThroughWhenCheckIntervalHasNotPassedYetAfterTheCircuitShouldHaveOpened() throws Exception {
    //given
    Utils.setThresholdsToCircuitBreakAfterOneFailure(commandKey);

    int checkInterval = 50;
    Utils.setConfig("hystrix.command." + commandKey + ".metrics.healthSnapshot.intervalInMilliseconds", checkInterval);

    ReportingCommand command = new ExceptionThrowingCommand(commandKey);
    command.execute();
    assertTrue(command.runWasCalled());

    Thread.sleep(checkInterval - 40);

    //when
    command = new ExceptionThrowingCommand(commandKey);
    command.execute();

    //then
    command = new ExceptionThrowingCommand(commandKey);
    command.execute();
    assertTrue(command.runWasCalled());
  }

  @Test
  public void checkIntervalCanNotBeOverriddenInTheSameJVMEvenAfterHystrixReset() throws Exception {
    Utils.setThresholdsToCircuitBreakAfterOneFailure(commandKey);
    Utils.setConfig("hystrix.command." + commandKey + ".metrics.healthSnapshot.intervalInMilliseconds", 10);

    ReportingCommand command = new ExceptionThrowingCommand(commandKey);
    command.execute();
    assertTrue(command.runWasCalled());
    Thread.sleep(50);
    command = new ExceptionThrowingCommand(commandKey);
    command.execute();
    assertFalse(command.runWasCalled());

    Utils.hystrixReset();
    Utils.setTimeout(commandKey, Utils.HYSTRIX_TIMEOUT);
    Utils.setThresholdsToCircuitBreakAfterOneFailure(commandKey);

    Utils.setConfig("hystrix.command." + commandKey + ".metrics.healthSnapshot.intervalInMilliseconds", 100);

    command = new ExceptionThrowingCommand(commandKey);
    command.execute();
    assertTrue(command.runWasCalled());
    Thread.sleep(50);
    command = new ExceptionThrowingCommand(commandKey);
    command.execute();
    assertFalse(command.runWasCalled());
  }

  @Test
  public void forceOpenOpensTheCircuitAndRunOnCommandNotCalled() {
    Utils.setConfig("hystrix.command." + commandKey + ".circuitBreaker.forceOpen", true);
    ReportingCommand command = new DelayedCommand(commandKey, 1);

    command.execute();

    assertFalse(command.runWasCalled());
    assertTrue(command.fallbackWasCalled());
  }
}
