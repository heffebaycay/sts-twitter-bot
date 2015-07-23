package fr.heffebaycay.sts.twitter_bot.model;


import java.io.Serializable;
import java.time.LocalDateTime;

public class StateFile implements Serializable {

    private static final long serialVersionUID = 1L;

    protected LocalDateTime pubDate;

    public StateFile() {

    }

    public StateFile(LocalDateTime pubDate) {
        this.pubDate = pubDate;
    }

    public LocalDateTime getPubDate() {
        return pubDate;
    }

    public void setPubDate(LocalDateTime pubDate) {
        this.pubDate = pubDate;
    }

}
