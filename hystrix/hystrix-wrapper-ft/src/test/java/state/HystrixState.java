package state;

import com.jspcore.hystrix.Command;
import com.jspcore.hystrix.CommandFactory;
import com.jspcore.hystrix.FailureListener;
import com.jspcore.hystrix.HttpResponseTransformer;
import com.jspcore.hystrix.config.CommandConfiguration;
import com.jspcore.hystrix.config.CommandGroupConfiguration;
import com.jspcore.hystrix.config.CommandManager;
import com.netflix.config.ConfigurationManager;
import config.ScenarioCommandConfiguration;
import config.SharedGroupConfiguration;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import step_definitions.CommandType;
import step_definitions.ScenarioState;

import static org.mockito.Mockito.mock;
import static step_definitions.ScenarioState.COMMAND_KEY;

public class HystrixState {

    public static final int DEFAULT_QUEUE_SIZE = -1;
    public static final int DEFAULT_QUEUE_SIZE_REJECTION_THRESHOLD = 5;
    private ScenarioState scenarioState;
    private ScenarioCommandConfiguration scenarioCommandConfiguration = new ScenarioCommandConfiguration(COMMAND_KEY);
    private CommandGroupConfiguration.ThreadpoolConfiguration threadpoolConfiguration =
        new CommandGroupConfiguration.ThreadpoolConfiguration();
    private SharedGroupConfiguration testGroupConfiguration = new SharedGroupConfiguration(
        ScenarioState.GROUP_KEY, threadpoolConfiguration, scenarioCommandConfiguration);
    private Command<String> command;
    private CommandType commandType;
    private CommandManager commandManager = new CommandManager(ConfigurationManager.getConfigInstance());

    @SuppressWarnings("unchecked")
    private FailureListener<String> mockFailureListener = mock(FailureListener.class);

    public HystrixState(ScenarioState scenarioState) {
        this.scenarioState = scenarioState;
    }

    public SharedGroupConfiguration scenarioGroupConfiguration() {
        return testGroupConfiguration;
    }

    public CommandConfiguration scenarioCommandConfiguration() {
        return scenarioCommandConfiguration;
    }

    public void installConfig() {
        commandManager.install(testGroupConfiguration);
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public Command<String> getCommand() {
        return command;
    }

    public void setThreadPoolWithTotalOf(int threads) {
        threadpoolConfiguration =
            new CommandGroupConfiguration.ThreadpoolConfiguration(threads,
                DEFAULT_QUEUE_SIZE,
                DEFAULT_QUEUE_SIZE_REJECTION_THRESHOLD
            );
    }

    public void setCommand(Command<String> command) {
        this.command = command;
    }

    public FailureListener<?> getMockFailureListener() {
        return mockFailureListener;
    }

    public void setUpCommand(CommandType commandType, String fallbackType, String failListener) {
        setUpCommand(commandType, fallbackType, failListener, null);
    }

    public void setUpCommand(
        CommandType commandType, String fallbackType, String failListener,
        HttpResponseTransformer<String> httpResponseTransformer
    ) {
        setUpCommand(
            commandType,
            fallbackType,
            failListener,
            CommandFactory.withHttpCapability(
                scenarioState.getMetricsRegistry(),
                testGroupConfiguration,
                scenarioState.http().client()
            ),
            testGroupConfiguration,
            httpResponseTransformer
        );
    }

    public void setUpCommand(
        CommandType commandType, String fallbackType, String failListener, CommandFactory commandFactory,
        SharedGroupConfiguration groupConfiguration, HttpResponseTransformer<String> transformer
    ) {
        this.commandType = commandType;

        boolean withFallback = "fallback".equals(fallbackType);
        boolean withFailListener = "failure listener".equals(failListener);

        System.out.println("Creating command: " + commandType + ", withFallBack: " + withFallback);

        Command<String> command = createCommand(commandType, commandFactory, groupConfiguration, transformer);

        if (withFallback) {
            command = command.withFallbackTo(ScenarioState.FALLBACK_VALUE);
        }

        if (withFailListener) {
            command = command.withFailListener(mockFailureListener);
        }
        setCommand(command);
    }

    private Command<String> createCommand(
        CommandType commandType, CommandFactory commandFactory, SharedGroupConfiguration groupConfiguration,
        HttpResponseTransformer<String> transformer
    ) {
        switch (commandType) {
            default:
            case Java: return createJavaCommand(commandFactory, groupConfiguration);
            case HTTP: return createHttpCommand(commandFactory, groupConfiguration, transformer);
        }
    }

    private Command<String> createHttpCommand(
        CommandFactory commandFactory, SharedGroupConfiguration groupConfiguration,
        HttpResponseTransformer<String> transformer
    ) {
        if (transformer == null) {
            transformer = defaultHttpResponseTransformer();
        }
        return  commandFactory.buildHttpCommand(groupConfiguration.getCommandConfiguration(),
            new HttpPost(scenarioState.wiremock().testEndpointUrl()),
            transformer
        );
    }

    private HttpResponseTransformer<String> defaultHttpResponseTransformer() {
        return response -> {
            scenarioState.mdc().saveRequestId();
            return EntityUtils.toString(response.getEntity());
        };
    }

    private Command<String> createJavaCommand(
        CommandFactory commandFactory, SharedGroupConfiguration groupConfiguration
    ) {
        return commandFactory.buildCommand(groupConfiguration.getCommandConfiguration(),
            () -> {
                scenarioState.mdc().saveRequestId();
                return scenarioState.getMockJavaCall().call();
            }
        );
    }
}
