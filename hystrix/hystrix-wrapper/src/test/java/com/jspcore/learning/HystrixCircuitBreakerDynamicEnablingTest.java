package com.jspcore.learning;

import com.jspcore.learning.commands.ExceptionThrowingCommand;
import com.jspcore.learning.commands.ReportingCommand;
import com.jspcore.learning.commands.SimpleCommand;
import org.junit.Before;
import org.junit.Test;

import static com.jspcore.learning.Utils.HYSTRIX_TIMEOUT;
import static com.jspcore.learning.Utils.hystrixReset;
import static com.jspcore.learning.Utils.setConfig;
import static com.jspcore.learning.Utils.setThresholdsToCircuitBreakAfterOneFailure;
import static com.jspcore.learning.Utils.setTimeout;
import static com.jspcore.learning.Utils.uniqueCommandKey;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class HystrixCircuitBreakerDynamicEnablingTest {
  private static final int SLEEP_WINDOW = 500;
  private String commandKey;
  public static final int CHECK_INTERVAL = 10;
  public static final int METRIC_RESET_WINDOW = 200;

  @Before
  public void initialise() {
    commandKey = uniqueCommandKey();
    hystrixReset();
    setTimeout(commandKey, HYSTRIX_TIMEOUT);

    setThresholdsToCircuitBreakAfterOneFailure(commandKey);
    setConfig("hystrix.command." + commandKey + ".circuitBreaker.sleepWindowInMilliseconds", SLEEP_WINDOW);
    setConfig("hystrix.command." + commandKey + ".metrics.healthSnapshot.intervalInMilliseconds", CHECK_INTERVAL);
    setConfig("hystrix.command." + commandKey + ".metrics.rollingStats.timeInMilliseconds", METRIC_RESET_WINDOW);
  }

  @Test
  public void enabledCircuitBreakerCanBeDisabled() throws Exception {
    assertCircuitBreakerWorks();

    setConfig("hystrix.command." + commandKey + ".circuitBreaker.enabled", false);

    assertCircuitBreakerDoesNotWork();
  }

  @Test
  public void enabledCircuitBreakerCanBeDisabledThenEnabledAgain() throws Exception {
    assertCircuitBreakerWorks();

    setConfig("hystrix.command." + commandKey + ".circuitBreaker.enabled", false);

    assertCircuitBreakerDoesNotWork();

    setConfig("hystrix.command." + commandKey + ".circuitBreaker.enabled", true);
    assertCircuitBreakerWorks();
  }

  @Test
  public void disabledCircuitBreakerCanBeEnabled() throws Exception {
    setConfig("hystrix.command." + commandKey + ".circuitBreaker.enabled", false);
    assertCircuitBreakerDoesNotWork();

    setConfig("hystrix.command." + commandKey + ".circuitBreaker.enabled", true);
    assertCircuitBreakerWorks();
  }

  @Test
  public void disabledCircuitBreakerCanBeEnabledThenDisabledAgain() throws Exception {
    setConfig("hystrix.command." + commandKey + ".circuitBreaker.enabled", false);
    assertCircuitBreakerDoesNotWork();

    setConfig("hystrix.command." + commandKey + ".circuitBreaker.enabled", true);
    assertCircuitBreakerWorks();

    setConfig("hystrix.command." + commandKey + ".circuitBreaker.enabled", false);
    assertCircuitBreakerDoesNotWork();
  }

  private void assertCircuitBreakerWorks() throws InterruptedException {
    System.out.println("***************** assertCircuitBreakerWorks");

    ReportingCommand command = new ExceptionThrowingCommand(commandKey);
    command.execute();
    assertFalse(command.hasCircuitBroken());
    assertTrue(command.runWasCalled());

    Thread.sleep(CHECK_INTERVAL + 30);
    command = new ExceptionThrowingCommand(commandKey);
    command.execute();

    assertTrue(command.hasCircuitBroken());
    assertFalse(command.runWasCalled());

    //wait to switches off
    Thread.sleep(SLEEP_WINDOW + 30);

    command = new SimpleCommand(commandKey);
    command.execute();
    assertFalse(command.hasCircuitBroken());
    assertTrue(command.runWasCalled());

    Thread.sleep(METRIC_RESET_WINDOW + 30);
  }

  private void assertCircuitBreakerDoesNotWork() throws InterruptedException {
    System.out.println("***************** assertCircuitBreakerDoesNotWork");

    ReportingCommand command = new ExceptionThrowingCommand(commandKey);
    command.execute();
    assertTrue(command.runWasCalled());

    Thread.sleep(CHECK_INTERVAL + 30);

    command = new ExceptionThrowingCommand(commandKey);
    command.execute();
    assertTrue(command.runWasCalled());

    //wait to switches off
    Thread.sleep(SLEEP_WINDOW + 30);

    command = new SimpleCommand(commandKey);
    command.execute();
    assertTrue(command.runWasCalled());

    Thread.sleep(METRIC_RESET_WINDOW + 30);
  }
}
