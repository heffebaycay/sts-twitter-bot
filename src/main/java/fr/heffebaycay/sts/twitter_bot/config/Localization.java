package fr.heffebaycay.sts.twitter_bot.config;


import fr.heffebaycay.sts.twitter_bot.util.Constants;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

@Root
public class Localization {

    private static final Logger logger = LoggerFactory.getLogger(Localization.class);

    public static Localization INSTANCE;

    @ElementMap
    public Map<String, String> aliases;

    private Localization() {

    }

    static {
        Localization.INSTANCE = new Localization();

        load();

    }

    public static void load() {
        Serializer serializer = new Persister();

        InputStream localizationIS = Localization.class.getResourceAsStream("/" + Constants.LOCALIZATION_FILE);

        try {
            Localization.INSTANCE = serializer.read(Localization.class, localizationIS);
        } catch (Exception e) {
            logger.error("Failed to load localization file: {}", e);
            throw new RuntimeException(e);
        }

    }

    public static void save() {
        Serializer serializer = new Persister();

        File localizationFile = new File(Constants.LOCALIZATION_FILE);

        try {
            serializer.write(INSTANCE, localizationFile);
        } catch (Exception e) {
            logger.error("Failed to save localization file: {}", e);
            throw new RuntimeException("Failed to save localization file", e);
        }
    }

}
