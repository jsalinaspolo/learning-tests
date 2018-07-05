package com.jspcore.spockme

import com.tngtech.jgiven.annotation.ScenarioStage
import com.tngtech.jgiven.junit.JGivenClassRule
import com.tngtech.jgiven.junit.JGivenMethodRule
import org.junit.ClassRule
import org.junit.Rule
import spock.lang.Shared
import spock.lang.Specification

class MyShinyJGivenSpec extends Specification {

  @ClassRule @Shared
  JGivenClassRule writerRule = new JGivenClassRule()

  @Rule
  JGivenMethodRule scenarioRule = new JGivenMethodRule()

  @ScenarioStage
  GivenSomeState someState

  @ScenarioStage
  WhenSomeAction someAction

  @ScenarioStage
  ThenSomeOutcome someOutcome

  def something_should_happen() {
    expect:
    someState.given().some_state()
    someAction.when()."some action bro"()
    someOutcome.then().some_outcome()
  }

}
