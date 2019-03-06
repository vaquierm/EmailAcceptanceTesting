Feature: Email with image functionality of gmail

  Scenario Outline: Send an email with image
    Given Open Google Chrome
    And login to gmail
    When I compose an email
    And enter a valid email as "<email>"
    And attach an image as "<image>"
    And send
    Then the email should be sent successfully

  Examples:
    | email | image |
    | jeremy.lentini2@gmail.com | dog.jpg |
    | jeremy.lentini@mail.mcgill.ca | chunkus.jpg |
