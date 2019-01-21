package com.jspcore.prometheus.ratpack

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import ratpack.func.Action
import ratpack.server.BaseDir
import ratpack.server.ServerConfigBuilder

object RatpackBaseConfig {

  @JvmStatic
  fun forService(serverConfigBuilder: ServerConfigBuilder): ServerConfigBuilder {
    serverConfigBuilder
        .baseDir(BaseDir.find())
        .configureObjectMapper(JacksonConfig())
        .yaml("application.yaml")
        .env("")
    return serverConfigBuilder
  }

  private class JacksonConfig : Action<ObjectMapper> {
    override fun execute(objectMapper: ObjectMapper) {
      objectMapper.registerModule(KotlinModule())
    }
  }
}
