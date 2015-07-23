package fr.heffebaycay.sts.twitter_bot.util;

import fr.heffebaycay.sts.twitter_bot.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class TwitterUtil {

    private static final Logger logger = LoggerFactory.getLogger(TwitterUtil.class);

    public static void testTwitter() {
        Twitter twitter = TwitterFactory.getSingleton();
        twitter.setOAuthConsumer(Configuration.INSTANCE.consumerToken, Configuration.INSTANCE.consumerSecret);
        twitter.setOAuthAccessToken(new AccessToken(Configuration.INSTANCE.accessToken, Configuration.INSTANCE.accessSecret));

        try {
            twitter.updateStatus("Hello world o/");
        } catch (TwitterException e) {
            logger.error("Twitter test failed: {}", e);
            throw new RuntimeException(e);
        }
    }

    public static void authenticateUser() {

        Twitter twitter = TwitterFactory.getSingleton();
        twitter.setOAuthConsumer(Configuration.INSTANCE.consumerToken, Configuration.INSTANCE.consumerSecret);
        try {
            RequestToken requestToken = twitter.getOAuthRequestToken();
            AccessToken accessToken = null;

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            while(null == accessToken) {
                System.out.println("Open the following URL and grant access to your account: ");
                System.out.println(requestToken.getAuthorizationURL());
                System.out.print("Enter the PIN (if available) or just hit enter.[PIN]:");
                String pin = br.readLine();

                try {
                    if (pin.length() > 0) {
                        accessToken = twitter.getOAuthAccessToken(requestToken, pin);
                    } else {
                        accessToken = twitter.getOAuthAccessToken();
                    }
                } catch (TwitterException e) {
                    if(401 == e.getStatusCode()) {
                        logger.error("Unable to get the access token.");
                    } else {
                        logger.error("Twitter exception: {}", e);
                        throw new RuntimeException(e);
                    }
                }

            }

            Configuration.INSTANCE.accessToken = accessToken.getToken();
            Configuration.INSTANCE.accessSecret = accessToken.getTokenSecret();
            Configuration.save();

        } catch (TwitterException e) {
            logger.error("Twitter error: {}", e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            logger.error("Failed to read input data: {}", e);
            throw new RuntimeException(e);
        }


    }

}
