package com.jspcore.ratpack;

import com.jspcore.ratpack.modules.PrometheusModule;
import ratpack.guice.Guice;
import ratpack.server.RatpackServer;

public class Application {

  public static void main(String[] args) throws Exception {
    RatpackServer.start(server -> server
      .registry(Guice.registry(bindings -> bindings
        .module(PrometheusModule.class))));
  }
}
