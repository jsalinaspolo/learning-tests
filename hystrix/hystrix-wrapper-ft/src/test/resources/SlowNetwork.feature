Feature: Network slowed down
  Packets come slow, but faster than SocketTimeout. The overall time taken is larger than SocketTimeout. SocketTimeout never reached.

  Background:
    Given the network is slow

  @Execute @HTTP @WithoutFallBack @WithoutFailListener
  Scenario: Executing an HTTP command with no fallback throws an exception and aborts the HTTP request
    Given an HTTP command
    When it is executed
    Then a TimeoutException is thrown
    And the HTTP connection is closed

  @Execute @HTTP @WithoutFallBack @WithFailListener
  Scenario: Executing an HTTP command with no fallback throws an exception and aborts the HTTP request and the provided failure listener is called
    Given an HTTP command with a failure listener
    When it is executed
    Then a TimeoutException is thrown
    And the HTTP connection is closed
    And the failure listener was called with HystrixRuntimeException with TIMEOUT cause

  @Execute @HTTP @WithFallBack @WithoutFailListener
  Scenario: Executing an HTTP command with fallback that takes a long time without timing out returns fallback and aborts the HTTP request
    Given an HTTP command with a fallback
    When it is executed
    Then the fallback is returned
    And the HTTP connection is closed

  @Execute @HTTP @WithFallBack @WithFailListener
  Scenario: Executing an HTTP command with fallback that takes a long time without timing out returns fallback and aborts the HTTP request and the provided failure listener is called
    Given an HTTP command with a fallback and with a failure listener
    When it is executed
    Then the fallback is returned
    And the HTTP connection is closed
    And the failure listener was called with HystrixRuntimeException with TIMEOUT cause and fallback value passed in

  @Queue @HTTP @WithoutFailListener @WithoutFallback
  Scenario: Queueing an HTTP command that takes a long time without timing out aborts the HTTP request and the future throws the timeout exception
    Given an HTTP command
    When it is queued
    Then the command has run asynchronously without failure
    And the HTTP connection is closed
    And the future throws HystrixRuntimeException with TIMEOUT cause

  @Queue @HTTP @WithoutFailListener @WithFallback
  Scenario: Queueing an HTTP command that takes a long time without timing out aborts the HTTP request and returns the fallback
    Given an HTTP command with a fallback
    When it is queued
    Then the command has run asynchronously without failure
    And the HTTP connection is closed
    And the fallback is returned

  @Queue @HTTP @WithFailListener @WithoutFallback
  Scenario: Queueing an HTTP command with a failure listener that takes a long time without timing out aborts the HTTP request and the future throws the timeout exception
    Given an HTTP command with a failure listener
    When it is queued
    Then the command has run asynchronously without failure
    And the HTTP connection is closed
    And the failure listener was called with HystrixRuntimeException with TIMEOUT cause
    And the future throws HystrixRuntimeException with TIMEOUT cause

  @Queue @HTTP @WithFailListener @WithFallback
  Scenario: Queueing an HTTP command with a failure listener that takes a long time without timing out aborts the HTTP request and the future returns the fallback
    Given an HTTP command with a fallback and with a failure listener
    When it is queued
    Then the command has run asynchronously without failure
    And the HTTP connection is closed
    And the failure listener was called with HystrixRuntimeException with TIMEOUT cause and fallback value passed in
    And the fallback is returned
