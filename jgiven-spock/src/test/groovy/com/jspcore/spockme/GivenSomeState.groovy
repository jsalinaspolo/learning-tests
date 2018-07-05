package com.jspcore.spockme

import com.tngtech.jgiven.Stage

class GivenSomeState extends Stage<GivenSomeState> {

  GivenSomeState some_state() {
    return self()
  }
}
