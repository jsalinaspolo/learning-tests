package com.jspcore.hystrix;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Optional;
import com.jspcore.hystrix.config.CommandConfiguration;
import com.jspcore.hystrix.config.CommandGroupConfiguration;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

public class HttpCommand<T> extends Command<T> {
  protected final CloseableHttpClient closeableHttpClient;
  protected final HttpUriRequest httpRequest;
  protected final HttpResponseTransformer<T> httpResponseTransformer;

  HttpCommand(
    MetricRegistry metricRegistry, CloseableHttpClient closeableHttpClient,
    CommandGroupConfiguration client,
    CommandConfiguration command,
    HttpUriRequest httpRequest,
    HttpResponseTransformer<T> transformer
  ) {
    this(metricRegistry, closeableHttpClient, client, command, httpRequest, transformer,
      Optional.absent(), Optional.absent());
  }

  HttpCommand(
    MetricRegistry metricRegistry,
    CloseableHttpClient closeableHttpClient,
    CommandGroupConfiguration client,
    CommandConfiguration command,
    HttpUriRequest httpRequest,
    HttpResponseTransformer<T> httpResponseTransformer,
    Optional<T> fallbackValue,
    Optional<FailureListener<T>> failureListener
  ) {
    super(metricRegistry, client, command, fallbackValue, failureListener);
    this.closeableHttpClient = closeableHttpClient;
    this.httpRequest = httpRequest;
    this.httpResponseTransformer = httpResponseTransformer;
  }

  @Override
  protected void onFailure(Throwable failedExecutionException, Optional<T> result) {
    httpRequest.abort();
    if (failureListener.isPresent()) {
      failureListener.get().onFailure(failedExecutionException, result);
    }
  }

  @Override
  protected T runAction() throws Exception {
    CloseableHttpResponse httpResponse = null;
    try {
      httpResponse = closeableHttpClient.execute(httpRequest);
      return httpResponseTransformer.transform(httpResponse);
    } finally {
      if (httpResponse != null) {
        EntityUtils.consumeQuietly(httpResponse.getEntity());
        httpResponse.close();
      }
    }
  }

  @Override
  public HttpCommand<T> withFallbackTo(T fallbackValue) {
    return new HttpCommand<>(metricRegistry,
      closeableHttpClient,
      client,
      command,
      httpRequest,
      httpResponseTransformer,
      Optional.of(fallbackValue),
      failureListener
    );
  }

  @Override
  public HttpCommand<T> withFailListener(FailureListener<T> failureListener) {
    return new HttpCommand<>(metricRegistry,
      closeableHttpClient,
      client,
      command,
      httpRequest,
      httpResponseTransformer,
      fallbackValue,
      Optional.of(failureListener)
    );
  }
}
