package com.jspcore.hystrix;

import ch.qos.logback.classic.Level;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Optional;
import com.jspcore.hystrix.config.CommandConfiguration;
import com.jspcore.hystrix.config.CommandGroupConfiguration;
import com.jspcore.hystrix.config.TestCommandGroupConfiguration;
import com.logcapture.LogCapture;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.io.InputStream;

import static com.logcapture.assertion.ExpectedLoggingMessage.aLog;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

public class HttpCommandTest {
  private static final String FALLBACK_VALUE = "fallbackValue12323";
  private static final String NOT_A_FALLBACK_RESPONSE = "Not a Fallback Response";

  private CommandGroupConfiguration commandGroupConfiguration = new TestCommandGroupConfiguration();

  private HttpResponseTransformer<String> httpResponseTransformer = mock(HttpResponseTransformer.class);

  private MetricRegistry metricRegistry = new MetricRegistry();

  private Command<String> commandUnderTest = testedHttpCommandWithFallbackAs(Optional.of(FALLBACK_VALUE));

  @Before
  public void setUp() throws Exception {
    given(httpResponseTransformer.transform(any(CloseableHttpResponse.class))).willReturn(NOT_A_FALLBACK_RESPONSE);
  }

  @Test
  public void returnFallbackWhenKillSwitchIsEnabled() throws Exception {
    // given
    commandConfiguration().getKillSwitch().enable();

    // when
    String outcome = commandUnderTest.execute();

    // then
    assertThat(outcome).isEqualTo(FALLBACK_VALUE);
  }

  @Test
  public void throwExceptionWhenKillSwitchIsEnabledAndNoFallbackDefined() throws Exception {
    // given
    commandConfiguration().getKillSwitch().enable();
    commandUnderTest = testedHttpCommandWithFallbackAs(Optional.absent());

    // when
    try {
      commandUnderTest.execute();
      fail("Expected KillSwitchWithoutFallbackException");
    } catch (KillSwitchWithoutFallbackException e) {
      assertThat(e.getMessage()).isEqualTo("Command test1Command was kill switched but no fallback was defined.");
    }
  }

  @Test
  public void executeCommandWhenKillSwitchIsEnabledFirstAndThenDisabled() throws Exception {
    // given
    commandConfiguration().getKillSwitch().enable();
    commandUnderTest.execute();
    commandConfiguration().getKillSwitch().disable();

    // when
    String outcome = commandUnderTest.execute();

    // then
    assertThat(outcome).isEqualTo(NOT_A_FALLBACK_RESPONSE);
  }

  @Test
  public void executingCommandThatHasBeenKillSwitchedProducesMetric() throws Exception {
    // given
    commandConfiguration().getKillSwitch().enable();

    // when
    commandUnderTest.execute();

    // then
    String metricName = MetricRegistry.name("testGroup", "test1Command", "killSwitched");
    Meter killSwitchMeter = metricRegistry.getMeters()
      .get(metricName);

    assertThat(killSwitchMeter).describedAs("Metric meter with name: " + metricName).isNotNull();
    assertThat(killSwitchMeter.getCount()).isEqualTo(1);
  }

  @Test
  public void logWarningWhenKillSwitchIsEnabled() {
    LogCapture.captureLogEvents(() -> {
      commandConfiguration().getKillSwitch().enable();
      commandUnderTest.execute();

    }).logged(aLog().
      withLevel(equalTo(Level.INFO))
      .withMessage(equalTo("Kill Switch enabled for testGroup-test1Command")));
  }

  @Test
  public void executeCommandShouldConsumeResponse() throws Exception {
    InputStream inputStream = mock(InputStream.class);
    CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
    CloseableHttpResponse httpResponse = mockHttpResponseWith(inputStream);
    given(httpClient.execute(any())).willReturn(httpResponse);

    HttpCommand<String> httpCommand = new HttpCommand<>(
      metricRegistry, httpClient,
      commandGroupConfiguration,
      commandConfiguration(),
      mock(HttpRequestBase.class),
      httpResponseTransformer,
      Optional.absent(),
      Optional.absent());

    httpCommand.execute();

    InOrder inOrder = Mockito.inOrder(inputStream, httpResponse);
    inOrder.verify(inputStream).close();
    inOrder.verify(httpResponse).close();
  }

  @Test
  public void executeCommandShouldConsumeResponseWhenTransformerThrowsException() throws Exception {
    InputStream inputStream = mock(InputStream.class);
    CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
    CloseableHttpResponse httpResponse = mockHttpResponseWith(inputStream);
    given(httpClient.execute(any())).willReturn(httpResponse);
    given(httpResponseTransformer.transform(httpResponse)).willThrow(new RuntimeException());

    HttpCommand<String> httpCommand = new HttpCommand<>(
      metricRegistry, httpClient,
      commandGroupConfiguration,
      commandConfiguration(),
      mock(HttpRequestBase.class),
      httpResponseTransformer,
      Optional.absent(),
      Optional.absent());

    catchThrowable(httpCommand::execute);

    InOrder inOrder = Mockito.inOrder(inputStream, httpResponse);
    inOrder.verify(inputStream).close();
    inOrder.verify(httpResponse).close();
  }

  private CloseableHttpResponse mockHttpResponseWith(InputStream inputStream) {
    CloseableHttpResponse httpResponse = mock(CloseableHttpResponse.class);
    BasicHttpEntity basicHttpEntity = new BasicHttpEntity();

    basicHttpEntity.setContent(inputStream);
    given(httpResponse.getEntity()).willReturn(basicHttpEntity);
    return httpResponse;
  }

  private CommandConfiguration commandConfiguration() {
    return commandGroupConfiguration.getCommands().get(0);
  }

  private HttpCommand<String> testedHttpCommandWithFallbackAs(Optional<String> fallbackValue) {
    return new HttpCommand<>(metricRegistry, mock(CloseableHttpClient.class),
      commandGroupConfiguration,
      commandConfiguration(),
      mock(HttpRequestBase.class),
      httpResponseTransformer,
      fallbackValue,
      Optional.absent());
  }
}
