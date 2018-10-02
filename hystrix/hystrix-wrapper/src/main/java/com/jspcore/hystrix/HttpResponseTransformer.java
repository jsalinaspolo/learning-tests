package com.jspcore.hystrix;

import org.apache.http.client.methods.CloseableHttpResponse;

public interface HttpResponseTransformer<T> {
  T transform(CloseableHttpResponse response) throws Exception;
}
