package com.jspcore.prometheus.ratpack

import io.micrometer.core.instrument.MeterRegistry
import ratpack.exec.Promise
import ratpack.func.Action
import ratpack.handling.Chain
import java.time.Duration
import javax.inject.Inject
import kotlin.random.Random
import kotlin.random.nextLong

class Endpoints @Inject constructor(val meterRegistry: MeterRegistry): Action<Chain> {
  val random = Random(1L)
  override fun execute(chain: Chain) {
    chain
      .get("something") { ctx ->
        Promise.value(meterRegistry.counter("ticks").increment()).defer(Duration.ofMillis(random.nextLong(LongRange(200, 800))))
          .then { result -> ctx.response.send("OK") }
      }
  }
}
