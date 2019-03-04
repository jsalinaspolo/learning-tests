package com.jspcore.ratpack


import ratpack.exec.Operation
import ratpack.exec.Promise
import ratpack.test.exec.ExecHarness
import spock.lang.AutoCleanup
import spock.lang.Specification

class WiretapShould extends Specification {

  @AutoCleanup
  ExecHarness execHarness = ExecHarness.harness()

  def "promise wiretap"() {
    expect:
    List<String> elements = []
    execHarness.yield {
      Promise.async({ downstream ->
        elements.add("1")
        downstream.success(elements)
      }).wiretap { elements.add("2") }
    }

    elements == ["1", "2"]
  }

  def "promise wiretap on error"() {
    expect:
    List<String> elements = []
    execHarness.yield {
      Promise.async({ downstream ->
        elements.add("1")
        throw new IllegalStateException("Some error")
      }).wiretap { elements.add("2") }
        .onError { elements.add("3") }
    }

    elements == ["1", "2", "3"]
  }

  def "operation wiretap"() {
    expect:
    List<String> elements = []
    ExecHarness.runSingle {
      Operation.of {
        elements.add("1")
      }
      .wiretap { elements.add("2") }
        .then { elements.add("3") }
    }
    elements == ["1", "2", "3"]
  }

  def "operation wiretap on error"() {
    expect:
    List<String> elements = []
    ExecHarness.runSingle {
      Operation.of { downstream ->
        elements.add("1")
        throw new IllegalStateException("Some error")
      }.wiretap { elements.add("2") }
        .onError { elements.add("4") }
        .then { elements.add("3") }
    }

    elements == ["1", "2", "4"]
  }
}
