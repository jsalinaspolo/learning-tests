package com.jspcore.redis.lettuce;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisURI;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.api.sync.RedisCommands;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.testcontainers.containers.GenericContainer;

public abstract class AbstractCommandTest {

  @ClassRule
  public static GenericContainer redis = new GenericContainer("redis:4.0.11")
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
