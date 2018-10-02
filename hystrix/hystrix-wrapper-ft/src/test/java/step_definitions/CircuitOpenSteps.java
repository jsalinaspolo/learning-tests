package step_definitions;

import com.jspcore.hystrix.FailureWithoutFallbackException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import org.mockito.internal.util.reflection.Whitebox;

import static org.assertj.core.api.Assertions.assertThat;

public class CircuitOpenSteps {
    private ScenarioState scenarioState;

    public CircuitOpenSteps(ScenarioState scenarioState) {
        this.scenarioState = scenarioState;
    }

    @And("^the circuit is open$")
    public void openTheCircuit() throws Throwable {
        Whitebox.setInternalState(scenarioState.hystrix()
            .scenarioCommandConfiguration().getCircuitBreaker(), "enabled", true);
        Whitebox.setInternalState(scenarioState.hystrix()
            .scenarioCommandConfiguration().getCircuitBreaker(), "forceOpen", true);
    }

    @Then("^an OpenCircuitException is thrown$")
    public void assertThatOpenCircuitExceptionHasBeenThrown() throws Throwable {
        scenarioState.getResultState().thrownException().printStackTrace();
        assertThat(scenarioState.getResultState().thrownException()).describedAs("Exception thrown by execute")
            .isNotNull()
            .isInstanceOf(FailureWithoutFallbackException.class);
    }

    @And("^no http metrics are not affected$")
    public void no_http_metrics_are_not_affected() throws Throwable {
        assertThat(scenarioState.getMetricsRegistry().getTimers()).hasSize(0);
        assertThat(scenarioState.getMetricsRegistry().getMeters()).hasSize(0);
    }
}
