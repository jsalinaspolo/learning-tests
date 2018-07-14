package com.jspcore.spockme.stages

import com.tngtech.jgiven.Stage

class ThenSomeOutcome extends Stage<ThenSomeOutcome> {

  ThenSomeOutcome some_outcome() {
    assert true
    return self()
  }
}
