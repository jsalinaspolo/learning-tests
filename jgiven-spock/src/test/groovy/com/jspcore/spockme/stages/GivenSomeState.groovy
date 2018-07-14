package com.jspcore.spockme.stages

import com.tngtech.jgiven.Stage

class GivenSomeState extends Stage<GivenSomeState> {

  GivenSomeState some_state() {
    return self()
  }

  GivenSomeState and() { self() }
}
