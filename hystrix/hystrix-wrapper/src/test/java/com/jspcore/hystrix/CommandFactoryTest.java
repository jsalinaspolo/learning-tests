package com.jspcore.hystrix;

import com.codahale.metrics.MetricRegistry;
import com.jspcore.hystrix.config.CommandConfiguration;
import com.jspcore.hystrix.config.Test1CommandConfiguration;
import com.jspcore.hystrix.config.TestCommandGroupConfiguration;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Test;

import static org.mockito.Mockito.mock;

public class CommandFactoryTest {

  private CloseableHttpClient mockCloseableHttpClient = mock(CloseableHttpClient.class);
  private MetricRegistry metricRegistry = new MetricRegistry();

  @Test(expected = CannotConstructHttpCommandException.class)
  public void throwsExceptionWhenTryingToBuildHttpCommandWithAFactoryThatDoesNotHaveIt() throws Exception {
    // given
    CommandFactory commandFactory = CommandFactory
      .withoutHttpCapability(metricRegistry, new TestCommandGroupConfiguration());

    // when
    commandFactory.buildHttpCommand(new Test1CommandConfiguration(), new HttpGet(), mock(HttpResponseTransformer.class));
  }

  @Test(expected = IllegalStateException.class)
  public void shouldThrowExceptionIfCommandIsNotPartOfCommandGroupForJavaCommand() {
    // given
    CommandFactory commandFactory = CommandFactory
      .withoutHttpCapability(metricRegistry, new TestCommandGroupConfiguration());

    // when
    commandFactory.buildCommand(new CommandConfiguration("newCommandKey") {
    }, null);
  }

  @Test(expected = IllegalStateException.class)
  public void shouldThrowExceptionIfCommandIsNotPartOfCommandGroupForHttpCommand() {

    // given
    CommandFactory commandFactory = CommandFactory.withHttpCapability(
      metricRegistry, new TestCommandGroupConfiguration(), mockCloseableHttpClient);

    // when
    commandFactory.buildHttpCommand(new CommandConfiguration("newCommandKey") {
    }, null, null);
  }
}
