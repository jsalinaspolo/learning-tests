Feature: MDC Context is maintained across threads

  @Execute @Java
  Scenario: request-id is copied to the hystrix thread's MDC context when executing in a Java command
    Given one thread available in the pool
    And we set request-id in the main thread
    And we set up a Java command
    When it is executed
    Then a successful response is returned
    And the request-id is available in its MDC context

  @Execute @Java
  Scenario: request-id is not reused from the previous thread in a Java command
    Given one thread available in the pool
    And we set request-id in the main thread
    And we set up a Java command
    And it is executed
    And a successful response is returned
    And the request-id is available in its MDC context
    When we set a new request-id in the main thread
    And we set up a Java command
    And it is executed
    Then a successful response is returned
    And the new request-id is available in its MDC context

  @Execute @HTTP
  Scenario: request-id is copied to the hystrix thread's MDC context when executing in an HTTP command
    Given one thread available in the pool
    And we set request-id in the main thread
    And we set up an HTTP command
    When it is executed
    Then a successful response is returned
    And the request-id is available in its MDC context
    And the HTTP connection is closed

  @Execute @HTTP
  Scenario: request-id is not reused from the previous thread in an HTTP command
    Given one thread available in the pool
    And we set request-id in the main thread
    And we set up an HTTP command
    And it is executed
    And a successful response is returned
    And the request-id is available in its MDC context
    And the HTTP connection is closed
    When we set a new request-id in the main thread
    And we set up a HTTP command
    And it is executed
    Then a successful response is returned
    And the new request-id is available in its MDC context
    And the HTTP connection is closed

  @Queue @Java
  Scenario: request-id is copied to the hystrix thread's MDC context when queueing in a Java command
    Given one thread available in the pool
    And we set request-id in the main thread
    And we set up a Java command
    When it is queued
    Then the command has run asynchronously without failure
    And the request-id is available in its MDC context

  @Queue @Java
  Scenario: request-id is not reused from the previous thread when queueing in  a Java command
    Given one thread available in the pool
    And we set request-id in the main thread
    And we set up a Java command
    And it is queued
    And the command has run asynchronously without failure
    And the request-id is available in its MDC context
    When we set a new request-id in the main thread
    And we set up a Java command
    And it is queued
    Then the command has run asynchronously without failure
    And the new request-id is available in its MDC context

  @Queue @HTTP
  Scenario: request-id is copied to the hystrix thread's MDC context when queueing in an HTTP command
    Given two threads available in the pool
    And we set request-id in the main thread
    And we set up an HTTP command
    When it is queued
    Then the command has run asynchronously without failure
    And the request-id is available in its MDC context
    And the HTTP connection is closed

  @Queue @HTTP
  Scenario: request-id is not reused from the previous thread when queueing in an HTTP command
    Given two threads available in the pool
    And we set request-id in the main thread
    And we set up an HTTP command
    And it is queued
    And the command has run asynchronously without failure
    And the request-id is available in its MDC context
    And the HTTP connection is closed
    When we set a new request-id in the main thread
    And we set up a HTTP command
    And it is queued
    Then the command has run asynchronously without failure
    And the new request-id is available in its MDC context
    And the HTTP connection is closed
