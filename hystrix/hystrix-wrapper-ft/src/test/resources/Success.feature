Feature: Successful command

  @Execute @HTTP @WithoutFallBack @WithoutFailListener
  Scenario: Executing an HTTP command with no fallback returns a response
    Given an HTTP command
    When it is executed
    Then a successful response is returned
    And the HTTP connection is closed

  @Execute @HTTP @WithoutFallBack @WithFailListener
  Scenario: Executing an HTTP command with no fallback returns a response and the provided failure listener is not called
    Given an HTTP command with a failure listener
    When it is executed
    Then a successful response is returned
    And the HTTP connection is closed
    And the failure listener was not called

  @Execute @Java @WithoutFallBack @WithoutFailListener
  Scenario: Executing a Java command with no fallback returns a response
    Given a Java command
    When it is executed
    Then a successful response is returned

  @Execute @Java @WithoutFallBack @WithFailListener
  Scenario: Executing a Java command with no fallback returns a response and the provided failure listener is not called
    Given a Java command with a failure listener
    When it is executed
    Then a successful response is returned
    And the failure listener was not called

  @Execute @HTTP @WithFallback @WithoutFailListener
  Scenario: Executing an HTTP command with fallback returns a response
    Given an HTTP command with a fallback
    When it is executed
    Then a successful response is returned
    And the HTTP connection is closed

  @Execute @HTTP @WithFallback @WithFailListener
  Scenario: Executing an HTTP command with fallback returns a response and the provided failure listener is not called
    Given an HTTP command with a fallback and with a failure listener
    When it is executed
    Then a successful response is returned
    And the HTTP connection is closed
    And the failure listener was not called

  @Execute @Java @WithFallback @WithoutFailListener
  Scenario: Executing a Java command with fallback returns a response
    Given a Java command with a fallback
    When it is executed
    Then a successful response is returned

  @Execute @Java @WithFallback @WithFailListener
  Scenario: Executing a Java command with fallback returns a response and the provided failure listener is not called
    Given a Java command with a fallback and with a failure listener
    When it is executed
    Then a successful response is returned
    And the failure listener was not called

  @Queue @HTTP @WithoutFallBack @WithoutFailListener @GetNotCalled
  Scenario: Queueing an HTTP command
    Given an HTTP command
    When it is queued
    Then the command has run asynchronously without failure
    And the HTTP connection is closed

  @Queue @HTTP @WithoutFallBack @WithoutFailListener @GetCalled
  Scenario: Queueing an HTTP command can get the result from the future
    Given an HTTP command
    When it is queued
    Then the command has run asynchronously without failure
    And a successful response is eventually returned
    And the HTTP connection is closed

  @Queue @HTTP @WithoutFallBack @WithFailListener @GetNotCalled
  Scenario: Queueing an HTTP command successfully does not call the provided failure listener
    Given an HTTP command with a failure listener
    When it is queued
    Then the command has run asynchronously without failure
    And the HTTP connection is closed
    And the failure listener was not called

  @Queue @HTTP @WithoutFallBack @WithFailListener @GetCalled
  Scenario: Queueing an HTTP command successfully does not call the provided failure listener and returns on get
    Given an HTTP command with a failure listener
    When it is queued
    Then the command has run asynchronously without failure
    And the HTTP connection is closed
    And the failure listener was not called
    And a successful response is eventually returned

  @Queue @Java @WithoutFallBack @WithoutFailListener @GetNotCalled
  Scenario: Queueing a Java command
    Given a Java command
    When it is queued
    Then the command has run asynchronously without failure

  @Queue @Java @WithoutFallBack @WithoutFailListener @GetCalled
  Scenario: Queueing a Java command you can get the result from the future
    Given a Java command
    When it is queued
    Then the command has run asynchronously without failure
    And a successful response is eventually returned

  @Queue @Java @WithoutFallBack @WithFailListener @GetNotCalled
  Scenario: Queueing a Java command successfully does not call the provided failure listener
    Given a Java command with a failure listener
    When it is queued
    Then the command has run asynchronously without failure
    And the failure listener was not called

  @Queue @Java @WithoutFallBack @WithFailListener @GetCalled
  Scenario: Queueing a Java command successfully does not call the provided failure listener and returns on get
    Given a Java command with a failure listener
    When it is queued
    Then the command has run asynchronously without failure
    And the failure listener was not called
    And a successful response is eventually returned

  @Queue @HTTP @WithFallBack @WithoutFailListener @GetNotCalled
  Scenario: Queueing an HTTP command with fallback
    Given an HTTP command with a fallback
    When it is queued
    Then the command has run asynchronously without failure
    And the HTTP connection is closed

  @Queue @HTTP @WithFallBack @WithoutFailListener @GetCalled
  Scenario: Queueing an HTTP command with fallback returns with the fallback value on calling get
    Given an HTTP command with a fallback
    When it is queued
    Then the command has run asynchronously without failure
    And a successful response is eventually returned
    And the HTTP connection is closed

  @Queue @HTTP @WithFallBack @WithFailListener @GetNotCalled
  Scenario: Queueing an HTTP command with fallback and listener then the provided failure listener is not called
    Given an HTTP command with a fallback and with a failure listener
    When it is queued
    Then the command has run asynchronously without failure
    And the HTTP connection is closed
    And the failure listener was not called

  @Queue @HTTP @WithFallBack @WithFailListener @GetCalled
  Scenario: Queueing an HTTP command with fallback and listener then the provided failure listener is not called and it returns with the fallback value on calling get
    Given an HTTP command with a fallback and with a failure listener
    When it is queued
    Then the command has run asynchronously without failure
    And the HTTP connection is closed
    And the failure listener was not called
    And a successful response is eventually returned

  @Queue @Java @WithFallBack @WithoutFailListener @GetNotCalled
  Scenario: Queueing a Java command with fallback
    Given a Java command with a fallback
    When it is queued
    Then the command has run asynchronously without failure

  @Queue @Java @WithFallBack @WithoutFailListener @GetCalled
  Scenario: Queueing a Java command with fallback returns with the fallback value on calling get
    Given a Java command with a fallback
    When it is queued
    Then the command has run asynchronously without failure
    And a successful response is eventually returned

  @Queue @Java @WithFallBack @WithFailListener @GetNotCalled
  Scenario: Queueing a Java command with fallback and listener then the provided failure listener is not called
    Given a Java command with a fallback and with a failure listener
    When it is queued
    Then the command has run asynchronously without failure
    And the failure listener was not called

  @Queue @Java @WithFallBack @WithFailListener @GetCalled
  Scenario: Queueing a Java command with fallback and listener then the provided failure listener is not called and it returns with the fallback value on calling get
    Given a Java command with a fallback and with a failure listener
    When it is queued
    Then the command has run asynchronously without failure
    And the failure listener was not called
    And a successful response is eventually returned
