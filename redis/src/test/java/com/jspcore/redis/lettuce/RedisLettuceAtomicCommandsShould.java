package com.jspcore.redis.lettuce;

import org.awaitility.core.ConditionTimeoutException;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.await;
import static org.awaitility.Duration.ONE_SECOND;

public class RedisLettuceAtomicCommandsShould extends AbstractCommandTest {

  CountDownLatch countDownLatch = new CountDownLatch(1);

  @Test
  public void have_race_condition_when_get_and_set_as_non_atomic_operation() {
    commands.set("foo", "1");

    Thread thread1 = raceConditionOperation();
    Thread thread2 = raceConditionOperation();

    thread1.start();
    thread2.start();

    countDownLatch.countDown();

    assertThatThrownBy(() ->
      await().atMost(ONE_SECOND).untilAsserted(() -> assertThat(commands.get("foo")).isEqualTo("3"))
    ).isInstanceOf(ConditionTimeoutException.class);
  }

  @Test
  public void increment_for_atomic_operation() {
    commands.set("foo", "1");

    Thread thread1 = atomicOperation();
    Thread thread2 = atomicOperation();

    thread1.start();
    thread2.start();

    countDownLatch.countDown();

    await().atMost(ONE_SECOND).untilAsserted(() -> assertThat(commands.get("foo")).isEqualTo("3"));
  }

  private Thread raceConditionOperation() {
    return new Thread(() -> {
      try {
        String value = commands.get("foo");
        countDownLatch.await();
        commands.set("foo", String.valueOf(Integer.valueOf(value) + 1));
      } catch (InterruptedException e) {
        throw new IllegalStateException(e);
      }
    });
  }

  private Thread atomicOperation() {
    return new Thread(() -> {
      try {
        countDownLatch.await();
        commands.incr("foo");
      } catch (InterruptedException e) {
        throw new IllegalStateException(e);
      }
    });
  }


}
