package fr.heffebaycay.sts.twitter_bot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import fr.heffebaycay.sts.twitter_bot.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;


public class Configuration {

    private static final Logger logger = LoggerFactory.getLogger(Configuration.class);

    public static Configuration INSTANCE;

    public String accessToken;
    public String accessSecret;
    public String consumerToken;
    public String consumerSecret;


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
        ObjectMapper xmlMapper = new XmlMapper();

        try {
            Configuration.INSTANCE = xmlMapper.readValue(new File(Constants.CONFIG_FILE), Configuration.class);
        } catch (IOException e) {
            logger.error("Failed to deserialize configuration: {}", e);
            throw new RuntimeException(e);
        }

    }

    public static void save() {
        ObjectMapper xmlMapper = new XmlMapper();

        try {
            xmlMapper.writeValue(new File(Constants.CONFIG_FILE), Configuration.INSTANCE);
        } catch (IOException e) {
            logger.error("Failed to serialize configuration: {}", e);
            throw new RuntimeException(e);
        }
    }

}
