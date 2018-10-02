Feature: Calling queue returns earlier than the long running command should have finished
  Background:
    Given we have larger timeouts

  @Queue @Java @WithoutFallBack @WithoutFailListener @GetCalled
  Scenario: Queueing a Java Command happens asynchronously
    And a Java command
    When it is queued
    Then the command has not finished yet
    And a successful response is eventually returned

  @Queue @HTTP @WithoutFallBack @WithoutFailListener @GetCalled
  Scenario: Queueing a Http Command happens asynchronous
    And a HTTP command
    When it is queued
    Then the command has not finished yet
    And a successful response is eventually returned
