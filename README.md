# STS Twitter Bot

This project is a Twitter bot that posts the list of recent language file
changes from the Steam Translation Server.

## Setup

In order to setup the application, please follow these instructions:

1. Copy the application 'jar' file and the 'lib' directory in a directory
2. Create a file called *config.xml* next to the application jar file (see the *Configuration*
 section for more information about the content of this file)
3. Run the application with the *twitter:authenticate* command line parameter to setup
your Twitter account with the application.
4. (Optionally) Run the application with the *twitter:test* command line parameter to
  check that the application can interact with your Twitter account.
5. You're done! Now you can run the application to check for new updates on STS.


## Configuration

The application uses a configuration file called *config.xml* that has the following structure:

```xml
<configuration>
   <accessToken>loremipsum</accessToken>
   <accessSecret>loremipsum</accessSecret>
   <consumerToken>loremipsum</consumerToken>
   <consumerSecret>loremipsum</consumerSecret>
   <dryRun>[true|false]</dryRun>
</configuration>

```

Here is a short documentation:

- accessToken / accessSecret : The access token & secret for the Twitter account from which
updates are posted.
- consumerToken / consumerSecret : The API Key & Secret for the Twitter app. These can be found
in your Twitter application settings, over at https://apps.twitter.com/
- dryRun : No tweets will be posted, even if new changes from STS are detected.

## License

This project is released under the MIT License and uses work from the following projects:

- Twitter4J : http://twitter4j.org/ (Apache License 2.0)
- SLF4J : http://www.slf4j.org/license.html (MIT License)
- Logback : http://logback.qos.ch/license.html (Eclipse Public License v1.0 & LGPL 2.1)
- Simple XML : http://simple.sourceforge.net/download.php (Apache License 2.0)


For more information, please refer to the *LICENSE.txt* file.