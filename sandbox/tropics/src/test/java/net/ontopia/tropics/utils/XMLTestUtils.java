package net.ontopia.tropics.utils;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMLTestUtils {

  public static Document readIntoDOM(String response) throws ParserConfigurationException, SAXException, IOException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    return builder.parse(new InputSource(new StringReader(response)));
  }

  public static NodeList getLengthCheckedNodeList(Document doc, String tagName, int expectedNrOfElements) {
    NodeList list = doc.getElementsByTagName(tagName);    
    assertEquals(expectedNrOfElements, list.getLength());    
    return list;
  }

  public static NodeList getLengthCheckedNodeList(Element el, String tagName, int expectedNrOfElements) {
    NodeList list = el.getElementsByTagName(tagName);    
    assertEquals(expectedNrOfElements, list.getLength());    
    return list;
  }
}
