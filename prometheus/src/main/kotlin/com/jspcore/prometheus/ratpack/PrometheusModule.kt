package com.jspcore.prometheus.ratpack

import com.google.inject.Provides
import io.micrometer.core.instrument.Meter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics
import io.micrometer.core.instrument.binder.logging.LogbackMetrics
import io.micrometer.core.instrument.binder.system.ProcessorMetrics
import io.micrometer.core.instrument.binder.system.UptimeMetrics
import io.micrometer.core.instrument.config.MeterFilter
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig
import java.time.Duration
import javax.inject.Singleton

class PrometheusModule : MeterRegistryModule() {

  @Provides
  @Singleton
  override fun providesMeterRegistry(config: MetricsConfig): MeterRegistry {
    prometheus.config().meterFilter(object : MeterFilter {
      override fun configure(id: Meter.Id, config: DistributionStatisticConfig) =
          DistributionStatisticConfig.builder()
              .percentilesHistogram(true)
              .minimumExpectedValue(Duration.ofMillis(10).toNanos())
              .maximumExpectedValue(Duration.ofSeconds(5).toNanos())
              .build()
              .merge(config)
    })

    prometheus.config().commonTags("application", config.application)

    composite.add(prometheus)

    ClassLoaderMetrics().bindTo(composite)
    JvmMemoryMetrics().bindTo(composite)
    JvmGcMetrics().bindTo(composite)
    ProcessorMetrics().bindTo(composite)
    JvmThreadMetrics().bindTo(composite)
    LogbackMetrics().bindTo(composite)
    UptimeMetrics().bindTo(composite)


    return composite
  }
}
