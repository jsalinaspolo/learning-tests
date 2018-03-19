package com.jspcore.redis.lettuce;

import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class RedisLettuceBaseCommandsShould extends AbstractCommandTest {

  @Test
  public void ping_server() {
    assertThat(commands.ping()).isEqualTo("PONG");
  }
}
