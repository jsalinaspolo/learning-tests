package step_definitions;

import com.jspcore.hystrix.KillSwitchWithoutFallbackException;
import com.codahale.metrics.Meter;
import com.github.tomakehurst.wiremock.client.WireMock;
import cucumber.api.java.en.And;
import cucumber.api.java.en.But;
import cucumber.api.java.en.Then;

import static com.codahale.metrics.MetricRegistry.name;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyZeroInteractions;

public class KillSwitchSteps {

    private ScenarioState scenarioState;

    public KillSwitchSteps(ScenarioState scenarioState) {
        this.scenarioState = scenarioState;
    }

    @And("^the command has been kill switched$")
    public void the_command_has_been_kill_switched() throws Throwable {
        scenarioState.hystrix().scenarioCommandConfiguration().getKillSwitch().enable();
    }

    @And("^the hystrix ([a-zA-Z]*?) command has not been called at all$")
    public void the_hystrix_command_has_not_been_called_at_all(CommandType type) throws Throwable {
        switch (type) {
            case HTTP:
                WireMock.verify(0, postRequestedFor(urlEqualTo(scenarioState.wiremock().testEndpointPath())));
                break;
            case Java:
                verifyZeroInteractions(scenarioState.getMockJavaCall());
                break;

        }

    }

    @Then("^a KillSwitchWithoutFallback exception is thrown$")
    public void a_KillSwitchWithoutFallback_exception_is_thrown() throws Throwable {
        assertThat(scenarioState.getResultState().thrownException())
            .describedAs("Exception thrown by execute")
            .isNotNull()
            .isInstanceOf(KillSwitchWithoutFallbackException.class);
    }

    @But("^the kill switch metric is produced$")
    public void the_kill_switch_metric_is_produced() throws Throwable {

        Meter killSwitchedMeter =
            scenarioState.getMetricsRegistry()
                .meter(name(scenarioState.hystrix().scenarioGroupConfiguration().getGroupKey(),
                    scenarioState.hystrix().scenarioCommandConfiguration().getCommandKey(),
                    "killSwitched"));

        assertThat(killSwitchedMeter.getCount()).isEqualTo(1);
    }

    @And("^the kill switch metric is not produced$")
    public void the_kill_switch_metric_is_not_produced() throws Throwable {
        assertThat(scenarioState.getMetricsRegistry().getTimers()).hasSize(0);
        assertThat(scenarioState.getMetricsRegistry().getMeters()).hasSize(0);
        assertThat(scenarioState.getMetricsRegistry().getGauges()).hasSize(0);
        assertThat(scenarioState.getMetricsRegistry().getHistograms()).hasSize(0);
    }
}
