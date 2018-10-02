package com.jspcore.hystrix;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Optional;
import com.jspcore.hystrix.config.CommandConfiguration;
import com.jspcore.hystrix.config.CommandGroupConfiguration;
import com.jspcore.hystrix.config.CommandManager;
import com.netflix.config.ConfigurationManager;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;

public class CommandFactory {
  private final CommandGroupConfiguration client;
  private final CommandManager commandManager = new CommandManager(ConfigurationManager.getConfigInstance());

  private final MetricRegistry metricRegistry;
  private final Optional<CloseableHttpClient> closeableHttpClient;

  private CommandFactory(MetricRegistry metricRegistry,
                         CommandGroupConfiguration client,
                         Optional<CloseableHttpClient> closeableHttpClient) {
    this.metricRegistry = metricRegistry;
    this.closeableHttpClient = closeableHttpClient;
    this.client = client;
    commandManager.install(client);
  }

  public static CommandFactory withHttpCapability(MetricRegistry metricRegistry,
                                                  CommandGroupConfiguration client,
                                                  CloseableHttpClient closeableHttpClient) {
    return new CommandFactory(metricRegistry, client, Optional.of(closeableHttpClient));
  }

  public static CommandFactory withoutHttpCapability(MetricRegistry metricRegistry, CommandGroupConfiguration client) {
    return new CommandFactory(metricRegistry, client, Optional.<CloseableHttpClient>absent());
  }

  public <T> Command<T> buildCommand(CommandConfiguration command,
                                     Action<T> action) {
    client.checkValidCommand(command);

    return new JavaCommand<>(metricRegistry, client, command, action);
  }

  public <T> HttpCommand<T> buildHttpCommand(CommandConfiguration command,
                                             HttpUriRequest httpRequest,
                                             HttpResponseTransformer<T> transformer) {
    client.checkValidCommand(command);

    if (closeableHttpClient.isPresent()) {
      return new HttpCommand<>(metricRegistry,
        closeableHttpClient.get(),
        client,
        command,
        httpRequest,
        transformer
      );
    }

    throw new CannotConstructHttpCommandException();
  }
}
