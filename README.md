# tweet-ui

tweet-ui is a sub-module of the [TwtrTrkr project](https://github.com/zubairsaiyed/TwtrTrkr). It is designed to serve as an interface where user's can submit queries into the sentiment analyzer model and then view real-time aggregate sentiment time-series for those query keywords.

## Requirements

* Apache Kafka v0.8.1.1
* Twitter Developer account (for API Key, Secret etc.)
* lettuce (Redis Asynch Java Client)
* Apache Maven
* Oracle JDK 1.8 (64 bit)

## Launching the server

tweet-ui must first be configured with the Twitter API authentication credentials, Apache Kafka server details, Redis connection details, and a server port number via a configuration file at `src\main\resources\config.properties`.

The web project can then be built using the `mvn assembly:assembly` command. Finally the jar can be launched as follows:

```
mvn java -jar target/twitter-tracker-0.0.1-SNAPSHOT-jar-with-dependencies.jar
```

*Note: the above command must be run as super user if server port is configured to a restricted port (<1024) otherwise the server will fail to launch.*
