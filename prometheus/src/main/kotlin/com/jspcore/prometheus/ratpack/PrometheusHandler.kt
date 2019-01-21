package com.jspcore.prometheus.ratpack

import io.micrometer.prometheus.PrometheusMeterRegistry
import ratpack.handling.Context
import ratpack.handling.Handler
import ratpack.http.Status
import javax.inject.Inject

class PrometheusHandler @Inject constructor(
    private val registry: PrometheusMeterRegistry
) : Handler {

  override fun handle(ctx: Context) =
      ctx.response.status(Status.OK).send(registry.scrape())

}
