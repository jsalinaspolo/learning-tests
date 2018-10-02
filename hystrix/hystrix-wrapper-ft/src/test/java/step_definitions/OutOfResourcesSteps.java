package step_definitions;

import com.jspcore.hystrix.FailureWithoutFallbackException;
import com.netflix.config.ConfigurationManager;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import cucumber.api.java.After;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import org.mockito.internal.util.reflection.Whitebox;

import java.util.concurrent.RejectedExecutionException;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

public class OutOfResourcesSteps {
    public static final String OTHER_COMMAND_USING_RESOURCES = "otherCommandUsingResources";
    private final ScenarioState scenarioState;
    private StoppableCommand stoppableCommand;

    public OutOfResourcesSteps(ScenarioState scenarioState) {
        this.scenarioState = scenarioState;
    }

    @And("^there are no more resources to start a thread$")
    public void setNoMoreResources() throws Throwable {
        //Wait for all previous threads to finish
        ConfigurationManager.getConfigInstance().addProperty(
            format("hystrix.command.%s.circuitBreaker.enabled", OTHER_COMMAND_USING_RESOURCES), "false");

        Whitebox.setInternalState(scenarioState.hystrix().scenarioGroupConfiguration().getThreadPool(), "coreSize", 1);
        scenarioState.hystrix().installConfig();

        int max = 20;
        for (int i = 0; i < max; i++) {
            try {
                System.out.println("Seizing scarce resources..." + i);
                stoppableCommand = new StoppableCommand();
                stoppableCommand.queue();
                break;
            } catch (RuntimeException e) {
                System.out.println("Failed claiming thread for otherCommandUsingResources:" + e.getMessage() + ", retrying... " + i);
                if (i == max - 1) {
                    throw e;
                }
                Thread.sleep(300);
            }
        }

    }

    @After("@OutOfResources")
    public void stopUsingResources() throws InterruptedException {
        stoppableCommand.stop();
        Thread.sleep(200);
    }

    @Then("^an OutOfResourcesException is thrown$")
    public void assertThatOutOfResourcesExceptionHasBeenThrown() throws Throwable {
        System.out.println(scenarioState.getResultState().successResult());
        assertThat(scenarioState.getResultState().thrownException())
                .describedAs("Exception thrown by execute")
                .isNotNull()
                .isInstanceOf(FailureWithoutFallbackException.class);
        assertThat(scenarioState.getResultState().thrownException().getCause())
                .hasCauseExactlyInstanceOf(RejectedExecutionException.class);
        scenarioState.getResultState().thrownException().printStackTrace();
    }


    private class StoppableCommand extends HystrixCommand<String> {
        private boolean stillRunning = true;

        private StoppableCommand() {
            super(HystrixCommand.Setter
                    .withGroupKey(HystrixCommandGroupKey.Factory.asKey(ScenarioState.GROUP_KEY))
                    .andCommandKey(HystrixCommandKey.Factory.asKey(OTHER_COMMAND_USING_RESOURCES)));
        }

        @Override
        protected String run() throws Exception {
            while(stillRunning) {
                try {
                    Thread.sleep(5);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Releasing resources.");
            return "other thread succeeded";
        }

        public void stop() {
            this.stillRunning = false;
        }
    }
}
