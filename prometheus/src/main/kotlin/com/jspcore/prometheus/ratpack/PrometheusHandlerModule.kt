package com.jspcore.prometheus.ratpack

import com.google.inject.AbstractModule
import com.google.inject.multibindings.Multibinder
import ratpack.handling.HandlerDecorator
import ratpack.handling.Handlers.chain

class PrometheusHandlerModule : AbstractModule() {

  override fun configure() {
    bind(PrometheusHandler::class.java)
    bind(RequestTimingHandler::class.java)

    Multibinder.newSetBinder(binder(), HandlerDecorator::class.java)
        .addBinding()
        .toInstance(
            HandlerDecorator { registry, rest ->
              chain(chain(registry) { chain ->
                chain.get("metrics", PrometheusHandler::class.java)
              }, rest)
            })
  }

}
