package com.jspcore.ratpack;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import org.junit.Test;
import ratpack.func.Action;
import ratpack.guice.Guice;
import ratpack.handling.Chain;
import ratpack.handling.HandlerDecorator;
import ratpack.http.Status;
import ratpack.http.client.ReceivedResponse;
import ratpack.server.RatpackServer;
import ratpack.test.MainClassApplicationUnderTest;

import static org.assertj.core.api.Assertions.assertThat;
import static ratpack.handling.Handlers.chain;

public class ApplicationRequiresHandlersShould {

  public static class Endpoint implements Action<Chain> {
    @Override
    public void execute(Chain chain) {
      chain.get(ctx -> ctx.getResponse().status(Status.OK).send("{}"));
    }
  }

  public static class EndpointModule extends AbstractModule {
    @Override
    protected void configure() {
      Multibinder.newSetBinder(binder(), HandlerDecorator.class).addBinding().toInstance((serverRegistry, rest) ->
        chain(rest, chain(serverRegistry, c -> c
          .prefix("metrics", new Endpoint()))));
    }
  }

  public static class AppWithOnlyEndpoint {
    public static void main(String[] args) throws Exception {
      RatpackServer.start(s -> s
        .registry(Guice.registry(r -> r.module(EndpointModule.class)))
      );
    }
  }

  public static class AppWithEndpointAndHandlers {
    public static void main(String[] args) throws Exception {
      RatpackServer.start(s -> s
        .registry(Guice.registry(r -> r.module(EndpointModule.class)))
        .handlers(chain -> chain.get(ctx -> ctx.render("")))
      );
    }
  }

  @Test
  public void fail_when_endpoint_in_module_but_no_handlers() throws Exception {
    new MainClassApplicationUnderTest(AppWithOnlyEndpoint.class)
      .test(client -> {
        ReceivedResponse response = client.get("metrics");
        assertThat(response.getStatusCode()).isEqualTo(404);
      });
  }

  @Test
  public void require_handlers_to_enable_endpoints_in_modules() throws Exception {
    new MainClassApplicationUnderTest(AppWithEndpointAndHandlers.class)
      .test(client -> {
        ReceivedResponse response = client.get("metrics");
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getBody().getText()).isEqualTo("{}");
      });
  }
}
