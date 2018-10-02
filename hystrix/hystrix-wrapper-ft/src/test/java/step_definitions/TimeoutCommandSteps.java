package step_definitions;

import com.jspcore.hystrix.FailureWithoutFallbackException;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import io.dropwizard.util.Duration;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class TimeoutCommandSteps {
    private final ScenarioState scenarioState;

    public TimeoutCommandSteps(ScenarioState scenarioState) {
        this.scenarioState = scenarioState;
    }

    @Given("^hystrix timeout < http call delay < http timeout$")
    public void setUpTimeoutScenario() throws Throwable {
        Whitebox.setInternalState(scenarioState.hystrix()
            .scenarioCommandConfiguration().getExecution(), "isolationThreadTimeout", Duration.milliseconds(30));
        scenarioState.wiremock().setResponseDelay(200);
        scenarioState.http().setSocketTimeOut(350);

        scenarioState.scenarioWait().setWaitAfterCommand(40);
    }

    @Given("^an? ([a-zA-Z]*?) command that always times out(?:| (?:with a (fallback)))(?:|(?:| and) with a (failure listener))$")
    public void setUpTimeOutCommand(CommandType commandType, String fallbackType, String failureListener) throws Throwable {
        scenarioState.setLatestCommandType(commandType);
        switch (commandType) {
            default:
            case Java:
                when(scenarioState.getMockJavaCall().call()).thenAnswer(new Answer<String>() {
                    @Override
                    public String answer(InvocationOnMock invocationOnMock) throws Throwable {
                        Thread.sleep(30);
                        return "success after some time";
                    }
                });
                break;
            case HTTP:
                // is timing out due to the background of the feature
                stubFor(post(urlEqualTo(scenarioState.wiremock().testEndpointPath()))
                        .willReturn(aResponse().withBody(scenarioState.wiremock().responseBody())
                                               .withFixedDelay(scenarioState.wiremock().responseDelay())));
                break;
        }

        scenarioState.hystrix().setUpCommand(commandType, fallbackType, failureListener);
     }

    @Then("^a TimeoutException is thrown$")
    public void assertThatTimeoutExceptionIsThrown() throws Throwable {
        assertThat(scenarioState.getResultState().thrownException()).describedAs("Exception thrown by execute")
            .isNotNull()
            .isInstanceOf(FailureWithoutFallbackException.class);
        scenarioState.getResultState().thrownException().printStackTrace();
    }
}
