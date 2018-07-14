package com.jspcore.spockme

import com.jspcore.spockme.stages.GivenSomeState
import com.jspcore.spockme.stages.ThenSomeOutcome
import com.jspcore.spockme.stages.WhenSomeAction
import com.tngtech.jgiven.Stage
import com.tngtech.jgiven.annotation.As
import com.tngtech.jgiven.annotation.ScenarioStage

class MyShinyComposedShould extends ComposeSpec<GivenSomeState, WhenSomeAction, ThenSomeOutcome> {

  def "something composed"() {
    expect:

    given().some_state()
    and_another().different_state()
    when().some_action_bro()
    then().some_outcome()
  }

  def "something composed another order"() {
    expect:

    given_another().different_state()
    and().some_state()
    when().some_action_bro()
    then().some_outcome()
  }
}

class ComposeSpec<GIVEN, WHEN, THEN> extends ScenarioSpec<GIVEN, WHEN, THEN>
  implements GivenAnotherState.GivenAnotherStateTest {

  @ScenarioStage
  GivenAnotherState givenAnotherState

  @Override
  GivenAnotherState givenAnother() { return givenAnotherState }

  GIVEN and() { getScenario().given() }
}

@As("another")
class GivenAnotherState<SELF extends GivenAnotherState<?>> extends Stage<SELF> {

  SELF different_state() {
    self()
  }

  trait GivenAnotherStateTest {

    abstract def givenAnother()

    GivenAnotherState given_another() {
      return givenAnother().then()
    }

    GivenAnotherState and_another() {
      return givenAnother().and()
    }
  }
}
