package fr.heffebaycay.sts.twitter_bot.model.impl;

import fr.heffebaycay.sts.twitter_bot.model.Action;
import fr.heffebaycay.sts.twitter_bot.model.ActionParam;
import fr.heffebaycay.sts.twitter_bot.model.FeedMessage;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TweetAction implements Action {

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

                while (itemMatcher.find()) {
                    int nbStringsInFile = Integer.parseInt(itemMatcher.group(1));
                    String fileName = itemMatcher.group(2);



                }

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
