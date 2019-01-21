package com.jspcore.prometheus.ratpack

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.composite.CompositeMeterRegistry
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import ratpack.guice.ConfigurableModule

abstract class MeterRegistryModule(
  protected val prometheus: PrometheusMeterRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT),
  protected val composite: CompositeMeterRegistry = CompositeMeterRegistry()
) : ConfigurableModule<MetricsConfig>() {

  override fun configure() {
    bind(PrometheusMeterRegistry::class.java).toInstance(prometheus)
    bind(CompositeMeterRegistry::class.java).toInstance(composite)
    bind(RequestTimingHandler::class.java)
  }

  abstract fun providesMeterRegistry(config: MetricsConfig) : MeterRegistry
}

data class MetricsConfig(val application: String, val groups: Map<String, String> = mapOf())
