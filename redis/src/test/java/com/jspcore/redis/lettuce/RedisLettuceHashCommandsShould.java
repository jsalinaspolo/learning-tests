package com.jspcore.redis.lettuce;

import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class RedisLettuceHashCommandsShould extends AbstractCommandTest {

  @Test
  public void read_value_from_hash() {
    commands.hset("foo", "field", "value");

    String value = commands.hget("foo", "field");

    assertThat(value).isEqualTo("value");
  }
}
