package com.jspcore.ratpack.modules

import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import io.netty.handler.codec.http.HttpResponseStatus
import ratpack.groovy.test.handling.GroovyRequestFixture
import ratpack.test.handling.HandlingResult
import spock.lang.Specification

class PrometheusEndpointShould extends Specification {

  def "prometheus endpoint should display metrics"() {
    given:
    PrometheusMeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
    registry.counter("aCounter").increment()

    when:
    HandlingResult result = GroovyRequestFixture.handle(new PrometheusEndpoint(registry), { uri("") })

    then:
    result.status.code == HttpResponseStatus.OK.code()
    assert result.bodyText.contains("aCounter_total 1.0")
  }
}
