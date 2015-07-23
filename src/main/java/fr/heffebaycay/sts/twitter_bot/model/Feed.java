package fr.heffebaycay.sts.twitter_bot.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Fabien on 18/07/2015.
 */
public class Feed {

    final String title;
    final String link;
    final String description;
    final String language;
    final String generator;

    //final List<FeedMessage> messages = new ArrayList<FeedMessage>();
    final Set<FeedMessage> messages = new TreeSet<>();

    public Feed(String title, String link, String description, String language, String generator) {
        this.title = title;
        this.link = link;
        this.description = description;
        this.language = language;
        this.generator = generator;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getDescription() {
        return description;
    }

    public String getLanguage() {
        return language;
    }

    public String getGenerator() {
        return generator;
    }

    public Set<FeedMessage> getMessages() {
        return messages;
    }

    @Override
    public String toString() {
        return "Feed{" +
                "title='" + title + '\'' +
                ", link='" + link + '\'' +
                ", description='" + description + '\'' +
                ", language='" + language + '\'' +
                ", generator='" + generator + '\'' +
                '}';
    }
}
