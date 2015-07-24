package fr.heffebaycay.sts.twitter_bot.config;

import fr.heffebaycay.sts.twitter_bot.util.Constants;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

@Root
public class Configuration {

    private static final Logger logger = LoggerFactory.getLogger(Configuration.class);

    public static Configuration INSTANCE;

    @Element(required = false)
    public String accessToken;
    @Element(required = false)
    public String accessSecret;
    @Element(required = false)
    public String consumerToken;
    @Element(required = false)
    public String consumerSecret;
    @Element(required = false)
    public boolean dryRun;

    private Configuration() {

    }

    static {
        INSTANCE = new Configuration();

        File configFile = new File(Constants.CONFIG_FILE);

        if(configFile.isFile()) {
            load();
        } else {
            save();
        }

    }

    public static void load() {
        Serializer serializer = new Persister();

        File configFile = new File(Constants.CONFIG_FILE);

        try {
            Configuration.INSTANCE = serializer.read(Configuration.class, configFile);
        } catch (Exception e) {
            logger.error("Failed to deserialize configuration: {}", e);
            throw new RuntimeException(e);
        }

    }

    public static void save() {
        Serializer serializer = new Persister();

        File configFile = new File(Constants.CONFIG_FILE);

        try {
            serializer.write(Configuration.INSTANCE, configFile);
        } catch (Exception e) {
            logger.error("Failed to serialize configuration: {}", e);
            throw new RuntimeException(e);
        }

    }

}
