package state;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.pool.PoolStats;

import static java.util.concurrent.TimeUnit.SECONDS;

public class HttpClientState {
    private final PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
    private CloseableHttpClient client;

    public HttpClientState() {
        this.client = createClientWithSocketTimeoutMillis(30);
    }

    public PoolStats connectionPoolStats() {
        return connectionManager.getTotalStats();
    }

    public CloseableHttpClient client() {
        return client;
    }

    public void setSocketTimeOut(int socketTimeOut) {
        this.client = createClientWithSocketTimeoutMillis(socketTimeOut);
    }

    private CloseableHttpClient createClientWithSocketTimeoutMillis(int socketTimeout) {
        return HttpClientBuilder.create()
            .setDefaultRequestConfig(
                RequestConfig.custom()
                    .setConnectTimeout(10)
                    .setConnectionRequestTimeout(10)
                    .setSocketTimeout(socketTimeout)
                    .build())
            .setConnectionTimeToLive(60, SECONDS)
            .evictIdleConnections(60L, SECONDS)
            .setConnectionManager(connectionManager)
            .build();
    }
}
