Feature: No threads available in hystrix
  Execution queue full.

  Background:
    Given there are no more resources to start a thread

  @Execute @HTTP @OutOfResources @WithoutFallBack @WithoutFailListener
  Scenario: Executing an HTTP command with no fallback when there are no more threads to execute the command throws an exception
    Given an HTTP command
    When it is executed
    Then an OutOfResourcesException is thrown

  @Execute @HTTP @OutOfResources @WithoutFallBack @WithFailListener
  Scenario: Executing an HTTP command with no fallback when there are no more threads to execute the command throws an exception and the provided failure listener is called
    Given an HTTP command with a failure listener
    When it is executed
    Then an OutOfResourcesException is thrown
    And the failure listener was called with HystrixRuntimeException with REJECTED_THREAD_EXECUTION cause

  @Execute @Java @OutOfResources @WithoutFallBack @WithoutFailListener
  Scenario: Executing a Java command with no fallback when there are no more threads to execute the command throws an exception
    Given a Java command
    When it is executed
    Then an OutOfResourcesException is thrown

  @Execute @Java @OutOfResources @WithoutFallBack @WithFailListener
  Scenario: Executing a Java command with no fallback when there are no more threads to execute the command throws an exception and the provided failure listener is called
    Given a Java command with a failure listener
    When it is executed
    Then an OutOfResourcesException is thrown
    And the failure listener was called with HystrixRuntimeException with REJECTED_THREAD_EXECUTION cause

  @Execute @HTTP @OutOfResources @WithFallBack @WithoutFailListener
  Scenario: Executing an HTTP command with fallback when there are no more threads to execute the command returns fallback
    Given an HTTP command with a fallback
    When it is executed
    Then the fallback is returned

  @Execute @HTTP @OutOfResources @WithFallBack @WithFailListener
  Scenario: Executing an HTTP command with fallback when there are no more threads to execute the command returns fallback and the provided failure listener is called
    Given an HTTP command with a fallback and with a failure listener
    When it is executed
    Then the fallback is returned
    And the failure listener was called with HystrixRuntimeException with REJECTED_THREAD_EXECUTION cause and fallback value passed in

  @Execute @Java @OutOfResources @WithFallBack @WithoutFailListener
  Scenario: Executing a Java command with fallback when there are no more threads to execute the command returns fallback
    Given a Java command with a fallback
    When it is executed
    Then the fallback is returned

  @Execute @Java @OutOfResources @WithFallBack @WithFailListener
  Scenario: Executing a Java command with fallback when there are no more threads to execute the command returns fallback and the provided failure listener is called
    Given a Java command with a fallback with a failure listener
    When it is executed
    Then the fallback is returned
    And the failure listener was called with HystrixRuntimeException with REJECTED_THREAD_EXECUTION cause and fallback value passed in

  @Queue @HTTP @OutOfResources @WithoutFallBack @WithoutFailListener
  Scenario: Queueing an HTTP command when there are no more threads to execute runs the command and the result can be seen from future
    Given an HTTP command
    When it is queued
    Then the command has returned without failure
    And the future throws HystrixRuntimeException with REJECTED_THREAD_EXECUTION cause

  @Queue @HTTP @OutOfResources @WithoutFallBack @WithFailListener
  Scenario: Queueing an HTTP command when there are no more threads to execute runs the command and the provided failure listener is called as well as the future
    Given an HTTP command with a failure listener
    When it is queued
    Then the command has returned without failure
    And the failure listener was called with HystrixRuntimeException with REJECTED_THREAD_EXECUTION cause
    And the future throws HystrixRuntimeException with REJECTED_THREAD_EXECUTION cause

  @Queue @HTTP @OutOfResources @WithFallBack @WithoutFailListener
  Scenario: Queueing an HTTP command when there are no more threads to execute runs the command and the fallback is returned by the future
    Given an HTTP command with a fallback
    When it is queued
    Then the command has returned without failure
    And the fallback is returned

  @Queue @HTTP @OutOfResources @WithFallBack @WithFailListener
  Scenario: Queueing an HTTP command when there are no more threads to execute runs the command and the provided failure listener is called and the fallback is returned by the future
    Given an HTTP command with a fallback and with a failure listener
    When it is queued
    Then the command has returned without failure
    And the failure listener was called with HystrixRuntimeException with REJECTED_THREAD_EXECUTION cause and fallback value passed in
    And the fallback is returned

  @Queue @Java @OutOfResources @WithoutFallBack @WithoutFailListener
  Scenario: Queue command when there are no more threads to execute runs the command and the future throws the exception
    Given a Java command
    When it is queued
    Then the command has returned without failure
    And the future throws HystrixRuntimeException with REJECTED_THREAD_EXECUTION cause

  @Queue @Java @OutOfResources @WithoutFallBack @WithFailListener
  Scenario: Queue command with fail listener when there are no more threads to execute runs the command and the future throws the exception
    Given a Java command with a failure listener
    When it is queued
    Then the command has returned without failure
    And the failure listener was called with HystrixRuntimeException with REJECTED_THREAD_EXECUTION cause
    And the future throws HystrixRuntimeException with REJECTED_THREAD_EXECUTION cause

  @Queue @Java @OutOfResources @WithFallBack @WithoutFailListener
  Scenario: Queue command when there are no more threads to execute runs the command and the fallback is returned by the future
    Given a Java command with a fallback
    When it is queued
    Then the command has returned without failure
    And the fallback is returned

  @Queue @Java @OutOfResources @WithFallBack @WithFailListener
  Scenario: Queueing a command when there are no more threads to execute runs the command and the provided failure listener is called and the fallback is returned by the future
    Given a Java command with a fallback and with a failure listener
    When it is queued
    Then the command has returned without failure
    And the failure listener was called with HystrixRuntimeException with REJECTED_THREAD_EXECUTION cause and fallback value passed in
    And the fallback is returned
