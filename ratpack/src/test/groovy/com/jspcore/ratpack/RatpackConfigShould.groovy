package com.jspcore.ratpack

import org.junit.Rule
import org.junit.contrib.java.lang.system.EnvironmentVariables
import ratpack.groovy.test.embed.GroovyEmbeddedApp
import spock.lang.Specification

import static ratpack.jackson.Jackson.json

class RatpackConfigShould extends Specification {

  @Rule
  public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

  static class Sample {
    public String field
    public String anotherField
  }

  def "parse configuration object"() {
    expect:
    GroovyEmbeddedApp.of {
      serverConfig {
        yaml Class.getResource("/application.yaml")
        require("/sample", Sample)
      }
      handlers {
        get { Sample config ->
          render json(config)
        }
      }
    } test {
      assert getText() == '{"field":"value","anotherField":"value2"}'
    }
  }

  def "parse configuration object using environment variables"() {
    expect:
    environmentVariables.set("SAMPLE__FIELD", "env-value1")
    environmentVariables.set("SAMPLE__ANOTHER_FIELD", "env-value2")
    GroovyEmbeddedApp.of {
      serverConfig {
        env("")
        require("/sample", Sample)
      }
      handlers {
        get { Sample config ->
          render json(config)
        }
      }
    } test {
      assert getText() == '{"field":"env-value1","anotherField":"env-value2"}'
    }
  }

  def "parse configuration object order of preference less to most important"() {
    expect:
    environmentVariables.set("SAMPLE__FIELD", "env-value1")
    environmentVariables.set("SAMPLE__ANOTHER_FIELD", "env-value2")
    GroovyEmbeddedApp.of {
      serverConfig {
        yaml Class.getResource("/application.yaml")
        env("")
        require("/sample", Sample)
      }
      handlers {
        get { Sample config ->
          render json(config)
        }
      }
    } test {
      assert getText() == '{"field":"env-value1","anotherField":"env-value2"}'
    }
  }

  def "parse configuration object having env vars overwriting only one field"() {
    expect:
    environmentVariables.set("SAMPLE__ANOTHER_FIELD", "env-value2")
    GroovyEmbeddedApp.of {
      serverConfig {
        yaml Class.getResource("/application.yaml")
        env("")
        require("/sample", Sample)
      }
      handlers {
        get { Sample config ->
          render json(config)
        }
      }
    } test {
      assert getText() == '{"field":"value","anotherField":"env-value2"}'
    }
  }
}
