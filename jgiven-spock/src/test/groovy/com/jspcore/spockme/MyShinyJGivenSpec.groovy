package com.jspcore.spockme

import com.jspcore.spockme.stages.GivenSomeState
import com.jspcore.spockme.stages.ThenSomeOutcome
import com.jspcore.spockme.stages.WhenSomeAction
import com.tngtech.jgiven.annotation.Description

class MyShinyJGivenSpec extends ScenarioSpec<GivenSomeState, WhenSomeAction, ThenSomeOutcome> {


  def something_should_happen() {
    expect:

    given().some_state()
    when().some_action_bro()
    then().some_outcome()
  }
}
