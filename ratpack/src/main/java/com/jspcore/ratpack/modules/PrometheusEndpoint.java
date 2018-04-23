package com.jspcore.ratpack.modules;

import io.micrometer.prometheus.PrometheusMeterRegistry;
import ratpack.func.Action;
import ratpack.handling.Chain;
import ratpack.http.Status;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PrometheusEndpoint implements Action<Chain> {

  private final PrometheusMeterRegistry registry;

  @Inject
  public PrometheusEndpoint(PrometheusMeterRegistry registry) {
    this.registry = registry;
  }

  @Override
  public void execute(Chain chain) {
    chain.get(ctx -> ctx.getResponse().status(Status.OK).send(registry.scrape()));
  }
}
