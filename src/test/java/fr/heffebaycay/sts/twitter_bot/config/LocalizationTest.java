package fr.heffebaycay.sts.twitter_bot.config;

import fr.heffebaycay.sts.twitter_bot.util.Constants;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.fail;

public class LocalizationTest {

    @Test
    public void localization_xml_file_should_be_valid() {
        try {
            Class.forName(Localization.class.getName());
        } catch (ClassNotFoundException e) {
            fail("Failed to find Localization class.");
        } catch (ExceptionInInitializerError e) {
            fail("Failed to load Localization class. Is the localization.xml file valid?", e);
        }
    }

    @Test
    public void localization_xml_names_should_be_unique() throws ParserConfigurationException, IOException, SAXException {
        InputStream localizationFile = LocalizationTest.class.getResourceAsStream("/" + Constants.LOCALIZATION_FILE);
        Document xmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(localizationFile);

        NodeList entries = xmlDocument.getElementsByTagName("entry");

        Set<String> fileNames = new HashSet<>();
        for (int i = 0; i < entries.getLength(); i++) {
            Node node = entries.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                NodeList strings = element.getElementsByTagName("string");

                assertThat(strings.getLength()).isEqualTo(2);
                String fileName = strings.item(0).getTextContent();
                String fileAlias = strings.item(1).getTextContent();

                assertThat(fileNames).doesNotContain(fileName);
                fileNames.add(fileName);
            }
        }
    }

}