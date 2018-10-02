package com.jspcore.learning;

import com.netflix.config.ConfigurationManager;
import com.netflix.hystrix.Hystrix;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Utils {
  public static final int HYSTRIX_TIMEOUT = 30;

  public static void setConfig(String name, Object value) {
    ConfigurationManager.getConfigInstance().addProperty(name, value);
  }

  public static String generateShortUuid() {
    UUID uuid = UUID.randomUUID();

    long lsb = uuid.getLeastSignificantBits();
    long msb = uuid.getMostSignificantBits();

    byte[] uuidBytes = ByteBuffer.allocate(16).putLong(msb).putLong(lsb).array();

    // Strip down the '==' at the end and make it url friendly
    return new String(Base64.getEncoder().encode(uuidBytes))
      .substring(0, 6)
      .replace("/", "_")
      .replace("+", "-");
  }

  public static void setTimeout(String commandKey, int timeoutInMs) {
    setConfig("hystrix.command." + commandKey + ".execution.isolation.thread.timeoutInMilliseconds", timeoutInMs);
  }

  public static void hystrixReset() {
    ConfigurationManager.getConfigInstance().clear();
    Hystrix.reset(2, TimeUnit.SECONDS);
  }

  public static String uniqueCommandKey() {
    return "COMMAND_KEY_" + generateShortUuid();
  }

  public static void setThresholdsToCircuitBreakAfterOneFailure(String commandKey) {
    setConfig("hystrix.command." + commandKey + ".circuitBreaker.requestVolumeThreshold", 1);
    setConfig("hystrix.command." + commandKey + ".circuitBreaker.errorThresholdPercentage", 100);
  }
}
