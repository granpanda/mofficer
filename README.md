# Mofficer [![Build Status](https://travis-ci.org/granpanda/mofficer.svg?branch=master)](https://travis-ci.org/granpanda/mofficer)

Mofficer (for Mail Officer) is a lightweight and small system responsible for one thing and one thing only: send emails. It offers an API that allows other systems to send emails programmatically. We use Mofficer at BlackSip to notify different kind of events.

## Philosophy

We created Mofficer driven by two basic ideas:

1. Minimal configuration.
1. Easy to understand and consume API.

In order to send an email, the least amount of information you need is: the sender information and the email information. That's the only data you need to tell Mofficer and that can be done via API.

## How to use?

If you don't have Leiningen installed please [install it](http://leiningen.org/#install). And then type these 4 steps in your terminal.

Simple!

1. git clone git@github.com:granpanda/mofficer.git
1. cd mofficer/scripts
1. sh build.sh
1. sh start.sh

### How to send an Email?

#### 1. Create a UserConfig (the sender information).

HTTP Method: POST <br>
URL: http://localhost:9022/mofficer/api/user-configs <br>
Payload: 

    {
        "emailHost": "smtp.gmail.com",
        "emailPort": 587,
        "senderUsername": "username",
        "senderPassword": "password",
        "senderEmail": "username@gmail.com",
        "senderId": "senderId"
    }

#### 2. Send an email (the email information).

HTTP Method: POST <br>
URL: http://localhost:9022/mofficer/api/emails/senderId <br>
Payload: 

    {
        "recipients": ["qwe@gmail.com", "asd@hotmail.com", "zxc@fb.com"],
        "subject": "Welcome",
        "body": "Welcome to mofficer!"
    }

The sender email will be taken from the UserConfig with the same sellerId given as path param.

For further API documentation please follow this [link](https://github.com/granpanda/mofficer/wiki#api). 
