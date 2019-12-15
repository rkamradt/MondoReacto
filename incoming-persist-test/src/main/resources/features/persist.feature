Feature: Message queue is landed in the database

  Scenario: Good input is passed to message queue
    Given a good input value
    When the input value is handed to the message queue
    Then the input value should be found in the database

