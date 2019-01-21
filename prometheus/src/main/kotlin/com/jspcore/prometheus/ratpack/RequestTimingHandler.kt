package com.jspcore.prometheus.ratpack

import io.micrometer.core.instrument.MeterRegistry
import ratpack.handling.Context
import ratpack.handling.Handler
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RequestTimingHandler @Inject constructor(
  private val meterRegistry: MeterRegistry,
  private val metricsConfig: MetricsConfig) : Handler {

  override fun handle(ctx: Context) {
    ctx.onClose { outcome ->
      val statusCode = outcome.response.status.code.toString()
      meterRegistry.timer("http.requests",
          "status", statusCode,
          "path", findPathGroup(outcome.request.path),
          "method", outcome.request.method.name.toLowerCase())
          .record(outcome.duration.toNanos(), TimeUnit.NANOSECONDS)
      meterRegistry.timer("http.server.requests", "status", statusCode).record(outcome.duration.toNanos(), TimeUnit.NANOSECONDS)
    }

    ctx.next()
  }

  private fun findPathGroup(requestPath: String): String {
    var tagName= if (requestPath == "") "root" else requestPath

    for (it in metricsConfig.groups.entries) {
      val regex = it.value.toRegex()
      val match = regex.find(requestPath)
      if (match != null) {
        tagName = it.key

        if(match.groups.isNotEmpty()) {
          match.groups.forEachIndexed { index, matchGroup ->
            tagName = tagName.replace("$$index", matchGroup?.value!!, false)
          }
        }
        break
      }
    }
    return tagName
  }
}
