
package net.ontopia.topicmaps.xml.test;

import java.io.IOException;

import junit.framework.*;

import org.xml.sax.XMLReader;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.DefaultHandler;

import net.ontopia.xml.DefaultXMLReaderFactory;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.*;

// extending XTMExporterTest in order to reuse some of the helper code
public class XTM2ExporterTest extends AbstractXMLTestCase {
  
  public XTM2ExporterTest(String name) {
    super(name);
    version = 2; // ensure that export uses XTM 2.0
  }

  // --- Test cases

  // motivated by issue 133
  public void testOccurrenceURIExport() throws IOException, SAXException {
    prepareTopicMap();
    TopicIF topic = builder.makeTopic();
    TopicIF occtype = builder.makeTopic();
    LocatorIF loc = new URILocator("http://example.org/foo+bar");
    builder.makeOccurrence(topic, occtype, loc);

    // export to file
    export();

    // reread file with SAX
    SearchAttributeValue handler =
      new SearchAttributeValue("resourceRef", "href", loc.getExternalForm(),
                               SearchAttributeValue.REQUIRED);
    parseFile(handler);
    handler.check();
  }

  public void testVariantURIExport() throws IOException, SAXException {
    prepareTopicMap();
    TopicIF topic = builder.makeTopic();
    TopicNameIF name = builder.makeTopicName(topic, "Topic");
    LocatorIF loc = new URILocator("http://example.org/foo+bar");
    builder.makeVariantName(name, loc);

    // export to file
    export();

    // reread file with SAX
    SearchAttributeValue handler =
      new SearchAttributeValue("resourceRef", "href", loc.getExternalForm(),
                               SearchAttributeValue.REQUIRED);
    parseFile(handler);
    handler.check();
  }

  public void testStringDatatype() throws IOException, SAXException {
    prepareTopicMap();
    TopicIF topic = builder.makeTopic();
    TopicIF occtype = builder.makeTopic();
    String value = "hey ho";
    builder.makeOccurrence(topic, occtype, value);

    // export to file
    export();

    // reread file with SAX
    SearchAttributeValue handler =
      new SearchAttributeValue("resourceData", "datatype",
                               DataTypes.TYPE_STRING.getAddress(),
                               SearchAttributeValue.FORBIDDEN);
    parseFile(handler);
    handler.check();
  }
  
  // --- Internal helper methods
  
  private void parseFile(ContentHandler handler) throws IOException, SAXException {
    InputSource source = new InputSource(tmbase.getExternalForm());
    XMLReader parser = new DefaultXMLReaderFactory().createXMLReader();
    parser.setContentHandler(handler);
    parser.parse(source);
  }
  
  // --- Internal helper classes

  class SearchAttributeValue extends DefaultHandler {
    public static final int REQUIRED = 0;
    public static final int FORBIDDEN = 1;
    private String element;
    private String attribute;
    private String value;
    private int allowed;
    private boolean foundElement;
    private boolean foundAttribute;
    private boolean foundValue;

    public SearchAttributeValue(String element, String attribute, String value,
                                 int allowed) {
      this.element = element;
      this.attribute = attribute;
      this.value = value;
      this.allowed = allowed;
    }

    public void check() {
      assertTrue("element " + element + " not found", foundElement);
      if (allowed == REQUIRED)
        assertTrue("attribute " + attribute + " not found", foundAttribute);

      if (allowed == REQUIRED)
        assertTrue("value " + value + " not found", foundValue);
      else if (allowed == FORBIDDEN)
        assertTrue("value " + value + " found", !foundValue);
      else
        fail("Unknown allowed value: " + allowed);
    }

    public void startElement(String nsuri, String localname,
                             String qname, Attributes atts) {
      if (localname.equals(element)) {
        foundElement = true;
        String thevalue = atts.getValue(attribute);
        if (thevalue != null) {
          foundAttribute = true;
          foundValue = foundValue || thevalue.equals(value);
        }
      }
    }
  }
}
