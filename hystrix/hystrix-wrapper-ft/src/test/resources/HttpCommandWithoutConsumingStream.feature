Feature: HttpCommand works even when it does not consume its stream

  @Execute @HTTP @WithoutFallBack @WithoutFailListener
  Scenario: Executing an HTTP command with no fallback returns connection to the pool even if the stream is not been read
    Given an HTTP command that does not read the stream
    When it is executed
    Then a successful response is returned
    And the HTTP connection is closed