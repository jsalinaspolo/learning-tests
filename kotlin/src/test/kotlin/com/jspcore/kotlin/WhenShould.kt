package com.jspcore.kotlin

import org.junit.Assert.assertEquals
import org.junit.Test


sealed class SomeType
data class A(val name: String = "A") : SomeType()
data class B(val name: String = "B") : SomeType()

class WhenShould {

  @Test
  fun not_be_exhausted_when_is_not_assigned() {
    val a: SomeType = A()
    val list = mutableListOf<String>()
    when(a) {
      is A -> list.add(a.name)
//      is B -> true
    }

    assertEquals(list.size, 1)
  }
}
