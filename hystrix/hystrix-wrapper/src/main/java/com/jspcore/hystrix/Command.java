package com.jspcore.hystrix;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Optional;
import com.jspcore.hystrix.config.CommandConfiguration;
import com.jspcore.hystrix.config.CommandGroupConfiguration;
import com.jspcore.hystrix.config.CommandManager;
import com.netflix.config.ConfigurationManager;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import rx.Observable;
import rx.functions.Action1;

import java.util.Map;
import java.util.concurrent.Future;

import static java.lang.String.format;

public abstract class Command<T> {

  private static final Logger LOG = LoggerFactory.getLogger(Command.class);
  protected final CommandGroupConfiguration client;
  protected final CommandConfiguration command;
  protected final Optional<T> fallbackValue;
  protected final Optional<FailureListener<T>> failureListener;
  protected final MetricRegistry metricRegistry;

  private final Map mdcContextMap;

  protected Command(MetricRegistry metricRegistry,
                    CommandGroupConfiguration client,
                    CommandConfiguration command,
                    final Optional<T> fallbackValue,
                    Optional<FailureListener<T>> failureListener) {
    this.metricRegistry = metricRegistry;
    mdcContextMap = MDC.getCopyOfContextMap();
    this.client = client;
    this.command = command;
    this.fallbackValue = fallbackValue;
    this.failureListener = failureListener;
  }

  public T execute() throws FailureWithoutFallbackException {
    if (command.getKillSwitch().isOn()) {
      LOG.info(String.format("Kill Switch enabled for %s-%s", client.getGroupKey(), commandKey()));
      metricRegistry
        .meter(MetricRegistry.name(client.getGroupKey(), commandKey(), "killSwitched"))
        .mark();

      if (fallbackValue.isPresent()) {
        return fallbackValue.get();
      } else {
        throw new KillSwitchWithoutFallbackException(
          String.format("Command %s was kill switched but no fallback was defined.", commandKey()));
      }
    }

    try {
      HystrixCommand<T> hystrixCommand = hystrixCommand();
      T result = hystrixCommand.execute();

      if (hystrixCommand.isResponseFromFallback()) {
        onFailure(hystrixCommand.getExecutionException(), Optional.of(result));
      }

      return result;
    } catch (RuntimeException e) {
      onFailure(e, Optional.absent());
      throw new FailureWithoutFallbackException(e);
    }
  }

  protected abstract void onFailure(Throwable failedExecutionException, Optional<T> result);

  protected abstract T runAction() throws Exception;

  public Future<T> queue() {
    HystrixCommand<T> hystrixCommand = hystrixCommand();
    Observable<T> observable = hystrixCommand.observe();
    observable.subscribe(
      new OnFailureCallingAction<>(this, hystrixCommand, fallbackValue),
      t -> onFailure(t, fallbackValue)
    );

    return observable.toBlocking().toFuture();
  }

  private static final class OnFailureCallingAction<T> implements Action1<T> {
    private final Command<T> command;
    private final HystrixCommand<T> hystrixCommand;
    private final Optional<T> fallbackValue;

    public OnFailureCallingAction(Command<T> command, HystrixCommand<T> hystrixCommand, Optional<T> fallbackValue) {
      this.command = command;
      this.hystrixCommand = hystrixCommand;
      this.fallbackValue = fallbackValue;
    }

    @Override
    public void call(T value) {
      if (!hystrixCommand.isSuccessfulExecution()) {
        command.onFailure(hystrixCommand.getExecutionException(), fallbackValue);
      }
    }
  }

  public abstract Command<T> withFallbackTo(T fallbackValue);

  public abstract Command<T> withFailListener(FailureListener<T> failureListener);

  protected HystrixCommand<T> hystrixCommand() {
    // NOTE: fallback configuration is static, new commands with the same name will use the first commands
    // fallback setting remove the following once we removed this possibility
    ConfigurationManager.getConfigInstance().setProperty(
      format("hystrix.command.%s.fallback.enabled", commandKey()), fallbackValue.isPresent());
    // END
    return new HystrixCommand<T>(commandSetter()) {

      @Override
      protected T run() throws Exception {
        if (mdcContextMap != null) {
          MDC.setContextMap(mdcContextMap);
        }
        return runAction();
      }

      @Override
      protected T getFallback() {
        return fallbackValue.get();
      }
    };
  }

  private String commandKey() {
    return CommandManager.commandKey(client.getGroupKey(), command.getCommandKey());
  }

  protected HystrixCommand.Setter commandSetter() {
    return HystrixCommand.Setter
      .withGroupKey(HystrixCommandGroupKey.Factory.asKey(client.getGroupKey()))
      .andCommandKey(HystrixCommandKey.Factory.asKey(commandKey()))
      .andCommandPropertiesDefaults(HystrixCommandProperties.Setter() // as above this doesn't do anything
        // after the first one
        .withFallbackEnabled(this.fallbackValue.isPresent()));
  }
}
