package state;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import step_definitions.ScenarioState;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

public class WiremockState {
    private ScenarioState scenarioState;
    private int responseDelay;
    private String testEndpointPath = "/some-request";
    private String responseBody = "success";

    private int port = 9090;
    private String host = "localhost";

    private WireMockServer wireMockServer;
    private int networkByteDribbleDelay = 0;

    public WiremockState(ScenarioState scenarioState) {
        this.scenarioState = scenarioState;
        wireMockServer = new WireMockServer(wireMockConfig().port(port));
    }

    public String baseUrl() {
        return "http://" + host + ":" + port;
    }

    public void start() {
        if (!wireMockServer.isRunning()) {
            wireMockServer.start();
            WireMock.configureFor(host, port);
        } else {
            new WireMock(host, port).resetMappings();
        }
    }

    public String testEndpointUrl() {
        return scenarioState.wiremock().baseUrl() + testEndpointPath;
    }

    public void stop() {
        if (wireMockServer.isRunning()) {
            wireMockServer.stop();
        }
    }

    public void setResponseDelay(int responseDelay) {
        this.responseDelay = responseDelay;
    }

    public int responseDelay() {
        return responseDelay;
    }

    public String testEndpointPath() {
        return testEndpointPath;
    }

    public String responseBody() {
        return responseBody;
    }

    public boolean isRunning() {
        return wireMockServer.isRunning();
    }

    public void setNetworkByteDribbleDelay(int networkByteDribbleDelay) {
        this.networkByteDribbleDelay = networkByteDribbleDelay;
    }

    public int networkByteDribbleDelay() {
        return networkByteDribbleDelay;
    }
}
