package com.jspcore.kotlin

import spock.lang.Specification

class EnumWithMethodShould extends Specification {

  def "method of an enum does not work from groovy"() {
    when:
    EnumWithMethod.A_VALUE.aMethod() == "a value"
    EnumWithMethod.ANOTHER_VALUE.aMethod() == "another value"

    then:
    thrown MissingMethodException
  }

  def "from kotlin works"() {
    expect:
    App.main() == "a value"
  }
}
