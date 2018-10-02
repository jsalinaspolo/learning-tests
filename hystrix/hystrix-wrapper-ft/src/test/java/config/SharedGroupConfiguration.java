package config;

import com.jspcore.hystrix.config.CommandConfiguration;
import com.jspcore.hystrix.config.CommandGroupConfiguration;
import com.google.common.collect.ImmutableList;

import java.util.List;

public class SharedGroupConfiguration extends CommandGroupConfiguration {

    private static final String METRIC_INFIX = "metric.infix";

    public CommandConfiguration getCommandConfiguration() {
        return commandConfiguration;
    }

    private final CommandConfiguration commandConfiguration;

    public SharedGroupConfiguration(String groupKey, ScenarioCommandConfiguration commandConfiguration) {
        super(groupKey, METRIC_INFIX);
        this.commandConfiguration = commandConfiguration;
    }

    public SharedGroupConfiguration(String groupKey,
                                    ThreadpoolConfiguration threadpoolConfiguration,
                                    CommandConfiguration commandConfiguration) {
        super(groupKey, METRIC_INFIX, threadpoolConfiguration);
        this.commandConfiguration = commandConfiguration;
    }

    @Override
    public List<CommandConfiguration> getCommands() {
        return ImmutableList.of(commandConfiguration);
    }
}
