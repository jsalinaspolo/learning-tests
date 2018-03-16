package com.jspcore.redis;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisURI;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.api.sync.RedisCommands;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class RedisLettuceClientShould {

    @ClassRule
    public static GenericContainer redis = new GenericContainer("redis:4.0.8")
            .withExposedPorts(6379);

    @Test
    public void read_empty_value() {
        RedisURI uri = RedisURI.create(redis.getContainerIpAddress(), redis.getMappedPort(6379));
        RedisClient client = RedisClient.create(uri);

        StatefulRedisConnection<String, String> connection = client.connect();
        RedisCommands<String, String> commands = connection.sync();

        String value = commands.get("foo");
        connection.close();
        client.shutdown();

        assertThat(value).isNull();
    }

}
