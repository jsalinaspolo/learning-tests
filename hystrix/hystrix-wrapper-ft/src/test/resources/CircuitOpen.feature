Feature: Circuit open

  Background:
    Given the circuit is open

  @Execute @HTTP @WithoutFallBack @WithoutFailListener
  Scenario: Executing an HTTP command with no fallback when the circuit is open throws an exception
    Given an HTTP command
    When it is executed
    Then an OpenCircuitException is thrown
    And no http metrics are not affected

  @Execute @HTTP @WithoutFallBack @WithFailListener
  Scenario: Executing an HTTP command with no fallback when the circuit is open throws an exception and the provided failure listener is called
    Given an HTTP command with a failure listener
    When it is executed
    Then an OpenCircuitException is thrown
    And the failure listener was called with HystrixRuntimeException with SHORTCIRCUIT cause

  @Execute @Java @WithoutFallBack @WithoutFailListener
  Scenario: Executing a Java command with no fallback when the circuit is open throws an exception
    Given a Java command
    When it is executed
    Then an OpenCircuitException is thrown

  @Execute @Java @WithoutFallBack @WithFailListener
  Scenario: Executing a Java command with no fallback when the circuit is open throws an exception and the provided failure listener is called
    Given a Java command with a failure listener
    When it is executed
    Then an OpenCircuitException is thrown
    And the failure listener was called with HystrixRuntimeException with SHORTCIRCUIT cause

  @Execute @HTTP @WithFallBack @WithoutFailListener
  Scenario: Executing an HTTP command with fallback when the circuit is open returns fallback
    Given an HTTP command with a fallback
    When it is executed
    Then the fallback is returned

  @Execute @HTTP @WithFallBack @WithFailListener
  Scenario: Executing an HTTP command with fallback when the circuit is open returns fallback and the provided failure listener is called
    Given an HTTP command with a fallback and with a failure listener
    When it is executed
    Then the failure listener was called with HystrixRuntimeException with SHORTCIRCUIT cause and fallback value passed in
    And the fallback is returned

  @Execute @Java @WithFallBack @WithoutFailListener
  Scenario: Executing a Java command with fallback when the circuit is open returns fallback
    Given a Java command with a fallback
    When it is executed
    Then the fallback is returned

  @Execute @Java @WithFallBack @WithFailListener
  Scenario: Executing a Java command with fallback when the circuit is open returns fallback and the provided failure listener is called
    Given a Java command with a fallback and with a failure listener
    When it is executed
    Then the failure listener was called with HystrixRuntimeException with SHORTCIRCUIT cause and fallback value passed in
    And the fallback is returned

  @Queue @HTTP @WithoutFailListener @WithoutFallback
  Scenario: Queueing an HTTP command when the circuit is open
    Given an HTTP command
    When it is queued
    Then the command has returned without failure
    And the future throws HystrixRuntimeException with SHORTCIRCUIT cause

  @Queue @HTTP @WithoutFailListener @WithFallback
  Scenario: Queueing an HTTP command when the circuit is open returns the fallback
    Given an HTTP command with a fallback
    When it is queued
    Then the command has returned without failure
    And the fallback is returned

  @Queue @HTTP @WithFailListener @WithoutFallback
  Scenario: Queueing an HTTP command when the circuit is open calls the failure listener
    Given an HTTP command with a failure listener
    When it is queued
    Then the command has returned without failure
    And the failure listener was called with HystrixRuntimeException with SHORTCIRCUIT cause
    And the future throws HystrixRuntimeException with SHORTCIRCUIT cause

  @Queue @HTTP @WithFailListener @WithFallback
  Scenario: Queueing an HTTP command when the circuit is open calls the failure listener and returns the fallback
    Given an HTTP command with a fallback and with a failure listener
    When it is queued
    Then the command has returned without failure
    And the failure listener was called with HystrixRuntimeException with SHORTCIRCUIT cause and fallback value passed in
    And the fallback is returned

  @Queue @Java @WithoutFailListener @WithoutFallback
  Scenario: Queueing command when the circuit is open
    Given a Java command
    When it is queued
    Then the command has returned without failure
    And the future throws HystrixRuntimeException with SHORTCIRCUIT cause

  @Queue @Java @WithoutFailListener @WithFallback
  Scenario: Queueing command when the circuit is open returns fallback
    Given a Java command with a fallback
    When it is queued
    Then the command has returned without failure
    And the fallback is returned

  @Queue @Java @WithFailListener @WithoutFallback
  Scenario: Queueing command when the circuit is open calls the failure listener
    Given a Java command with a failure listener
    When it is queued
    Then the command has returned without failure
    And the failure listener was called with HystrixRuntimeException with SHORTCIRCUIT cause
    And the future throws HystrixRuntimeException with SHORTCIRCUIT cause

  @Queue @Java @WithFailListener @WithFallback
  Scenario: Queueing command when the circuit is open calls the failure listener and returns the fallback
    Given a Java command with a fallback and with a failure listener
    When it is queued
    Then the command has returned without failure
    And the failure listener was called with HystrixRuntimeException with SHORTCIRCUIT cause and fallback value passed in
    And the fallback is returned
