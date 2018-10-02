package step_definitions;

import com.jspcore.hystrix.FailureWithoutFallbackException;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import io.dropwizard.util.Duration;
import org.mockito.internal.util.reflection.Whitebox;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static step_definitions.ScenarioState.FALLBACK_VALUE;

public class FailureCommandSteps {
    public static final RuntimeException RUNTIME_EXCEPTION = new RuntimeException("Java Call failing");
    private final ScenarioState scenarioState;

    public FailureCommandSteps(ScenarioState scenarioState) {
        this.scenarioState = scenarioState;
    }

    @Given("^http timeout < http call delay < hystrix timeout$")
    public void setUpFailureScenario() throws Throwable {
        Whitebox.setInternalState(scenarioState.hystrix()
            .scenarioCommandConfiguration().getExecution(), "isolationThreadTimeout", Duration.seconds(1));
        scenarioState.http().setSocketTimeOut(10);
        scenarioState.wiremock().setResponseDelay(20);

        scenarioState.scenarioWait().setWaitAfterCommand(500);
    }

    @Given("^an? ([a-zA-Z]*?) command that always fails(?:| (?:with a (fallback)))(?:|(?:| and) with a (failure listener))$")
    public void setUpCommandThatAlwaysFails(CommandType commandType, String fallback, String failureListener) throws Throwable {
        scenarioState.setLatestCommandType(commandType);
        switch (commandType) {
            default:
            case Java:
                when(scenarioState.getMockJavaCall().call()).thenThrow(RUNTIME_EXCEPTION);
                break;
            case HTTP:
                // default wiremock delay is greater than the default http timeout
                stubFor(post(urlEqualTo(scenarioState.wiremock().testEndpointPath()))
                        .willReturn(aResponse().withBody(scenarioState.wiremock().responseBody())
                                               .withFixedDelay(scenarioState.wiremock().responseDelay())));
                break;
        }

        scenarioState.hystrix().setUpCommand(commandType, fallback, failureListener);
    }

    @Then("^an exception is thrown$")
    public void assertThatExceptionIsThrown() throws Throwable {
        assertThat(scenarioState.getResultState().thrownException()).describedAs("Exception thrown by execute")
                                                                    .isNotNull()
                                                                    .isInstanceOf(FailureWithoutFallbackException.class);
    }

    @Then("^the fallback is returned$")
    public void assertFallbackIsReturned() throws Throwable {
        String result = scenarioState.getResultState().successResult();
        assertThat(result).describedAs("Response from call").isEqualTo(FALLBACK_VALUE);
    }

    @Then("^a NotImplementedException is thrown$")
    public void assertNotImplementedExceptionHasBeenThrown() throws Throwable {
        assertThat(scenarioState.getResultState().thrownException())
                .describedAs("Exception thrown from command")
                .isNotNull()
                .isInstanceOf(IllegalArgumentException.class);
    }
}
