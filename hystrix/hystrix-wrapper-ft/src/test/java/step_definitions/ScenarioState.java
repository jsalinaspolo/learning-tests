package step_definitions;

import com.codahale.metrics.MetricRegistry;
import org.mockito.Mockito;
import org.picocontainer.annotations.Cache;
import state.HttpClientState;
import state.HystrixState;
import state.JavaCallState;
import state.MDCState;
import state.ResultState;
import config.ScenarioCommandConfiguration;
import state.ScenarioWaitState;
import config.SharedGroupConfiguration;
import state.WiremockState;

import static org.mockito.Mockito.mock;

@Cache
public class ScenarioState {
    public static final String COMMAND_KEY = "COMMAND_KEY";
    public static final String GROUP_KEY = "GROUP_KEY";

    public static final String FALLBACK_VALUE = "fallback";
    public static final String SUCCESS_VALUE = "success";

    private ResultState resultState;

    private JavaCall mockJavaCall = mock(JavaCall.class);

    private HttpClientState httpState = new HttpClientState();
    private ScenarioWaitState scenarioWaitState = new ScenarioWaitState();
    private WiremockState wiremockState = new WiremockState(this);
    private JavaCallState javaCallState = new JavaCallState();
    private MDCState mdcState = new MDCState();
    private HystrixState hystrixState = new HystrixState(this);
    private final MetricRegistry metricRegistry = new MetricRegistry();
    private long timeTaken;
    private CommandType latestCommandType;

    public ResultState getResultState() {
        return resultState;
    }

    public void setResultState(ResultState resultState, long timeTaken) {
        this.resultState = resultState;
        this.timeTaken = timeTaken;
    }

    public SharedGroupConfiguration createSharedGroupConfigurationWithNewCommand(String groupKey) {
        return new SharedGroupConfiguration(groupKey, new ScenarioCommandConfiguration(COMMAND_KEY));
    }

    public long timeTaken() {
        return timeTaken;
    }

    public HystrixState hystrix() {
        return hystrixState;
    }

    public MetricRegistry getMetricsRegistry() {
        return metricRegistry;
    }

    public JavaCallState javaCall() {
        return javaCallState;
    }

    public void setLatestCommandType(CommandType latestCommandType) {
        this.latestCommandType = latestCommandType;
    }

    public CommandType getLatestCommandType() {
        return latestCommandType;
    }

    public MDCState mdc() {
        return mdcState;
    }

    public void clear() {
        resultState = null;
        Mockito.reset(mockJavaCall);
        httpState = new HttpClientState();
        scenarioWaitState = new ScenarioWaitState();
        mdcState = new MDCState();
        hystrixState = new HystrixState(this);
    }

    public interface JavaCall {
        String call();
    }

    public JavaCall getMockJavaCall() {
        return mockJavaCall;
    }

    public ScenarioWaitState scenarioWait() {
        return scenarioWaitState;
    }

    public HttpClientState http() {
        return httpState;
    }

    public WiremockState wiremock() {
        return wiremockState;
    }
}
