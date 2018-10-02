Feature: Timed out command
# Http connection is only put back in the pool when the calling hystrix thread has finished.

  Background:
    Given hystrix timeout < http call delay < http timeout

  @Execute @HTTP @WithoutFallBack @WithoutFailListener
  Scenario: Executing an HTTP command with no fallback that times out throws an exception and aborts the HTTP request
    Given an HTTP command that always times out
    When it is executed
    Then a TimeoutException is thrown
    And the HTTP connection is closed

  @Execute @HTTP @WithoutFallBack @WithFailListener
  Scenario: Executing an HTTP command with no fallback that times out throws an exception and aborts the HTTP request and the provided failure listener is called
    Given an HTTP command that always times out with a failure listener
    When it is executed
    Then a TimeoutException is thrown
    And the HTTP connection is closed
    And the failure listener was called with HystrixRuntimeException with TIMEOUT cause

  @Execute @Java @WithoutFallBack @WithoutFailListener
  Scenario: Executing a Java command with no fallback that times out throws an exception
    Given a Java command that always times out
    When it is executed
    Then a TimeoutException is thrown

  @Execute @Java @WithoutFallBack @WithFailListener
  Scenario: Executing a Java command with no fallback that times out throws an exception and the provided failure listener is called
    Given a Java command that always times out with a failure listener
    When it is executed
    Then a TimeoutException is thrown
    And the failure listener was called with HystrixRuntimeException with TIMEOUT cause

  @Execute @HTTP @WithFallBack @WithoutFailListener
  Scenario: Executing an HTTP command with fallback that times out returns fallback and aborts the HTTP request
    Given an HTTP command that always times out with a fallback
    When it is executed
    Then the fallback is returned
    And the HTTP connection is closed

  @Execute @HTTP @WithFallBack @WithFailListener
  Scenario: Executing an HTTP command with fallback that times out returns fallback and aborts the HTTP request and the provided failure listener is called
    Given an HTTP command that always times out with a fallback and with a failure listener
    When it is executed
    Then the fallback is returned
    And the HTTP connection is closed
    And the failure listener was called with HystrixRuntimeException with TIMEOUT cause and fallback value passed in

  @Execute @Java @WithFallBack @WithoutFailListener
  Scenario: Executing a Java command with fallback that times out returns fallback
    Given a Java command that always times out with a fallback
    When it is executed
    Then the fallback is returned

  @Execute @Java @WithFallBack @WithFailListener
  Scenario: Executing a Java command with fallback that times out returns fallback and the provided failure listener is called
    Given a Java command that always times out with a fallback and with a failure listener
    When it is executed
    Then the fallback is returned
    And the failure listener was called with HystrixRuntimeException with TIMEOUT cause and fallback value passed in

  @Queue @HTTP @WithoutFailListener @WithoutFallback
  Scenario: Queueing an HTTP command that times out aborts the HTTP request and the future throws the timeout exception
    Given an HTTP command that always times out
    When it is queued
    Then the command has run asynchronously without failure
    And the HTTP connection is closed
    And the future throws HystrixRuntimeException with TIMEOUT cause

  @Queue @HTTP @WithoutFailListener @WithFallback
  Scenario: Queueing an HTTP command that times out aborts the HTTP request and returns the fallback
    Given an HTTP command that always times out with a fallback
    When it is queued
    Then the command has run asynchronously without failure
    And the HTTP connection is closed
    And the fallback is returned

  @Queue @HTTP @WithFailListener @WithoutFallback
  Scenario: Queueing an HTTP command with a failure listener that times out aborts the HTTP request and the future throws the timeout exception
    Given an HTTP command that always times out with a failure listener
    When it is queued
    Then the command has run asynchronously without failure
    And the HTTP connection is closed
    And the failure listener was called with HystrixRuntimeException with TIMEOUT cause
    And the future throws HystrixRuntimeException with TIMEOUT cause

  @Queue @HTTP @WithFailListener @WithFallback
  Scenario: Queueing an HTTP command with a failure listener that times out aborts the HTTP request and the future returns the fallback
    Given an HTTP command that always times out with a fallback and with a failure listener
    When it is queued
    Then the command has run asynchronously without failure
    And the HTTP connection is closed
    And the failure listener was called with HystrixRuntimeException with TIMEOUT cause and fallback value passed in
    And the fallback is returned

  @Queue @Java @WithoutFailListener @WithoutFallback
  Scenario: Queue command that times out and the future throws timeout exception
    Given a Java command that always times out
    When it is queued
    Then the command has run asynchronously without failure
    And the future throws HystrixRuntimeException with TIMEOUT cause

  @Queue @Java @WithoutFailListener @WithFallback
  Scenario: Queue command that times out and the future returns the fallback
    Given a Java command that always times out with a fallback
    When it is queued
    Then the command has run asynchronously without failure
    And the fallback is returned

  @Queue @Java @WithFailListener @WithoutFallback
  Scenario: Queue command with failure listener that times out throws timeout exception from future
    Given a Java command that always times out with a failure listener
    When it is queued
    Then the command has run asynchronously without failure
    And the failure listener was called with HystrixRuntimeException with TIMEOUT cause
    And the future throws HystrixRuntimeException with TIMEOUT cause

  @Queue @Java @WithFailListener @WithFallback
  Scenario: Queueing command with failure listener that times out calls the failure listener and the future returns the fallback
    Given a Java command that always times out with a fallback and with a failure listener
    When it is queued
    Then the command has run asynchronously without failure
    And the failure listener was called with HystrixRuntimeException with TIMEOUT cause and fallback value passed in
    And the fallback is returned
