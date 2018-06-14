package com.jspcore.kotlin

enum class EnumWithMethod {

  A_VALUE {
    override fun aMethod(): String = "a value"
  },
  ANOTHER_VALUE {
    override fun aMethod(): String = "another value"
  };

  abstract fun aMethod(): String
}


class App {
  companion object {
    @JvmStatic
    fun main(): String = EnumWithMethod.A_VALUE.aMethod()
  }
}
