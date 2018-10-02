Feature: Prefix command names with the group key

  @Execute @Java @WithoutFallBack @WithoutFailListener
  Scenario: Command configuration affect other command with the same name in a different group
    Given hystrix commandFactories have been configured for both groups
    And hystrix timeout is set for the first command
    And a Java command waiting longer than its hystrix timeout
    And it is executed
    And  a TimeoutException is thrown
    When there is another Java command with the same name in another group
    And  it is executed
    Then a TimeoutException is thrown
