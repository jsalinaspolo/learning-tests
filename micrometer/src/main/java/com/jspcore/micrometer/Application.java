package com.jspcore.micrometer;

import com.sun.net.httpserver.HttpServer;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.prometheus.PrometheusMeterRegistry;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Optional;

public class Application {

  public void start(CompositeMeterRegistry registry) {

    try {
      HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
      server.createContext("/prometheus", httpExchange -> {
        Optional<PrometheusMeterRegistry> maybePrometheus = registry.getRegistries().stream()
          .filter(m -> m instanceof PrometheusMeterRegistry)
          .map(m -> (PrometheusMeterRegistry) m)
          .findAny();

        maybePrometheus.ifPresent(p -> {
          String response = p.scrape();
          try {
            httpExchange.sendResponseHeaders(200, response.length());
            OutputStream os = httpExchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        });

      });
      new Thread(server::start).run();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }
}
