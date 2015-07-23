package fr.heffebaycay.sts.twitter_bot.model.impl;

import fr.heffebaycay.sts.twitter_bot.model.Action;
import fr.heffebaycay.sts.twitter_bot.model.ActionParam;
import fr.heffebaycay.sts.twitter_bot.model.FeedMessage;

import java.util.Set;


public class TweetAction implements Action {

    @Override
    public void run(ActionParam param) {

        if (param instanceof TweetActionParam == false) {
            return;
        }

        TweetActionParam tweetParams = (TweetActionParam) param;

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
