package com.jspcore.redis.lettuce;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.junit.*;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public abstract class AbstractCommandTest {

  @ClassRule
  public static GenericContainer redis = new GenericContainer("redis:6.0.8")
    .withExposedPorts(6379);

  protected static RedisClient client;
  protected StatefulRedisConnection<String, String> connection;
  protected RedisCommands<String, String> commands;

  @BeforeClass
  public static void setupClient() {
    client = RedisClient.create(RedisURI.create(redis.getContainerIpAddress(), redis.getMappedPort(6379)));
  }

  @AfterClass
  public static void shutdownClient() {
    client.shutdown();
  }

  @Before
  public final void openConnection() throws Exception {
    connection = client.connect();
    commands = connection.sync();
  }

  @After
  public final void closeConnection() throws Exception {
    commands.flushall();
    connection.close();
  }
}
