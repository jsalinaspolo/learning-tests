package com.jspcore.hystrix.config;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.util.Duration;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Objects;

public abstract class CommandConfiguration {

  @JsonIgnore
  private String commandKey;

  @Valid
  @JsonProperty
  private CircuitBreaker circuitBreaker = new CircuitBreaker();

  @Valid
  @JsonProperty
  private Metrics metrics = new Metrics();

  @Valid
  @JsonProperty
  private Execution execution = new Execution();

  private KillSwitch killSwitch = new KillSwitch();

  public CommandConfiguration(String commandKey) {
    this.commandKey = commandKey;
  }

  public CommandConfiguration(String commandKey,
                              CircuitBreaker circuitBreaker,
                              Metrics metrics,
                              Execution execution) {
    this.commandKey = commandKey;
    this.circuitBreaker = circuitBreaker;
    this.metrics = metrics;
    this.execution = execution;
  }

  @Override
  public String toString() {
    return "Command{" +
      "commandKey='" + commandKey + '\'' +
      ", circuitBreaker=" + circuitBreaker +
      ", metrics=" + metrics +
      ", execution=" + execution +
      '}';
  }

  @Override
  public int hashCode() {
    return Objects.hash(commandKey, circuitBreaker, metrics, execution);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final CommandConfiguration other = (CommandConfiguration) obj;
    return Objects.equals(this.commandKey, other.commandKey) &&
      Objects.equals(this.circuitBreaker, other.circuitBreaker) &&
      Objects.equals(this.metrics, other.metrics) &&
      Objects.equals(this.execution, other.execution);
  }

  public CircuitBreaker getCircuitBreaker() {
    return circuitBreaker;
  }

  public Metrics getMetrics() {
    return metrics;
  }

  public Execution getExecution() {
    return execution;
  }

  @JsonIgnore
  public String getCommandKey() {
    return commandKey;
  }

  public KillSwitch getKillSwitch() {
    return killSwitch;
  }

  public void set(KillSwitch killSwitch) {
    this.killSwitch = killSwitch;
  }

  public static class KillSwitch {

    @JsonProperty
    private KillSwitchState state = KillSwitchState.OFF;

    public void enable() {
      this.state = KillSwitchState.ON;
    }

    public void disable() {
      this.state = KillSwitchState.OFF;
    }

    public KillSwitchState getState() {
      return state;
    }

    @JsonIgnore
    public boolean isOn() {
      return state == KillSwitchState.ON;
    }
  }

  public static enum KillSwitchState {
    OFF,
    ON
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  public static class CircuitBreaker {

    @NotNull
    @JsonProperty
    private Boolean enabled = false;

    @JsonProperty
    @NotNull
    private Integer requestVolumeThreshold = 20;

    @JsonProperty
    @NotNull
    private Duration sleepWindow = Duration.milliseconds(5000);

    @JsonProperty
    @NotNull
    @Min(0)
    @Max(100)
    private Integer errorThresholdPercentage = 50;

    @JsonProperty
    @NotNull
    private Boolean forceOpen = false;

    @NotNull
    @JsonProperty
    private Boolean forceClosed = false;

    public CircuitBreaker() {
    }

    public CircuitBreaker(Boolean enabled,
                          Integer requestVolumeThreshold,
                          Duration sleepWindow,
                          Integer errorThresholdPercentage,
                          Boolean forceOpen,
                          Boolean forceClosed) {
      this.enabled = enabled;
      this.requestVolumeThreshold = requestVolumeThreshold;
      this.sleepWindow = sleepWindow;
      this.errorThresholdPercentage = errorThresholdPercentage;
      this.forceOpen = forceOpen;
      this.forceClosed = forceClosed;
    }

    @Override
    public String toString() {
      return "CircuitBreaker{" +
        "enabled=" + enabled +
        ", requestVolumeThreshold=" + requestVolumeThreshold +
        ", sleepWindow=" + sleepWindow +
        ", errorThresholdPercentage=" + errorThresholdPercentage +
        ", forceOpen=" + forceOpen +
        ", forceClosed=" + forceClosed +
        '}';
    }

    @Override
    public int hashCode() {
      return Objects.hash(enabled,
        requestVolumeThreshold,
        sleepWindow,
        errorThresholdPercentage,
        forceOpen,
        forceClosed);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null || getClass() != obj.getClass()) {
        return false;
      }
      final CircuitBreaker other = (CircuitBreaker) obj;
      return Objects.equals(this.enabled, other.enabled) &&
        Objects.equals(this.requestVolumeThreshold, other.requestVolumeThreshold) &&
        Objects.equals(this.sleepWindow, other.sleepWindow) &&
        Objects.equals(this.errorThresholdPercentage, other.errorThresholdPercentage) &&
        Objects.equals(this.forceOpen, other.forceOpen) &&
        Objects.equals(this.forceClosed, other.forceClosed);
    }

    public Boolean getEnabled() {
      return enabled;
    }

    public Integer getRequestVolumeThreshold() {
      return requestVolumeThreshold;
    }

    public Duration getSleepWindow() {
      return sleepWindow;
    }

    public Integer getErrorThresholdPercentage() {
      return errorThresholdPercentage;
    }

    public Boolean getForceOpen() {
      return forceOpen;
    }

    public Boolean getForceClosed() {
      return forceClosed;
    }

    public void setForceOpen(Boolean forceOpen) {
      this.forceOpen = forceOpen;
    }

    public void setForceClosed(Boolean forceClosed) {
      this.forceClosed = forceClosed;
    }
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  public static class Metrics {

    @JsonProperty
    @NotNull
    private Duration healthSnapshotInterval = Duration.milliseconds(500);

    public Metrics() {
    }

    public Metrics(Duration healthSnapshotInterval) {
      this.healthSnapshotInterval = healthSnapshotInterval;
    }

    @Override
    public String toString() {
      return "Metrics{" +
        "healthSnapshotInterval=" + healthSnapshotInterval +
        '}';
    }

    @Override
    public int hashCode() {
      return Objects.hash(healthSnapshotInterval);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null || getClass() != obj.getClass()) {
        return false;
      }
      final Metrics other = (Metrics) obj;
      return Objects.equals(this.healthSnapshotInterval, other.healthSnapshotInterval);
    }

    public Duration getHealthSnapshotInterval() {
      return healthSnapshotInterval;
    }
  }

  public enum IsolationStrategy {
    THREAD,
    SEMAPHORE
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  public static class Execution {

    @JsonProperty
    @NotNull
    private IsolationStrategy isolationStrategy = IsolationStrategy.THREAD;

    @JsonProperty
    @NotNull
    private Duration isolationThreadTimeout = Duration.milliseconds(1000);


    @JsonProperty
    @NotNull
    private Integer isolationSemaphoreMaxConcurrentRequests = 10;

    public Execution() {
    }

    public Execution(IsolationStrategy isolationStrategy,
                     Duration isolationThreadTimeout,
                     Integer isolationSemaphoreMaxConcurrentRequests) {
      this.isolationStrategy = isolationStrategy;
      this.isolationThreadTimeout = isolationThreadTimeout;
      this.isolationSemaphoreMaxConcurrentRequests = isolationSemaphoreMaxConcurrentRequests;
    }

    public Integer getIsolationSemaphoreMaxConcurrentRequests() {
      return isolationSemaphoreMaxConcurrentRequests;
    }

    public IsolationStrategy getIsolationStrategy() {
      return isolationStrategy;
    }

    public Duration getIsolationThreadTimeout() {
      return isolationThreadTimeout;
    }

    @Override
    public int hashCode() {
      return Objects.hash(isolationStrategy, isolationThreadTimeout, isolationSemaphoreMaxConcurrentRequests);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null || getClass() != obj.getClass()) {
        return false;
      }
      final Execution other = (Execution) obj;
      return Objects.equals(this.isolationStrategy, other.isolationStrategy)
        && Objects.equals(this.isolationThreadTimeout, other.isolationThreadTimeout)
        && Objects.equals(this.isolationSemaphoreMaxConcurrentRequests, other.isolationSemaphoreMaxConcurrentRequests);
    }

    @Override
    public String toString() {
      return "Execution{" +
        "isolationStrategy=" + isolationStrategy +
        ", isolationThreadTimeout=" + isolationThreadTimeout +
        ", isolationSemaphoreMaxConcurrentRequests=" + isolationSemaphoreMaxConcurrentRequests +
        '}';
    }
  }
}
