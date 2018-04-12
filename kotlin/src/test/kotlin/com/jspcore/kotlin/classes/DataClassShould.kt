package com.jspcore.kotlin.classes

import org.junit.Assert.assertEquals
import org.junit.Test

class DataClassShould {

  data class Person(val name: String, val age: Int)

  @Test
  fun implement_equals() {
    assertEquals(Person("aName", 10), Person("aName", 10))
  }

  @Test
  fun implement_toString() {
    assertEquals(Person("aName", 10).toString(), "Person(name=aName, age=10)")
  }
}
