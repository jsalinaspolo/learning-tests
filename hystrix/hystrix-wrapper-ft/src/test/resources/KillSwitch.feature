Feature: Kill Switching commands

  @Execute @HTTP @WithoutFailListener
  Scenario: Kill switching an HTTP command with a fallback returns a fallback response
    Given an HTTP command with a fallback
    And the command has been kill switched
    When it is executed
    Then the fallback is returned
    And the hystrix HTTP command has not been called at all
    But the kill switch metric is produced

  @Execute @HTTP @WithoutFallBack @WithoutFailListener
  Scenario: Kill switching an HTTP command with no fallback throws a KillSwitchWithoutFallback exception
    Given an HTTP command
    And the command has been kill switched
    When it is executed
    Then a KillSwitchWithoutFallback exception is thrown
    And the hystrix HTTP command has not been called at all
    And the kill switch metric is produced

  @Execute @Java @WithoutFailListener
  Scenario: Kill switching an HTTP command with a fallback returns a fallback response
    Given an Java command with a fallback
    And the command has been kill switched
    When it is executed
    Then the fallback is returned
    And the hystrix Java command has not been called at all
    But the kill switch metric is produced

  @Execute @HTTP @WithoutFallBack @WithoutFailListener
  Scenario: Kill switching an HTTP command with no fallback throws a KillSwitchWithoutFallback exception
    Given an Java command
    And the command has been kill switched
    When it is executed
    Then a KillSwitchWithoutFallback exception is thrown
    And the hystrix Java command has not been called at all
    And the kill switch metric is produced
