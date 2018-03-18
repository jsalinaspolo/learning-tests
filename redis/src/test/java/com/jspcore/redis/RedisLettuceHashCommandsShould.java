package com.jspcore.redis;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisURI;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.api.sync.RedisCommands;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class RedisLettuceHashCommandsShould {

  @ClassRule
  public static GenericContainer redis = new GenericContainer("redis:4.0.8")
    .withExposedPorts(6379);

  private RedisURI URI;
  private RedisClient client;

  @Before
  public void initialise() {
    URI = RedisURI.create(redis.getContainerIpAddress(), redis.getMappedPort(6379));
    client = RedisClient.create(URI);
  }

  @Test
  public void read_value_from_hash() {
    StatefulRedisConnection<String, String> connection = client.connect();
    RedisCommands<String, String> commands = connection.sync();
    commands.hset("foo", "field", "value");

    String value = commands.hget("foo", "field");
    connection.close();

    assertThat(value).isEqualTo("value");
  }
}
