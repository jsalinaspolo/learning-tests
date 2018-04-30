package com.jspcore.kotlin

import de.jodamob.kotlin.testrunner.OpenedClasses
import de.jodamob.kotlin.testrunner.SpotlinTestRunner
import org.junit.runner.RunWith
import spock.lang.Specification

@RunWith(SpotlinTestRunner.class)
@OpenedClasses(AnyKotlinClass)
class KotlinSpockPluginShould extends Specification {

  AnyKotlinClass mocked = Mock(AnyKotlinClass)

  def "mock kotlin final class using kotlin-testrunner"() {
    when:
    def result = mocked.aMethod()

    then:
    1 * mocked.aMethod() >> "hello mock"
    result == "hello mock"
  }
}
