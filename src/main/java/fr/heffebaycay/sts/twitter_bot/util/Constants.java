package fr.heffebaycay.sts.twitter_bot.util;


import twitter4j.GeoLocation;

public interface Constants {

    String STS_FEED_URL = "https://steamcommunity.com/groups/STSLounge/rss";

    String CONFIG_DIR = System.getProperty("sts.twitter.config.dir");

    String CONFIG_FILE = "config.xml";
    String LOCALIZATION_FILE = "localization.xml";

    int TWITTER_SHORT_URL_LENGTH = 22;
    int TWITTER_SHORT_URL_LENGTH_HTTPS = 23;

    int TWITTER_TWEET_MAX_LENGTH = 140;

    double VALVE_GEO_LATITUDE = 47.614199;
    double VALVE_GEO_LONGITUDE = -122.193782;

    GeoLocation VALVE_LOCATION = new GeoLocation(Constants.VALVE_GEO_LATITUDE, Constants.VALVE_GEO_LONGITUDE);

}
