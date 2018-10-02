package step_definitions;

import cucumber.api.java.en.When;
import state.result.ExecuteResultState;
import state.result.QueueResultState;

import java.util.concurrent.Future;

public class CommonSteps {
    private ScenarioState scenarioState;

    public CommonSteps(ScenarioState scenarioState) {
        this.scenarioState = scenarioState;
    }

    @When("^it is executed(?:| again)$")
    public void executeTheCommand() throws Throwable {
        long startTime = System.currentTimeMillis();

        try {
            String response = scenarioState.hystrix().getCommand().execute();
            scenarioState.setResultState(ExecuteResultState.successWith(response), System.currentTimeMillis() - startTime);
        } catch (Throwable t) {
            scenarioState.setResultState(ExecuteResultState.failureWith(t), System.currentTimeMillis() - startTime);
        }

        Thread.sleep(scenarioState.scenarioWait().getWaitAfterCommand());
        scenarioState.scenarioWait().setWaitAfterCommand(0);
    }

    @When("^it is queued$")
    public void queueTheCommand() throws Throwable {
        long startTime = System.currentTimeMillis();

        try {
            Future<String> result = scenarioState.hystrix().getCommand().queue();
            scenarioState.setResultState(QueueResultState.successWith(result), System.currentTimeMillis() - startTime);
        } catch (Throwable t) {
            scenarioState.setResultState(QueueResultState.failureWith(t), System.currentTimeMillis() - startTime);
        }

        int waitAfterCommand = scenarioState.scenarioWait().getWaitAfterCommand();
        Thread.sleep(waitAfterCommand == 0 ? 500 : waitAfterCommand);
        scenarioState.scenarioWait().setWaitAfterCommand(0);
    }
}
