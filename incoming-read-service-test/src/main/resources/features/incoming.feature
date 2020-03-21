Feature: Incoming input is passed to message queue

  Scenario: Good input is passed to message queue
    Given a good input value
    And the input value is inserted in mongo
    And the incoming service get is called with the key value
    Then the return value should be 200
    Then the return value equal to the input value

