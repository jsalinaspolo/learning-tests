package com.jspcore.ratpack.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.multibindings.Multibinder;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import ratpack.handling.HandlerDecorator;

import static ratpack.handling.Handlers.chain;

public class PrometheusModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(PrometheusMeterRegistry.class).toInstance(new PrometheusMeterRegistry(PrometheusConfig.DEFAULT));
    bind(PrometheusEndpoint.class).in(Scopes.SINGLETON);

    Multibinder.newSetBinder(binder(), HandlerDecorator.class).addBinding().toInstance((serverRegistry, rest) ->
      chain(rest, chain(serverRegistry, c -> c
        .prefix("metrics", PrometheusEndpoint.class))));
  }
}
