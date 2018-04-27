package com.jspcore.ratpack;

import com.google.inject.AbstractModule;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.junit.Before;
import org.junit.Test;
import ratpack.guice.Guice;
import ratpack.impose.ImpositionsSpec;
import ratpack.impose.UserRegistryImposition;
import ratpack.registry.Registry;
import ratpack.server.RatpackServer;
import ratpack.test.MainClassApplicationUnderTest;
import ratpack.test.http.TestHttpClient;

import static org.assertj.core.api.Assertions.assertThat;

public class ApplicationFunctionalTestingShould {

  interface AnyInterface {
  }

  public static class SampleModule extends AbstractModule {
    @Override
    protected void configure() {
      bind(AnyInterface.class).toInstance(new AnyInterface() {
      });
    }
  }

  public static class App {
    public static void main(String[] args) throws Exception {
      RatpackServer.start(s -> s
        .registry(Guice.registry(r -> r.module(SampleModule.class))
        ).handlers(c -> c
          .get(ctx -> ctx.render("hi"))
        ));
    }
  }

  private Registry registry;
  MainClassApplicationUnderTest aut;

  @Before
  public void init() {
    aut = new MainClassApplicationUnderTest(App.class) {
      @Override
      protected void addImpositions(ImpositionsSpec impositions) {
        impositions.add(UserRegistryImposition.of(r -> {
          registry = r;
          return Registry.empty();
        }));
      }
    };
  }

  @Test(expected = NullPointerException.class)
  public void be_able_to_access_registry_requires_server_to_initialise() {
    assertThat(registry.get(PrometheusMeterRegistry.class)).isNotNull();
  }

  @Test
  public void be_able_to_access_registry_needs_initialise_server_and_getAddress_does() {
    aut.getAddress();
    assertThat(registry.get(AnyInterface.class)).isNotNull();
  }

  @Test
  public void be_able_to_access_registry_needs_initialise_server_and_http_call_does() throws Exception {
    aut.test(TestHttpClient::get);
    assertThat(registry.get(AnyInterface.class)).isNotNull();
  }
}
