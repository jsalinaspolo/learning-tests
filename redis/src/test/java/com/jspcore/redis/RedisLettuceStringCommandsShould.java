package com.jspcore.redis;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisURI;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.api.sync.RedisCommands;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class RedisLettuceStringCommandsShould {

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

  @After
  public void closeClient() {
    client.shutdown();
  }

  @Test
  public void read_empty_value() {
    StatefulRedisConnection<String, String> connection = client.connect();
    RedisCommands<String, String> commands = connection.sync();

    String value = commands.get("unknown-key");
    connection.close();

    assertThat(value).isNull();
  }

  @Test
  public void read_value_has_value() {
    StatefulRedisConnection<String, String> connection = client.connect();
    RedisCommands<String, String> commands = connection.sync();
    commands.set("foo", "value");

    String value = commands.get("foo");
    connection.close();

    assertThat(value).isEqualTo("value");
  }

}
