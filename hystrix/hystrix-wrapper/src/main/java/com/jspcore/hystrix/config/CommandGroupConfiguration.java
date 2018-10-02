package com.jspcore.hystrix.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class CommandGroupConfiguration {

  @JsonIgnore
  private String groupKey;

  @Valid
  private ThreadpoolConfiguration threadPool = new ThreadpoolConfiguration();

  @NotNull
  private String metricInfix;

  public CommandGroupConfiguration(String groupKey, String metricInfix) {
    this.groupKey = groupKey;
    this.metricInfix = metricInfix;
  }

  public CommandGroupConfiguration(String groupKey, String metricInfix, ThreadpoolConfiguration threadpoolConfiguration) {
    this.groupKey = groupKey;
    this.threadPool = threadpoolConfiguration;
    this.metricInfix = metricInfix;
  }

  @JsonIgnore
  public String getGroupKey() {
    return groupKey;
  }

  public String getMetricInfix() {
    return metricInfix;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CommandGroupConfiguration that = (CommandGroupConfiguration) o;
    return Objects.equals(groupKey, that.groupKey) &&
      Objects.equals(threadPool, that.threadPool) &&
      Objects.equals(metricInfix, that.metricInfix);
  }

  @Override
  public int hashCode() {
    return Objects.hash(groupKey, threadPool, metricInfix);
  }

  @Override
  public String toString() {
    return "CommandGroupConfiguration{" +
      "groupKey='" + groupKey + '\'' +
      ", threadPool=" + threadPool +
      ", metricInfix='" + metricInfix + '\'' +
      '}';
  }

  public ThreadpoolConfiguration getThreadPool() {
    return threadPool;
  }

  @JsonIgnore
  public abstract List<CommandConfiguration> getCommands();

  public void checkValidCommand(CommandConfiguration command) {
    for (CommandConfiguration commandConfig : getCommands()) {
      if (commandConfig.getClass() == command.getClass()) return;
    }
    throw new IllegalStateException(String.format("Command %s not part of group %s", command.getClass(), this.getClass()));
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  public static class ThreadpoolConfiguration {

    @NotNull
    private Integer coreSize = 10;

    @NotNull
    private Integer maxQueueSize = -1;

    @NotNull
    private Integer queueSizeRejectionThreshold = 5;

    public ThreadpoolConfiguration() {
    }

    public ThreadpoolConfiguration(Integer coreSize, Integer maxQueueSize, Integer queueSizeRejectionThreshold) {
      this.coreSize = coreSize;
      this.maxQueueSize = maxQueueSize;
      this.queueSizeRejectionThreshold = queueSizeRejectionThreshold;
    }

    @Override
    public String toString() {
      return "ThreadpoolConfiguration{" +
        "coreSize=" + coreSize +
        ", maxQueueSize=" + maxQueueSize +
        ", queueSizeRejectionThreshold=" + queueSizeRejectionThreshold +
        '}';
    }

    @Override
    public int hashCode() {
      return Objects.hash(coreSize, maxQueueSize, queueSizeRejectionThreshold);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null || getClass() != obj.getClass()) {
        return false;
      }
      final ThreadpoolConfiguration other = (ThreadpoolConfiguration) obj;
      return Objects.equals(this.coreSize, other.coreSize) && Objects.equals(this.maxQueueSize, other.maxQueueSize) && Objects.equals(this.queueSizeRejectionThreshold, other.queueSizeRejectionThreshold);
    }

    public Integer getMaxQueueSize() {
      return maxQueueSize;
    }

    public Integer getCoreSize() {
      return coreSize;
    }

    public Integer getQueueSizeRejectionThreshold() {
      return queueSizeRejectionThreshold;
    }
  }
}
