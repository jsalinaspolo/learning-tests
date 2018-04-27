package com.jspcore.ratpack

import com.jspcore.ratpack.modules.PrometheusModule
import ratpack.groovy.server.GroovyRatpackServerSpec
import ratpack.groovy.test.embed.GroovyEmbeddedApp
import ratpack.guice.Guice
import spock.lang.Specification

class ApplicationShould extends Specification {

  def "publish prometheus metrics on endpoint /metrics"() {
    expect:
    GroovyEmbeddedApp.of { GroovyRatpackServerSpec server ->
      server.registry {
        Guice.registry { b -> b.module(PrometheusModule.class) }
      }.handlers { get { render "{}" } }
    }.test {
      def response = get("metrics")
      response.status.code == 200
      assert response.body.text.contains("")
    }
  }
}
