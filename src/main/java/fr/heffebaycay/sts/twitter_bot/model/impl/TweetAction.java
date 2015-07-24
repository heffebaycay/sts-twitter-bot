package fr.heffebaycay.sts.twitter_bot.model.impl;

import fr.heffebaycay.sts.twitter_bot.config.Localization;
import fr.heffebaycay.sts.twitter_bot.model.Action;
import fr.heffebaycay.sts.twitter_bot.model.ActionParam;
import fr.heffebaycay.sts.twitter_bot.model.FeedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TweetAction implements Action {

    private static final Logger logger = LoggerFactory.getLogger(TweetAction.class);

    @Override
    public void run(ActionParam param) {

        if (param instanceof TweetActionParam == false) {
            return;
        }

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
                StringBuilder tweetMessage = new StringBuilder(tweetIntro);

                int nbOthers = 0;

                while (itemMatcher.find()) {
                    int nbStringsInFile = Integer.parseInt(itemMatcher.group(1));
                    String fileName = itemMatcher.group(2);

                    if (Localization.INSTANCE.aliases.containsKey(fileName)) {
                        String subUpdate = String.format("%1$s (%2$d), ", Localization.INSTANCE.aliases.get(fileName), nbStringsInFile );
                        tweetMessage.append(subUpdate);
                    } else {
                        nbOthers += nbStringsInFile;
                    }
                }

                if (nbOthers > 0) {
                    tweetMessage.append(String.format("Others (%1$d)", nbOthers));
                }

                String tweet = tweetMessage.toString();

                logger.info("Tweet: {}", tweet);
                logger.info("NbChars: {}", tweet.length());


            }

        }
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
