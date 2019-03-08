Feature: Email with image functionality of gmail

  Scenario Outline: Send an email with image to valid email
    Given Open Google Chrome
    And login to gmail
    When I compose an email
    And enter an email as "<email>"
    And enter a email subject
    And enter a body to the email
    And attach an image as "<image>"
    And send
    Then The 'Message Sent' popup appears
    And The email can be found in the 'Sent' tab

    Examples:
      | email                    | image   |
      | mcgill.chungus@gmail.com | dog.jpg |
      | aol@aol.com              | chunkus.jpg|
      | fake@aol.com             | cat.jpg    |

  Scenario Outline: Send an email with image but no subject line or body to valid email
    Given Open Google Chrome
    And login to gmail
    When I compose an email
    And enter an email as "<email>"
    And attach an image as "<image>"
    And send
    And confirm sending the email without a subject line or body
    Then The 'Message Sent' popup appears

    Examples:
      | email                    | image   |
      | mcgill.chungus@gmail.com | dog.jpg |
      | aol@aol.com              | chunkus.jpg|
      | fake@aol.com             | cat.jpg    |


  Scenario Outline: Send an email with image from an empty 'Sent' folder to valid email
    Given Open Google Chrome
    And login to gmail
    And I navigate to my empty 'Sent' folder
    When I compose from the sent page
    And enter an email as "<email>"
    And enter a body to the email
    And attach an image as "<image>"
    And send
    Then The 'Message Sent' popup appears
    And The email can be found in the 'Sent' tab

    Examples:
      | email                    | image   |
      | mcgill.chungus@gmail.com | dog.jpg |

  Scenario Outline: Send an email with image to invalid email
    Given Open Google Chrome
    And login to gmail
    When I compose an email
    And enter an email as "<email>"
    And enter a email subject
    And enter a body to the email
    And attach an image as "<image>"
    And send
    Then I should be notified that email "<email>" is invalid

    Examples:
      | email                    | image   |
      | abc                      | dog.jpg |




