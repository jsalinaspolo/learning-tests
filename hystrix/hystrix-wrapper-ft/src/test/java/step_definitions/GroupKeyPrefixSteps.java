package step_definitions;

import com.jspcore.hystrix.CommandFactory;
import config.SharedGroupConfiguration;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import io.dropwizard.util.Duration;
import org.mockito.internal.util.reflection.Whitebox;

import static org.mockito.Mockito.when;

public class GroupKeyPrefixSteps {
    private ScenarioState scenarioState;

    public GroupKeyPrefixSteps(ScenarioState scenarioState) {
        this.scenarioState = scenarioState;
    }

    private SharedGroupConfiguration groupConfiguration;
    private CommandFactory commandFactory;

    private SharedGroupConfiguration otherGroupConfiguration;
    private CommandFactory otherCommandFactory;


    @Given("^a Java command waiting longer than its hystrix timeout$")
    public void setUpSlowCommand() throws Throwable {
        final int commandDelay = 30;

        when(scenarioState.getMockJavaCall().call()).thenAnswer(invocationOnMock -> {
            Thread.sleep(commandDelay);
            return "success";
        });

        scenarioState.hystrix()
            .setUpCommand(CommandType.Java, null, null,
                          commandFactory,
                          groupConfiguration, null);
    }

    @When("^there is another Java command with the same name in another group$")
    public void setUpCommandInAnotherGroup() throws Throwable {
        final int commandDelay = 30;

        when(scenarioState.getMockJavaCall().call()).thenAnswer(invocationOnMock -> {
            Thread.sleep(commandDelay);
            return "success";
        });

        scenarioState.hystrix()
            .setUpCommand(CommandType.Java, null, null,
                          otherCommandFactory,
                          otherGroupConfiguration, null);
    }

    @Given("^hystrix commandFactories have been configured for both groups$")
    public void setUpCommandFactories() throws Throwable {
        groupConfiguration = scenarioState.hystrix().scenarioGroupConfiguration();
        commandFactory = CommandFactory.withHttpCapability(
            scenarioState.getMetricsRegistry(),
            groupConfiguration,
            scenarioState.http().client());

        otherGroupConfiguration = scenarioState.createSharedGroupConfigurationWithNewCommand("OTHER" + scenarioState.GROUP_KEY);
        otherCommandFactory = CommandFactory.withHttpCapability(
            scenarioState.getMetricsRegistry(),
            otherGroupConfiguration,
            scenarioState.http().client());
    }

    @And("^hystrix timeout is set for the first command$")
    public void installCommandConfig() throws Throwable {
        final Duration hystrixTimeout = Duration.milliseconds(10);
        Whitebox.setInternalState(scenarioState.hystrix().scenarioCommandConfiguration()
                                      .getExecution(), "isolationThreadTimeout", hystrixTimeout);
        scenarioState.hystrix().installConfig();
    }
}
