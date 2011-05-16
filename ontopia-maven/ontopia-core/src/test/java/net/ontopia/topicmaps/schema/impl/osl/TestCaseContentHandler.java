package net.ontopia.topicmaps.schema.impl.osl;

import java.util.ArrayList;
import java.util.Collection;
import net.ontopia.xml.SAXTracker;
import org.junit.Ignore;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

@Ignore
public class TestCaseContentHandler extends SAXTracker {

  private Collection<String[]> tests;

  public TestCaseContentHandler() {
    this.tests = new ArrayList<String[]>();
  }

  public Collection<String[]> getTests() {
    return tests;
  }

  public void startElement(String nsuri, String lname, String qname,
          Attributes attrs) throws SAXException {

    if (qname.equals("test")) {
      tests.add(new String[]{attrs.getValue("topicmap"), attrs.getValue("schema"),
          attrs.getValue("valid")});
    }
    super.startElement(nsuri, lname, qname, attrs);
  }
}
