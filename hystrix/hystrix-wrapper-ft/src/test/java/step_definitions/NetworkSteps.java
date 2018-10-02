package step_definitions;

import cucumber.api.java.en.Given;
import io.dropwizard.util.Duration;
import org.mockito.internal.util.reflection.Whitebox;

public class NetworkSteps {
    private ScenarioState scenarioState;

    public NetworkSteps(ScenarioState scenarioState) {
        this.scenarioState = scenarioState;
    }

    @Given("^the network is slow$")
    public void setUpSlowNetwork() throws Throwable {
        Whitebox.setInternalState(scenarioState.hystrix()
            .scenarioCommandConfiguration().getExecution(), "isolationThreadTimeout", Duration.milliseconds(30));
        scenarioState.http().setSocketTimeOut(200);
        scenarioState.wiremock().setNetworkByteDribbleDelay(100);

        scenarioState.scenarioWait().setWaitAfterCommand(40);
    }
}
