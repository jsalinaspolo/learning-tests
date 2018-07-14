package com.jspcore.spockme

class MyShinyJGivenSpec extends ScenarioSpec<GivenSomeState, WhenSomeAction, ThenSomeOutcome> {


  def something_should_happen() {
    expect:

    given().some_state()
    when().some_action_bro()
    then().some_outcome()
  }
}
