package com.jspcore.kotlin

import org.spockframework.mock.CannotCreateMockException
import spock.lang.Specification

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class KotlinSpockShould extends Specification {

  def "mock kotlin final class - but is not working yet"() {
    when:
    Mock(AnyKotlinClass)

    then:
    thrown CannotCreateMockException
  }

  def "mock kotlin final class using Mockito2"() {
    given:
    def mocked = mock(AnyKotlinClass.class)
    when(mocked.aMethod()).thenReturn("hello mock")

    when:
    def result = mocked.aMethod()

    then:
    result == "hello mock"
  }
}
