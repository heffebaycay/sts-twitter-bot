package fr.heffebaycay.sts.twitter_bot.ui;

import fr.heffebaycay.sts.twitter_bot.trigger.Trigger;
import fr.heffebaycay.sts.twitter_bot.trigger.impl.RSSTrigger;
import fr.heffebaycay.sts.twitter_bot.action.impl.TweetAction;
import fr.heffebaycay.sts.twitter_bot.util.TwitterUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class TwitterBotCLI {

    private static final Logger logger = LoggerFactory.getLogger(TwitterBotCLI.class);

    public static void main(String[] args) {

        try {
            Class.forName("fr.heffebaycay.sts.twitter_bot.config.Configuration");
            Class.forName("fr.heffebaycay.sts.twitter_bot.config.Localization");
        } catch (ClassNotFoundException e) {
            logger.error("Failed to find Configuration class: {}", e);
        }


        if (args.length == 1) {

            switch (args[0]) {
                case "twitter:authenticate":
                    TwitterUtil.authenticateUser();
                    break;
                case "twitter:test":
                    TwitterUtil.testTwitter();
                    break;
                default:
                    System.out.println("Invalid arguments. Aborting.");
                    break;
            }

            return;

        }


        TweetAction tweetAction = new TweetAction();
        Trigger rssTrigger = new RSSTrigger(tweetAction);

        if (rssTrigger.isTriggered()) {
            rssTrigger.getAction().run(rssTrigger.getActionParam());
        }

    }


}
