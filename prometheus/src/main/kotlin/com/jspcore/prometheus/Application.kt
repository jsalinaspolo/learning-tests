package com.jspcore.prometheus

import com.google.common.io.Resources
import com.jspcore.prometheus.ratpack.Endpoints
import com.jspcore.prometheus.ratpack.MetricsConfig
import com.jspcore.prometheus.ratpack.PrometheusHandlerModule
import com.jspcore.prometheus.ratpack.PrometheusModule
import com.jspcore.prometheus.ratpack.RatpackBaseConfig
import com.jspcore.prometheus.ratpack.RequestTimingHandler
import ratpack.guice.Guice
import ratpack.handling.Context
import ratpack.server.RatpackServer

object Application {

  @JvmStatic
  fun main(args: Array<String>) {
    showBanner()

    RatpackServer.start { serverSpec ->
      serverSpec
        .serverConfig { config ->
          RatpackBaseConfig.forService(config)
            .require("/metrics", MetricsConfig::class.java)
        }
        .registry(
          Guice.registry { bindings ->
            bindings
              .module(PrometheusHandlerModule::class.java)
              .module(PrometheusModule::class.java)
              .bind(Endpoints::class.java)
          }
        )
        .handlers { chain ->
          chain.all(RequestTimingHandler::class.java)
            .get("", this::hello)
            .prefix("api", Endpoints::class.java)
        }
    }
  }

  private fun showBanner() {
    println(Resources.toString(Resources.getResource("banner.txt"), Charsets.UTF_8))
  }

  private fun hello(ctx: Context) {
    val banner = Resources.toString(Resources.getResource("banner.txt"), Charsets.UTF_8)
    ctx.render("Application Playground \n\n$banner")
  }
}
