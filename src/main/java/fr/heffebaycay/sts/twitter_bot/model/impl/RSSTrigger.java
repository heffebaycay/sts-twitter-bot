package fr.heffebaycay.sts.twitter_bot.model.impl;

import fr.heffebaycay.sts.twitter_bot.model.*;
import fr.heffebaycay.sts.twitter_bot.service.RSSFeedParser;
import fr.heffebaycay.sts.twitter_bot.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

public class RSSTrigger extends Trigger {

    private static final String STATE_FILE_NAME = "rssTriggerStateFile.ser";
    private static final Logger logger = LoggerFactory.getLogger(RSSTrigger.class);

    protected Set<FeedMessage> newFeedMessages = new TreeSet<>();

    public RSSTrigger(Action action) {
        this.action = action;
    }

    @Override
    public boolean isTriggered() {
        Feed feed = fetchRSSFeed();
        StateFile stateFile = fetchStateFile();

        logger.info(String.format("State file info: %s", stateFile == null ? "null" : stateFile.getPubDate()));

        Pattern stsUpdatePattern = Pattern.compile("STS-ALERT \\(([0-9]+) new strings?\\)");

        for (FeedMessage feedMessage : feed.getMessages()) {

            if (stateFile == null || feedMessage.getPubDate().compareTo( stateFile.getPubDate() ) > 0) {
                // Feed message is more recent than last state
                newFeedMessages.add(feedMessage);
                logger.info(String.format("New message: %s", feedMessage));
            }
        }

        updateStateFile();

        return newFeedMessages.size() > 0 ? true : false;
    }

    private Feed fetchRSSFeed() {
        RSSFeedParser parser = new RSSFeedParser(Constants.STS_FEED_URL);
        Feed feed = parser.readFeed();
        return feed;
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
        TweetAction.TweetActionParam params = new TweetAction.TweetActionParam(newFeedMessages);

        return params;
    }
}
