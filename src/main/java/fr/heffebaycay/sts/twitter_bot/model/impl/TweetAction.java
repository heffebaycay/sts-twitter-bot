package fr.heffebaycay.sts.twitter_bot.model.impl;

import fr.heffebaycay.sts.twitter_bot.config.Configuration;
import fr.heffebaycay.sts.twitter_bot.config.Localization;
import fr.heffebaycay.sts.twitter_bot.model.Action;
import fr.heffebaycay.sts.twitter_bot.model.ActionParam;
import fr.heffebaycay.sts.twitter_bot.model.FeedMessage;
import fr.heffebaycay.sts.twitter_bot.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.*;
import twitter4j.auth.AccessToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TweetAction implements Action {

    private static final Logger logger = LoggerFactory.getLogger(TweetAction.class);

    private Twitter mTwitter;

    @Override
    public void run(ActionParam param) {

        if (param instanceof TweetActionParam == false) {
            return;
        }

        // Setting up twitter client
        initTwitter();

        TweetActionParam tweetParams = (TweetActionParam) param;

        Pattern titlePattern = Pattern.compile("STS-ALERT \\(([0-9]+) new strings?\\)");
        Pattern itemPattern = Pattern.compile("([0-9]+) new strings? for: ([a-z0-9/_]+\\.txt)");

        for (FeedMessage feedMessage : tweetParams.feedMessages) {
            Matcher titleMatcher = titlePattern.matcher(feedMessage.getTitle());

            if (titleMatcher.matches()) {
                // Feed message is a STS Update

                int nbStringsUpdated = Integer.parseInt(titleMatcher.group(1));

                Matcher itemMatcher = itemPattern.matcher(feedMessage.getDescription());

                String tokenText = nbStringsUpdated == 1 ? "token" : "tokens";
                String tweetIntro = String.format("STS Update: %1$d new %2$s: ", nbStringsUpdated, tokenText);


                int nbOthers = 0;

                List<String> tweetParts = new ArrayList<>();
                List<String> tweetMessages = new ArrayList<>();

                while (itemMatcher.find()) {
                    int nbStringsInFile = Integer.parseInt(itemMatcher.group(1));
                    String fileName = itemMatcher.group(2);

                    if (Localization.INSTANCE.aliases.containsKey(fileName)) {
                        String subUpdate = String.format("%1$s (%2$d), ", Localization.INSTANCE.aliases.get(fileName), nbStringsInFile );
                        tweetParts.add(subUpdate);
                    } else {
                        nbOthers += nbStringsInFile;
                    }
                }

                if (nbOthers > 0) {
                    tweetParts.add(String.format("Others (%1$d)", nbOthers));
                }

                final int TWEET_MAX_LENGTH_CORRECTED = Constants.TWITTER_TWEET_MAX_LENGTH - Constants.TWITTER_SHORT_URL_LENGTH_HTTPS - 1;

                int partIdx = 0;
                while (partIdx < tweetParts.size()) {
                    StringBuilder tweetMessage = new StringBuilder(tweetIntro);

                    while (partIdx < tweetParts.size() && ( (tweetMessage.length() + tweetParts.get(partIdx).length()) < TWEET_MAX_LENGTH_CORRECTED) ) {
                        // There's enough room for the next part
                        tweetMessage.append(tweetParts.get(partIdx));
                        partIdx++;
                    }

                    // No more room in the current tweet
                    if (tweetMessage.toString().endsWith(", ")) {
                        tweetMessage.delete(tweetMessage.length() - 2, tweetMessage.length());
                    }
                    tweetMessage.append(" ");
                    tweetMessage.append(feedMessage.getLink());

                    // Tweet is ready to be sent
                    tweetMessages.add(tweetMessage.toString());

                }

                for (String tweet : tweetMessages) {
                    logger.info("Tweet: {}", tweet);
                    logger.info("NbChars: {}", tweet.length());

                    if (Configuration.INSTANCE.dryRun == true) {
                        // This is a 'dry' run, so we shouldn't send any tweet
                        continue;
                    }

                    // Sending tweet
                    StatusUpdate statusUpdate = new StatusUpdate(tweet);
                    statusUpdate.setLocation(Constants.VALVE_LOCATION);
                    try {
                        mTwitter.updateStatus(statusUpdate);
                    } catch (TwitterException e) {
                        logger.error("Failed to send tweet: {}", e);
                        throw new RuntimeException("Failed to send tweet", e);
                    }

                    // Throttling tweet sending a tiny bit
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        logger.error("Failed to sleep: ", e);
                        throw new RuntimeException("Failed to sleep", e);
                    }
                }
            }

        }
    }

    private void initTwitter() {
        mTwitter = TwitterFactory.getSingleton();
        mTwitter.setOAuthConsumer(Configuration.INSTANCE.consumerToken, Configuration.INSTANCE.consumerSecret);
        mTwitter.setOAuthAccessToken(new AccessToken(Configuration.INSTANCE.accessToken, Configuration.INSTANCE.accessSecret));
    }

    public static class TweetActionParam implements ActionParam {

        Set<FeedMessage> feedMessages;

        public TweetActionParam(Set<FeedMessage> feedMessages) {
            this.feedMessages = feedMessages;
        }

        public Set<FeedMessage> getFeedMessages() {
            return feedMessages;
        }

        public void setFeedMessages(Set<FeedMessage> feedMessages) {
            this.feedMessages = feedMessages;
        }
    }

}
