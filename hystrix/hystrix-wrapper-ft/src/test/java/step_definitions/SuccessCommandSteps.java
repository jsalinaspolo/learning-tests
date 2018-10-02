package step_definitions;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.google.common.base.Optional;
import com.jspcore.hystrix.HttpResponseTransformer;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import com.netflix.hystrix.exception.HystrixTimeoutException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import io.dropwizard.util.Duration;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.jboss.logging.Logger;
import org.mockito.ArgumentCaptor;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import state.ResultState;

import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeoutException;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static step_definitions.ScenarioState.FALLBACK_VALUE;
import static step_definitions.ScenarioState.SUCCESS_VALUE;

public class SuccessCommandSteps {
    private ScenarioState scenarioState;

    private static final Logger LOG = Logger.getLogger(SuccessCommandSteps.class);

    public SuccessCommandSteps(ScenarioState scenarioState) {
        this.scenarioState = scenarioState;
    }

    @Then("^a successful response is( eventually)? returned$")
    public void assertSuccessResponse(String eventually) throws Throwable {
        boolean isQueued = " eventually".equals(eventually);

        final ResultState resultState = scenarioState.getResultState();
        assertThat(resultState).describedAs("Command ResultState").isNotNull();

        String result = resultState.successResult();

        if (!SUCCESS_VALUE.equals(result)) {
            Throwable exception = scenarioState.getResultState().thrownException();
            if (exception == null) {
                fail(isQueued ?
                     "Async " :
                     "" + "Response from call: expected:<" + SUCCESS_VALUE + "> but was:<" + result + ">");
            }
            fail(isQueued ? "Async " : "" + "Response from call: unexpected exception: " + exception);
        }
    }

    @Then("^the callback is called with successful result and the request has been passed to it$")
    public void theCallbackIsCalledWithSuccessfulResult() throws Throwable {
        assertSuccessResponse("");
    }

    @Given("^we have larger timeouts$")
    public void LargeTimeouts() throws Throwable {
        final int largeTimeout = 3000;
        scenarioState.wiremock().setResponseDelay(largeTimeout);
        scenarioState.javaCall().setResponseDelay(largeTimeout);
        scenarioState.http().setSocketTimeOut(largeTimeout * 2);
        Whitebox.setInternalState(scenarioState.hystrix()
                .scenarioCommandConfiguration().getExecution(),
            "isolationThreadTimeout",
            Duration.milliseconds(largeTimeout * 2)
        );
    }

    @Then("^the command has run asynchronously without failure$")
    public void assertAsynchronousCallHappenedWithoutFailure() throws Throwable {
        assertReturnedWithoutFailure();

        switch (scenarioState.hystrix().getCommandType()) {
            default:
            case Java:
                verify(scenarioState.getMockJavaCall()).call();
                reset(scenarioState.getMockJavaCall());
                break;
            case HTTP:
                WireMock.verify(postRequestedFor(urlEqualTo(scenarioState.wiremock().testEndpointPath())));
                break;
        }
    }

    @Then("^the command has returned without failure$")
    public void assertReturnedWithoutFailure() throws Throwable {
        assertThat(scenarioState.getResultState().thrownException())
            .describedAs("Exception from call")
            .isNull();
    }

    @And("^the HTTP connection is closed")
    public void assertThatTheHttpConnectionHasBeenClosed() throws Throwable {
        assertThat(scenarioState.http().connectionPoolStats().getLeased())
            .describedAs("Leased http connections in the pool")
            .isEqualTo(0);
    }

    @Given("^(?:|we set up )an? (HTTP|Java) command" +
           "(?:| (?:with a (fallback)))(?:|(?:| and) with a (failure listener))$")
    public void setUpCommand(CommandType commandType, String fallbackType, String failureListener) throws Throwable {
        scenarioState.setLatestCommandType(commandType);
        switch (commandType) {
            default:
            case Java:
                when(scenarioState.getMockJavaCall().call()).thenAnswer(new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                        try {
                            LOG.info("Java Command Start");
                            Thread.sleep(scenarioState.javaCall().responseDelay());
                            return SUCCESS_VALUE;
                        } finally {
                            LOG.info("Java Command Finished");
                        }
                    }
                });
                break;
            case HTTP:
                ResponseDefinitionBuilder response = aResponse().withBody(scenarioState.wiremock().responseBody());
//                int networkByteDribbleDelay = scenarioState.wiremock().networkByteDribbleDelay();
//                if (networkByteDribbleDelay > 0) {
//                    response.withByteDribbleDelay(networkByteDribbleDelay);
//                }
                response.withFixedDelay(scenarioState.wiremock().responseDelay());
                stubFor(post(urlEqualTo(scenarioState.wiremock().testEndpointPath()))
                    .willReturn(response));
                break;
        }

        scenarioState.hystrix().setUpCommand(commandType, fallbackType, failureListener);
    }

    @And("^the failure listener was not called$")
    public void assertOnFailListener() throws Throwable {
        verifyZeroInteractions(scenarioState.hystrix().getMockFailureListener());
    }

    @And("^the failure listener was called with HystrixRuntimeException with " +
         "(TIMEOUT|SHORTCIRCUIT|COMMAND_EXCEPTION|REJECTED_THREAD_EXECUTION) " +
         "cause( and fallback value passed in)?$")
    public void theFailureListenerWasCalledWithTimeoutException(String exceptionClass, String fallbackExpected)
        throws Throwable {
        ArgumentCaptor<? extends Throwable> exceptionPassedToOnFailureCaptor = ArgumentCaptor.forClass(
            HystrixRuntimeException.class);
        ArgumentCaptor<Optional> resultPassedToOnFailureCaptor = ArgumentCaptor.forClass(Optional.class);
        verify(scenarioState.hystrix().getMockFailureListener()).onFailure(exceptionPassedToOnFailureCaptor.capture(),
            resultPassedToOnFailureCaptor.capture()
        );

        final Optional<String> resultPassedToOnFailure = resultPassedToOnFailureCaptor.getValue();
        final Throwable exceptionPassedToOnFailure = exceptionPassedToOnFailureCaptor.getValue();

        assertExceptionIs(exceptionClass, exceptionPassedToOnFailure);

        if (fallbackExpected == null) {
            assertThat(resultPassedToOnFailure.isPresent()).describedAs("result passed on failure").isFalse();
        } else {
            assertThat(resultPassedToOnFailure.get()).describedAs("result passed on failure").isEqualTo(FALLBACK_VALUE);
        }
    }

    private void assertExceptionIs(String exceptionClass, Throwable exception) {
        LOG.info("------------------");
        LOG.info("", exception);
        LOG.info("------------------");
        if (exception instanceof HystrixRuntimeException) {
            exception = exception.getCause();
        }
        switch (exceptionClass) {
            case "TIMEOUT":
                assertThat(exception).isInstanceOfAny(HystrixTimeoutException.class, TimeoutException.class);
                break;
            case "SHORTCIRCUIT":
                assertThat(exception).isInstanceOf(RuntimeException.class);
                assertThat(exception).hasMessage("Hystrix circuit short-circuited and is OPEN");
                break;
            case "COMMAND_EXCEPTION":
                assertThat(exception).isInstanceOf(exceptionFor(scenarioState.getLatestCommandType()));
                break;
            case "REJECTED_THREAD_EXECUTION":
                assertThat(exception).isInstanceOf(RejectedExecutionException.class);
                break;
            default:
                throw new IllegalArgumentException("The case of " + exceptionClass + " has not been covered");
        }
    }

    private Class<? extends Exception> exceptionFor(CommandType commandType) {
        return commandType == CommandType.HTTP ? SocketTimeoutException.class : RuntimeException.class;
    }

    @And("^the future throws HystrixRuntimeException with " +
         "(TIMEOUT|SHORTCIRCUIT|COMMAND_EXCEPTION|REJECTED_THREAD_EXECUTION) cause$")
    public void theFutureThrowsHystrixRuntimeExceptionWithCause(String exceptionClass) throws Throwable {
        try {
            scenarioState.getResultState().successResult();
            fail("expected HystrixRuntimeException");
        } catch (ExecutionException e) {
            assertExceptionIs(exceptionClass, e.getCause());
        }
    }

    @Given("^an HTTP command that does not read the stream$")
    public void an_HTTP_command_that_does_not_read_the_stream() throws Throwable {
        ResponseDefinitionBuilder response = aResponse().withBody(scenarioState.wiremock().responseBody());
        stubFor(post(urlEqualTo(scenarioState.wiremock().testEndpointPath()))
            .willReturn(response));

        scenarioState.hystrix().setUpCommand(CommandType.HTTP, null, null, new HttpResponseTransformer<String>() {
            @Override
            public String transform(CloseableHttpResponse response) throws Exception {
                return "success";
            }
        });
    }

    @Then("^the command has not finished yet$")
    public void itReturnedBeforeTheTimeout() throws Throwable {
        LOG.info("********************************** " + scenarioState.timeTaken() + " <  " +
                 scenarioState.javaCall().responseDelay());
        assertThat(scenarioState.timeTaken()).isLessThan(scenarioState.javaCall().responseDelay());
    }
}
