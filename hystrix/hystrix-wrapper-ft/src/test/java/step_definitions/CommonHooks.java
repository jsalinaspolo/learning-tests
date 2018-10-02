package step_definitions;

import com.netflix.config.ConfigurationManager;
import com.netflix.hystrix.Hystrix;
import cucumber.api.java.After;
import cucumber.api.java.Before;

import java.util.concurrent.TimeUnit;

public class CommonHooks {
    private final ScenarioState scenarioState;

    public CommonHooks(ScenarioState scenarioState) {
        this.scenarioState = scenarioState;
    }

    @After
    public void clearHystrixProperties() {
        ConfigurationManager.getConfigInstance().clear();
    }

    @After
    public void clearScenarioState() throws InterruptedException {
        scenarioState.clear();
    }

    @Before
    public void resetHystrix() {
        Hystrix.reset(1, TimeUnit.SECONDS);
    }

    @Before("@HTTP")
    public void setUpWiremock() {
        scenarioState.wiremock().start();
        System.out.println("Running: " + scenarioState.wiremock().isRunning());
    }

    @After("@HTTP")
    public void resetWiremock() {
        scenarioState.wiremock().stop();
    }

}
