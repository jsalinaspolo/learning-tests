package com.jspcore.spockme

import com.tngtech.jgiven.Stage

class WhenSomeAction extends Stage<WhenSomeAction> {

  WhenSomeAction "some action bro"() {
    return self()
  }
}
