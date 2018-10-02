Feature: Failing command

  Background:
    Given  http timeout < http call delay < hystrix timeout

  @Execute @HTTP @WithoutFallBack @WithoutFailListener
  Scenario: Executing an HTTP command with no fallback that fails throws an exception and aborts the HTTP request
    Given an HTTP command that always fails
    When it is executed
    Then an exception is thrown
    And the HTTP connection is closed

  @Execute @HTTP @WithoutFallBack @WithFailListener
  Scenario: Executing an HTTP command with no fallback that fails throws an exception and aborts the HTTP request and the provided failure listener is called
    Given an HTTP command that always fails with a failure listener
    When it is executed
    Then an exception is thrown
    And the HTTP connection is closed
    And the failure listener was called with HystrixRuntimeException with COMMAND_EXCEPTION cause

  @Execute @Java @WithoutFallBack @WithoutFailListener
  Scenario: Executing a Java command with no fallback that fails throws an exception
    Given a Java command that always fails
    When it is executed
    Then an exception is thrown

  @Execute @Java @WithoutFallBack @WithFailListener
  Scenario: Executing a Java command with no fallback that fails throws an exception and the provided failure listener is called
    Given a Java command that always fails with a failure listener
    When it is executed
    Then an exception is thrown
    And the failure listener was called with HystrixRuntimeException with COMMAND_EXCEPTION cause

  @Execute @HTTP @WithFallBack @WithoutFailListener
  Scenario: Executing an HTTP command with fallback that fails returns fallback and aborts the HTTP request
    Given an HTTP command that always fails with a fallback
    When it is executed
    Then the fallback is returned
    And the HTTP connection is closed

  @Execute @HTTP @WithFallBack @WithFailListener
  Scenario: Executing an HTTP command with fallback that fails returns fallback and aborts the HTTP request and the provided failure listener is called
    Given an HTTP command that always fails with a fallback and with a failure listener
    When it is executed
    Then the fallback is returned
    And the HTTP connection is closed
    And the failure listener was called with HystrixRuntimeException with COMMAND_EXCEPTION cause and fallback value passed in

  @Execute @Java @WithFallBack @WithoutFailListener
  Scenario: Executing a Java command with fallback that fails returns fallback
    Given a Java command that always fails with a fallback
    When it is executed
    Then the fallback is returned

  @Execute @Java @WithFallBack @WithFailListener
  Scenario: Executing a Java command with fallback that fails returns fallback and the provided failure listener is called
    Given a Java command that always fails with a fallback and with a failure listener
    When it is executed
    Then the failure listener was called with HystrixRuntimeException with COMMAND_EXCEPTION cause and fallback value passed in
    And the fallback is returned

  @Queue @HTTP @WithoutFailListener @WithoutFallback
  Scenario: Queueing an HTTP command that fails aborts the HTTP request
    Given an HTTP command that always fails
    When it is queued
    Then the command has run asynchronously without failure
    And the HTTP connection is closed
    And the future throws HystrixRuntimeException with COMMAND_EXCEPTION cause

  @Queue @HTTP @WithoutFailListener @WithFallback
  Scenario: Queueing an HTTP command that fails aborts the HTTP request and returns the fallback
    Given an HTTP command that always fails with a fallback
    When it is queued
    Then the command has run asynchronously without failure
    And the HTTP connection is closed
    And the fallback is returned

  @Queue @HTTP @WithFailListener @WithoutFallback
  Scenario: Queueing an HTTP command that fails aborts the HTTP request and the provided failure listener is called
    Given an HTTP command that always fails with a failure listener
    When it is queued
    Then the command has run asynchronously without failure
    And the HTTP connection is closed
    And the failure listener was called with HystrixRuntimeException with COMMAND_EXCEPTION cause
    And the future throws HystrixRuntimeException with COMMAND_EXCEPTION cause

  @Queue @HTTP @WithFailListener @WithFallback
  Scenario: Queueing an HTTP command that fails aborts the HTTP request and the provided failure listener is called and returns the fallback
    Given an HTTP command that always fails with a fallback with a failure listener
    When it is queued
    Then the command has run asynchronously without failure
    And the HTTP connection is closed
    And the failure listener was called with HystrixRuntimeException with COMMAND_EXCEPTION cause and fallback value passed in
    And the fallback is returned

  @Queue @Java @WithoutFailListener @WithoutFallback
  Scenario: Queueing a Java command that fails
    Given a Java command that always fails
    When it is queued
    Then the command has run asynchronously without failure
    And the future throws HystrixRuntimeException with COMMAND_EXCEPTION cause

  @Queue @Java @WithFailListener @WithoutFallback
  Scenario: Queue command that fails and the provided failure listener is called
    Given a Java command that always fails with a failure listener
    When it is queued
    Then the command has run asynchronously without failure
    And the failure listener was called with HystrixRuntimeException with COMMAND_EXCEPTION cause
    And the future throws HystrixRuntimeException with COMMAND_EXCEPTION cause

  @Queue @Java @WithoutFailListener @WithFallback
  Scenario: Queueing a Java command that fails returns the fallback
    Given a Java command that always fails with a fallback
    When it is queued
    Then the command has run asynchronously without failure
    And the fallback is returned

  @Queue @Java @WithFailListener @WithFallback
  Scenario: Queueing a Java command that fails calls the provided failure listener and returns the fallback
    Given a Java command that always fails with a fallback with a failure listener
    When it is queued
    Then the command has run asynchronously without failure
    And the failure listener was called with HystrixRuntimeException with COMMAND_EXCEPTION cause and fallback value passed in
    And the fallback is returned

