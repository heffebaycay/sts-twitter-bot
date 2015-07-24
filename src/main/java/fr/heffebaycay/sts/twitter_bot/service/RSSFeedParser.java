package fr.heffebaycay.sts.twitter_bot.service;

import fr.heffebaycay.sts.twitter_bot.model.Feed;
import fr.heffebaycay.sts.twitter_bot.model.FeedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class RSSFeedParser {

    private static final Logger logger = LoggerFactory.getLogger(RSSFeedParser.class);

    static final String TITLE = "title";
    static final String DESCRIPTION = "description";
    static final String CHANNEL = "channel";
    static final String LANGUAGE  = "language";
    static final String LINK = "link";
    static final String AUTHOR = "author";
    static final String ITEM = "item";
    static final String PUB_DATE = "pubDate";
    static final String GUID = "guid";
    static final String GENERATOR = "generator";

    final URL url;

    public RSSFeedParser(String feedUrl) {
        try {
            this.url = new URL(feedUrl);
        } catch (MalformedURLException e) {
            logger.error("MalformedURLException: {}", e);
            throw new RuntimeException(e);
        }
    }

    public Feed readFeed() {
        Feed feed = null;
        try {
            boolean isFeedHeader = true;
            // Set header value initial to the empty String
            String description = "";
            String title = "";
            String link = "";
            String language = "";
            String generator = "";
            String guid = "";
            LocalDateTime pubDate = null;
            String author = "";

            // First, let's create a new XMLInputFactory
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();

            // Setup a new eventReader
            InputStream in = read();
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in);

            // read the XML document
            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();
                if (event.isStartElement()) {
                    String localPart = event.asStartElement().getName().getLocalPart();
                    switch (localPart) {
                        case ITEM:
                            if (isFeedHeader) {
                                isFeedHeader = false;
                                feed = new Feed(title, link, description, language, generator);
                            }
                            event = eventReader.nextEvent();
                            break;
                        case TITLE:
                            title = getCharacterData(eventReader);
                            break;
                        case DESCRIPTION:
                            description = getCharacterData(eventReader);
                            break;
                        case LINK:
                            link = getCharacterData(eventReader);
                            break;
                        case GUID:
                            guid = getCharacterData(eventReader);
                            break;
                        case LANGUAGE:
                            language = getCharacterData(eventReader);
                            break;
                        case PUB_DATE:
                            pubDate = getLocalDateTimeData(eventReader);
                            break;
                        case AUTHOR:
                            author = getCharacterData(eventReader);
                            break;
                        case GENERATOR:
                            generator = getCharacterData(eventReader);
                            break;
                    }
                } else if (event.isEndElement()) {
                    if (ITEM.equals(event.asEndElement().getName().getLocalPart())) {
                        FeedMessage message = new FeedMessage();
                        message.setAuthor(author);
                        message.setDescription(description);
                        message.setGuid(guid);
                        message.setLink(link);
                        message.setPubDate(pubDate);
                        message.setTitle(title);

                        feed.getMessages().add(message);
                        event = eventReader.nextEvent();
                        continue;
                    }
                }
            }

        } catch (XMLStreamException e) {
            logger.error("XMLStreamException: {}", e);
            throw new RuntimeException(e);
        }

        return feed;
    }

    private String getCharacterData(XMLEventReader eventReader) throws XMLStreamException {
        StringBuilder builder = new StringBuilder();

        while(eventReader.peek() instanceof Characters) {
            XMLEvent event = eventReader.nextEvent();
            builder.append(event.asCharacters().getData());
        }

        return builder.toString();
    }

    private LocalDateTime getLocalDateTimeData(XMLEventReader eventReader) throws XMLStreamException {
        LocalDateTime result = null;

        XMLEvent event = eventReader.nextEvent();
        if (event instanceof Characters) {
            String strDateTime = event.asCharacters().getData();

            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.RFC_1123_DATE_TIME;
            result = LocalDateTime.parse(strDateTime, dateTimeFormatter);
        }

        return result;
    }

    private InputStream read() {
        try {
            return url.openStream();
        } catch (IOException e) {
            logger.error("IOException: {}", e);
            throw new RuntimeException(e);
        }
    }

}
