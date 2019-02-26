package com.jspcore.ratpack

import org.junit.Rule
import org.junit.contrib.java.lang.system.EnvironmentVariables
import ratpack.groovy.test.embed.GroovyEmbeddedApp
import spock.lang.Specification

import static ratpack.jackson.Jackson.json

class RatpackConfigShould extends Specification {

  @Rule
  public final EnvironmentVariables environmentVariables = new EnvironmentVariables()

  static class Sample {
    public String field
    public String anotherField
    public Map<String, String> mapField
    public List<String> listField
  }

  def "parse configuration object"() {
    expect:
    GroovyEmbeddedApp.ratpack {
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
      assert getText().contains('"field":"value","anotherField":"value2"')
    }
  }

  def "parse configuration object using environment variables"() {
    expect:
    environmentVariables.set("SAMPLE__FIELD", "env-value1")
    environmentVariables.set("SAMPLE__ANOTHER_FIELD", "env-value2")
    GroovyEmbeddedApp.ratpack {
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
      assert getText().contains('"field":"env-value1","anotherField":"env-value2"')
    }
  }

  def "parse configuration object order of preference less to most important"() {
    expect:
    environmentVariables.set("SAMPLE__FIELD", "env-value1")
    environmentVariables.set("SAMPLE__ANOTHER_FIELD", "env-value2")
    GroovyEmbeddedApp.ratpack {
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
      assert getText().contains('{"field":"env-value1","anotherField":"env-value2"')
    }
  }

  def "parse configuration object having env vars overwriting only one field"() {
    expect:
    environmentVariables.set("SAMPLE__ANOTHER_FIELD", "env-value2")
    GroovyEmbeddedApp.ratpack {
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
      assert getText().contains('{"field":"value","anotherField":"env-value2"')
    }
  }

  def "parse configuration object with map"() {
    expect:
    GroovyEmbeddedApp.ratpack {
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
      assert getText().contains('{"item1":"value1","item2":"value2"}')
    }
  }

  def "parse configuration object with list"() {
    expect:
    GroovyEmbeddedApp.ratpack {
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
      assert getText().contains('"listField":["item1","item2","item3"]')
    }
  }

  def "parse configuration object with list and override with env variable"() {
    expect:
    environmentVariables.set("SAMPLE__MAP_FIELD__ITEM1", "anotherValue1")
    environmentVariables.set("SAMPLE__MAP_FIELD__ITEM2", "anotherValue2")
    environmentVariables.set("SAMPLE__MAP_FIELD__ITEM3", "anotherValue3")

    GroovyEmbeddedApp.ratpack {
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
      assert getText().contains('{"item1":"anotherValue1","item2":"anotherValue2","item3":"anotherValue3"}')
    }
  }

}
