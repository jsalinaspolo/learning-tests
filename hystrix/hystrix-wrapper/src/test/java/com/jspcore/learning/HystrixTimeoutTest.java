package com.jspcore.learning;

import com.jspcore.learning.commands.DelayedCommand;
import com.jspcore.learning.commands.ExceptionThrowingCommand;
import com.jspcore.learning.commands.ReportingCommand;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.jspcore.learning.commands.ReportingCommand.FALLBACK;
import static com.jspcore.learning.commands.ReportingCommand.SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class HystrixTimeoutTest {
  private String commandKey;

  @Before
  public void initialise() {
    commandKey = Utils.uniqueCommandKey();
    Utils.hystrixReset();
    Utils.setTimeout(commandKey, Utils.HYSTRIX_TIMEOUT);
  }

  @Test
  public void hystrixCallsRunOnExecuteWhenThereIsEnoughTime() throws Exception {
    ReportingCommand command = new DelayedCommand(commandKey, 1);

    String result = command.execute();

    assertThat(command.runWasCalled()).isTrue();
    assertThat(command.fallbackWasCalled()).isFalse();

    assertThat(result).isEqualTo(SUCCESS);
  }

  @Test
  public void hystrixCallsRunOnQueueWhenThereIsEnoughTime() throws Exception {
    ReportingCommand command = new DelayedCommand(commandKey, 1);

    Future<String> result = command.queue();

    assertThat(result.get()).isEqualTo(SUCCESS);

    assertThat(command.runWasCalled()).isTrue();
    assertThat(command.fallbackWasCalled()).isFalse();
  }

  @Test
  public void hystrixCallsFallbackOnExecuteTimeout() throws Exception {
    ReportingCommand command = new DelayedCommand(commandKey, 100);

    String result = command.execute();

    assertThat(command.runWasCalled()).isTrue();
    assertThat(command.fallbackWasCalled()).isTrue();

    assertThat(result).isEqualTo(FALLBACK);
  }

  @Test
  public void hystrixCallsFallbackOnQueueTimeoutWhenGetWasNotCalledOnTheFuture() throws Exception {
    ReportingCommand command = new DelayedCommand(commandKey, 100);

    command.queue();

    Thread.sleep(Utils.HYSTRIX_TIMEOUT + 10);

    assertThat(command.runWasCalled()).isTrue();
    assertThat(command.fallbackWasCalled()).isTrue();
  }

  @Test
  public void hystrixCallsFallbackOnQueueTimeoutAsLongAsYouCallGetOnTheFuture() throws Exception {
    ReportingCommand command = new DelayedCommand(commandKey, 100);

    Future<String> result = command.queue();

    assertThat(result.get()).isEqualTo(FALLBACK);

    assertThat(command.runWasCalled()).isTrue();
    assertThat(command.fallbackWasCalled()).isTrue();
  }

  @Test
  public void hystrixThrowsExceptionWhenFutureTimeoutIsBelowHystrixTimeoutAndCommandTakesLonger() throws Exception {
    Utils.hystrixReset();
    Utils.setTimeout(commandKey, 200);

    ReportingCommand command = new DelayedCommand(commandKey, 500);

    Future<String> futureResult = command.queue();

    Throwable thrown = catchThrowable(() -> {
      String result = futureResult.get(10, TimeUnit.MILLISECONDS);
      System.out.println("result: " + result);
    });
    assertThat(thrown)
      .isInstanceOf(TimeoutException.class)
      .hasMessage("Timed out after 10ms waiting for underlying Observable.");

    assertThat(command.runWasCalled()).isTrue();
    assertThat(command.fallbackWasCalled()).isFalse();
  }

  @Test
  public void callingFutureGetWithTimeoutButNotWaitingForHystrixTimeoutLeavesTheThreadHanging() throws Exception {
    Utils.hystrixReset();
    Utils.setTimeout(commandKey, 50);

    ReportingCommand command = new DelayedCommand(commandKey, 200);

    Throwable thrown = catchThrowable(() -> {
      Future<String> futureResult = command.queue();
      String result = futureResult.get(10, TimeUnit.MILLISECONDS);
      System.out.println("result: " + result);
    });
    assertThat(thrown)
      .isInstanceOf(TimeoutException.class)
      .hasMessage("Timed out after 10ms waiting for underlying Observable.");

    assertThat(command.runWasCalled()).isTrue();
    assertThat(command.isExecutionComplete()).isFalse();
  }

  @Test
  public void normalHystrixBehaviourWithHappyPathWithNoTimeoutOnGet() throws Exception {
    Utils.hystrixReset();
    Utils.setTimeout(commandKey, 20);

    ReportingCommand command = new DelayedCommand(commandKey, 10);

    Future<String> futureResult = command.queue();

    assertThat(futureResult.get()).isEqualTo(SUCCESS);
    assertThat(command.runWasCalled()).isTrue();
    assertThat(command.fallbackWasCalled()).isFalse();
  }

  @Test
  public void normalHystrixBehaviourWithFallbackWithNoTimeoutOnGet() throws Exception {
    Utils.hystrixReset();
    Utils.setTimeout(commandKey, 20);

    ReportingCommand command = new DelayedCommand(commandKey, 50);

    Future<String> futureResult = command.queue();

    assertThat(futureResult.get()).isEqualTo(FALLBACK);
    assertThat(command.runWasCalled()).isTrue();
    assertThat(command.fallbackWasCalled()).isTrue();
  }

  @Test
  public void hystrixCallsFallbackOnExecuteFailure() throws Exception {
    ReportingCommand command = new ExceptionThrowingCommand(commandKey);

    String result = command.execute();

    assertThat(command.runWasCalled()).isTrue();
    assertThat(command.fallbackWasCalled()).isTrue();

    assertThat(result).isEqualTo(FALLBACK);
  }

  @Test
  public void hystrixCallsFallbackOnQueueFailure() throws Exception {
    ReportingCommand command = new ExceptionThrowingCommand(commandKey);

    Future<String> result = command.queue();

    assertThat(result.get()).isEqualTo(FALLBACK);

    assertThat(command.runWasCalled()).isTrue();
    assertThat(command.fallbackWasCalled()).isTrue();
  }

  @Test
  public void hystrixCallsFallbackOnQueueFailureEvenWhenGetIsNotCalledOnTheFuture() throws Exception {
    ReportingCommand command = new ExceptionThrowingCommand(commandKey);

    command.queue();

    Thread.sleep(Utils.HYSTRIX_TIMEOUT + 10);

    assertThat(command.runWasCalled()).isTrue();
    assertThat(command.fallbackWasCalled()).isTrue();
  }
}
