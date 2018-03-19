package com.jspcore.redis.lettuce;

import org.awaitility.core.ConditionTimeoutException;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.await;
import static org.awaitility.Duration.FIVE_HUNDRED_MILLISECONDS;

public class RedisLettuceAtomicCommandsShould extends AbstractCommandTest {

  private CountDownLatch countDownLatch = new CountDownLatch(1);

  @Test
  public void have_race_condition_when_get_and_set_as_non_atomic_operation() throws InterruptedException {
    commands.set("foo", "1");

    ExecutorService executorService = Executors.newFixedThreadPool(2);
    executorService.submit(raceConditionOperation());
    executorService.submit(raceConditionOperation());

    executorService.awaitTermination(1, TimeUnit.SECONDS);
    countDownLatch.countDown();

    assertThatThrownBy(() ->
      await().atMost(FIVE_HUNDRED_MILLISECONDS).untilAsserted(() -> assertThat(commands.get("foo")).isEqualTo("3"))
    ).isInstanceOf(ConditionTimeoutException.class);
  }

  @Test
  public void increment_for_atomic_operation() throws InterruptedException {
    commands.set("foo", "1");

    ExecutorService executorService = Executors.newFixedThreadPool(2);
    executorService.submit(atomicOperation());
    executorService.submit(atomicOperation());

    executorService.awaitTermination(1, TimeUnit.SECONDS);
    countDownLatch.countDown();

    await().atMost(FIVE_HUNDRED_MILLISECONDS).untilAsserted(() -> assertThat(commands.get("foo")).isEqualTo("3"));
  }

  private Runnable raceConditionOperation() {
    return () -> {
      try {
        String value = commands.get("foo");
        countDownLatch.await();
        commands.set("foo", String.valueOf(Integer.valueOf(value) + 1));
      } catch (InterruptedException e) {
        throw new IllegalStateException(e);
      }
    };
  }

  private Runnable atomicOperation() {
    return () -> {
      try {
        countDownLatch.await();
        commands.incr("foo");
      } catch (InterruptedException e) {
        throw new IllegalStateException(e);
      }
    };
  }
}
