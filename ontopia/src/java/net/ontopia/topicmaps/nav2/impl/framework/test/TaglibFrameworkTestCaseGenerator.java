
// $Id: TaglibFrameworkTestCaseGenerator.java,v 1.23 2003/11/07 15:47:04 larsga Exp $

package net.ontopia.topicmaps.nav2.impl.framework.test;

import java.io.File;
import java.io.IOException;
import java.util.*;

import org.xml.sax.XMLReader;
import org.xml.sax.SAXException;

import net.ontopia.test.*;
import net.ontopia.xml.ConfiguredXMLReaderFactory;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.URIUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Genric Navigator Framework Testing tools.  Used to create
 * new tests for the taglibs.
 */
public class TaglibFrameworkTestCaseGenerator implements TestCaseGeneratorIF {

  // initialise logging facility
  static Logger log = LoggerFactory
    .getLogger(TaglibFrameworkTestCaseGenerator.class.getName());

  protected String root;
  protected String base;

  /**
   * Method used to generate a reference to the jsp files in the
   * test-data directory.
   *
   * @return An iterator which contains TaglibTestCase objects.
   */
  public Iterator generateTests() {
    root = AbstractOntopiaTestCase.getTestDirectory();
    base = root + File.separator + "nav2" + File.separator;
    Map result = null;
    Set tests = new HashSet();
    try {
      result = getTests();
    } catch (IOException e) {
      throw new OntopiaRuntimeException(e);
    } catch (SAXException e) {
      throw new OntopiaRuntimeException(e);
    }
    Iterator it = result.keySet().iterator();
    while (it.hasNext()) {
      String key = (String) it.next();
      Collection value = (Collection) result.get(key);
      StringTokenizer strtok = new StringTokenizer(key, "$$$");
      String tm = strtok.nextToken();
      String jsp = strtok.nextToken();
      Iterator iter = value.iterator();
      while (iter.hasNext()) {
        Map test_params = (HashMap) iter.next();
        tests.add(new TaglibTestCase(jsp, base, tm, test_params));
      }
    }
    return tests.iterator();
  }

  
  // -- internal helper method(s)
  
  private Map getTests() throws SAXException, IOException {
    XMLReader parser = new ConfiguredXMLReaderFactory().createXMLReader();
    String testfilename = System.getProperty("net.ontopia.test.nav2.testspec",
                                             "config" + File.separator +
                                             "tests.xml");
    String source = base + testfilename;
    TestCaseContentHandler handler = new TestCaseContentHandler();
    handler.register(parser);
    parser.parse(URIUtils.toURL(new File(source)).toString());
    return handler.getTests();
  }

}
