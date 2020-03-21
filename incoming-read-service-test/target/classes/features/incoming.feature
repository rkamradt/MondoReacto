Feature: Incoming input is passed to message queue

  Scenario: Good input is passed to message queue
    Given a good input value
    When the incoming service put is called
    Then the return value should be 202
    And the input value should be found on the message queue

