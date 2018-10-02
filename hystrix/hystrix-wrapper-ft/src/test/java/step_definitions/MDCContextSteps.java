package step_definitions;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;
import static state.MDCState.FIRST_REQUESTID_VALUE;
import static state.MDCState.SECOND_REQUESTID_VALUE;

public class MDCContextSteps {
    private ScenarioState scenarioState;

    public MDCContextSteps(ScenarioState scenarioState) {
        this.scenarioState = scenarioState;
    }

    @Given("^one thread available in the pool$")
    public void setUpOnlyOneThread() throws Throwable {
        setUpThreadpoolSize(1);
    }

    @Given("^two threads available in the pool$")
    public void setUpTwoAvailableThreadsForHttpWrappedBasedQueuing() throws Throwable {
        setUpThreadpoolSize(2);
    }

    @And("^we set request-id in the main thread$")
    public void setRequestId() throws Throwable {
        scenarioState.mdc().setupRequestId(FIRST_REQUESTID_VALUE);
    }

    @When("^we set a new request-id in the main thread$")
    public void setNewRequestId() throws Throwable {
        scenarioState.mdc().setupRequestId(SECOND_REQUESTID_VALUE);
    }

    @Then("^the(?:| new) request-id is available in its MDC context$")
    public void assertRequestIdInContext() throws Throwable {
        assertThat(scenarioState.mdc().commandThreadRequestId())
                .isEqualTo(scenarioState.mdc().mainThreadRequestId());
    }

    private void setUpThreadpoolSize(int threads) {
        scenarioState.hystrix().setThreadPoolWithTotalOf(threads);
    }
}
