package com.jspcore.hystrix.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import javax.validation.Valid;
import java.util.List;

public class TestCommandGroupConfiguration extends CommandGroupConfiguration {
  public static final String TEST_GROUP_KEY = "testGroup";
  private static final String METRIC_INFIX = "metric.infix";

  @Valid
  @JsonProperty
  private Test2CommandConfiguration test2Command = new Test2CommandConfiguration();

  @Valid
  @JsonProperty
  private Test1CommandConfiguration test1Command = new Test1CommandConfiguration();

  public TestCommandGroupConfiguration() {
    super(TEST_GROUP_KEY, METRIC_INFIX);
  }

  public TestCommandGroupConfiguration(CommandGroupConfiguration.ThreadpoolConfiguration threadpoolConfiguration,
                                       Test1CommandConfiguration test1Command,
                                       Test2CommandConfiguration test2Command) {
    super(TEST_GROUP_KEY, METRIC_INFIX, threadpoolConfiguration);
    this.test1Command = test1Command;
    this.test2Command = test2Command;
  }

  public Test2CommandConfiguration getTest2Command() {
    return test2Command;
  }

  public Test1CommandConfiguration getTest1Command() {
    return test1Command;
  }

  @JsonIgnore
  @Override
  public List<CommandConfiguration> getCommands() {
    ImmutableList.Builder<CommandConfiguration> commandConfigurationBuilder = ImmutableList.builder();

    if (test1Command != null) {
      commandConfigurationBuilder.add(test1Command);
    }
    if (test2Command != null) {
      commandConfigurationBuilder.add(test2Command);
    }
    return commandConfigurationBuilder.build();
  }

}
