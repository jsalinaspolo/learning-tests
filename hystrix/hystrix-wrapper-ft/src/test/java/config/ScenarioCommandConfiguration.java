package config;

import com.jspcore.hystrix.config.CommandConfiguration;

public class ScenarioCommandConfiguration extends CommandConfiguration {
    public ScenarioCommandConfiguration(String commandKey) {
        super(commandKey);
    }
}
