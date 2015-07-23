package fr.heffebaycay.sts.twitter_bot.ui;

import fr.heffebaycay.sts.twitter_bot.model.Trigger;
import fr.heffebaycay.sts.twitter_bot.model.impl.RSSTrigger;
import fr.heffebaycay.sts.twitter_bot.model.impl.TweetAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TwitterBotCLI {

    private static final Logger logger = LoggerFactory.getLogger(TwitterBotCLI.class);

    public static void main(String[] args) {

        TweetAction tweetAction = new TweetAction();
        Trigger rssTrigger = new RSSTrigger(tweetAction);

        if (rssTrigger.isTriggered()) {
            rssTrigger.getAction().run(rssTrigger.getActionParam());
        }

    }


}
