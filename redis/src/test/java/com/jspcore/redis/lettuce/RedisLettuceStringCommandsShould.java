package com.jspcore.redis.lettuce;

import io.lettuce.core.RedisCommandExecutionException;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class RedisLettuceStringCommandsShould extends AbstractCommandTest {

  @Test
  public void read_unset_key_has_null_value() {
    String value = commands.get("unknown-key");

    assertThat(value).isNull();
  }

  @Test
  public void read_key_with_value() {
    commands.set("foo", "value");

    String value = commands.get("foo");

    assertThat(value).isEqualTo("value");
  }

  @Test
  public void increment_integer_value_by_one() {
    commands.set("foo", "1");

    Long value = commands.incr("foo");

    assertThat(value).isEqualTo(2);
  }

  @Test
  public void throws_exception_when_increment_non_integer() {
    commands.set("foo", "non-integer-value");

    assertThatThrownBy(()->commands.incr("foo"))
      .isInstanceOf(RedisCommandExecutionException.class)
      .hasMessage("ERR value is not an integer or out of range");
  }

  @Test
  public void decrement_integer_value_by_one() {
    commands.set("foo", "1");

    Long value = commands.decr("foo");

    assertThat(value).isEqualTo(0);
  }

  @Test
  public void throws_exception_when_decrement_non_integer() {
    commands.set("foo", "non-integer-value");

    assertThatThrownBy(()->commands.decr("foo"))
      .isInstanceOf(RedisCommandExecutionException.class)
      .hasMessage("ERR value is not an integer or out of range");
  }
}
