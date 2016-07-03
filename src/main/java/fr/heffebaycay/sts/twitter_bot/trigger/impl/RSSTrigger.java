package fr.heffebaycay.sts.twitter_bot.trigger.impl;

import fr.heffebaycay.sts.twitter_bot.action.Action;
import fr.heffebaycay.sts.twitter_bot.action.ActionParam;
import fr.heffebaycay.sts.twitter_bot.model.*;
import fr.heffebaycay.sts.twitter_bot.action.impl.TweetAction;
import fr.heffebaycay.sts.twitter_bot.service.RSSFeedParser;
import fr.heffebaycay.sts.twitter_bot.trigger.Trigger;
import fr.heffebaycay.sts.twitter_bot.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.TreeSet;

public class RSSTrigger extends Trigger {

    private static final String STATE_FILE_NAME = Constants.CONFIG_DIR + File.separator + "rssTriggerStateFile.ser";
    private static final Logger logger = LoggerFactory.getLogger(RSSTrigger.class);

    private Set<FeedMessage> newFeedMessages = new TreeSet<>();

    public RSSTrigger(Action action) {
        this.action = action;
    }

    @Override
    public boolean isTriggered() {
        Feed feed = fetchRSSFeed();
        StateFile stateFile = fetchStateFile();

        logger.info(String.format("State file info: %s", stateFile == null ? "null" : stateFile.getPubDate()));

        for (FeedMessage feedMessage : feed.getMessages()) {
            logger.debug("Feed message pubdate is {}", feedMessage.getPubDate());
            if (stateFile == null || feedMessage.getPubDate().compareTo( stateFile.getPubDate() ) > 0) {
                // Feed message is more recent than last state
                newFeedMessages.add(feedMessage);
                logger.info(String.format("New message: %s", feedMessage));
            }
        }

        if (newFeedMessages.size() > 0) {
            // New messages are available, so we can update the state file
            updateStateFile();
        } else {
            logger.info("No new messages available");
        }


        return newFeedMessages.size() > 0;
    }

    private Feed fetchRSSFeed() {
        RSSFeedParser parser = new RSSFeedParser(Constants.STS_FEED_URL);
        return parser.readFeed();
    }

    private StateFile fetchStateFile() {
        File stateFile = new File(STATE_FILE_NAME);

        if (!stateFile.isFile()) {
            return null;
        } else {
            try {
                FileInputStream fis = new FileInputStream(stateFile);
                ObjectInputStream ois = new ObjectInputStream(fis);
                StateFile result = (StateFile) ois.readObject();
                ois.close();

                return result;
            } catch (FileNotFoundException e) {
                logger.error("Failed to find state file. {}", e);
                throw new RuntimeException(e);
            } catch (IOException e) {
                logger.error("Failed to create ObjectInputStream. {}", e);
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                logger.error("Failed to find class for deserialized object. {}", e);
                throw new RuntimeException(e);
            }
        }
    }

    private void updateStateFile() {
        StateFile stateFileContent = new StateFile(LocalDateTime.now());

        File stateFile = new File(STATE_FILE_NAME);

        try {
            FileOutputStream fos = new FileOutputStream(stateFile);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(stateFileContent);
            oos.close();
        } catch (FileNotFoundException e) {
            logger.error("Failed to create FileOutputStream: {}", e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            logger.error("Failed to create ObjectOutputStream: {}", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public ActionParam getActionParam() {
        return new TweetAction.TweetActionParam(newFeedMessages);
    }
}
