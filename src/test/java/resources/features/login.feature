Feature: Email with image functionality of gmail

  Scenario Outline: Send an email with image
    Given Open Google Chrome
    And login to gmail
    When I compose an email
    And enter a valid email as "<email>"
    And enter a email subject
    And enter a body to the email
    And attach an image as "<image>"
    And send
    Then The 'Message Sent' popup appears
    And The email can be found in the 'Sent' tab

    Examples:
      | email                    | image   |
      | mcgill.chungus@gmail.com | dog.jpg |

  Scenario Outline: Send an email with image but no subject line
    Given Open Google Chrome
    And login to gmail
    When I compose an email
    And enter a valid email as "<email>"
    And enter a body to the email
    And attach an image as "<image>"
    And send
    And confirm sending the email without a subject line
    Then The 'Message Sent' popup appears
    And The email can be found in the 'Sent' tab

    Examples:
      | email                    | image   |
      | mcgill.chungus@gmail.com | dog.jpg |

  Scenario Outline: Send an email with image but no email body
    Given Open Google Chrome
    And login to gmail
    When I compose an email
    And enter a valid email as "<email>"
    And enter a body to the email
    And attach an image as "<image>"
    And send
    Then The 'Message Sent' popup appears
    And The email can be found in the 'Sent' tab

    Examples:
      | email                    | image   |
      | mcgill.chungus@gmail.com | dog.jpg |

  Scenario Outline: Send an email with image from an empty 'Sent' folder
    Given Open Google Chrome
    And login to gmail
    And I navigate to my empty 'Sent' folder
    When I click on 'Send one now'
    And enter a valid email as "<email>"
    And enter a body to the email
    And attach an image as "<image>"
    And send
    Then The 'Message Sent' popup appears
    And The email can be found in the 'Sent' tab

    Examples:
      | email                    | image   |
      | mcgill.chungus@gmail.com | dog.jpg |
