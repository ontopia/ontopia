
// $Id: XTMReaderTest.java,v 1.36 2008/06/13 08:17:58 geir.gronmo Exp $

package net.ontopia.topicmaps.xml.test;

import java.io.*;
import java.util.*;

import org.xml.sax.SAXParseException;

import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.utils.PSI;
import net.ontopia.topicmaps.xml.*;

public class XTMReaderTest extends AbstractXMLTestCase {

  public XTMReaderTest(String name) {
    super(name);
  }
    
  public void setUp() {
  }

  // --- Utilities

  protected TopicMapIF readTopicMap(String filename) throws IOException {
    filename = resolveFileName("canonical" + File.separator + "in", filename);
    XTMTopicMapReader reader = new XTMTopicMapReader(new File(filename));
    reader.setValidation(false);
    TopicMapIF tm = reader.read();
    assertTrue("attempting to read second (non-existent) topic map did not give null",
           reader.read() == null);
    return tm;
  }

  // NOTE: this one validates!
  protected TopicMapIF readTopicMap(String dir, String filename) throws IOException {
    filename = resolveFileName(dir, filename);
    return new XTMTopicMapReader(new File(filename)).read();
  }

  protected Collection readTopicMaps(String filename) throws IOException {
    filename = resolveFileName("canonical" + File.separator + "in", filename);
    XTMTopicMapReader reader = new XTMTopicMapReader(new File(filename));
    reader.setValidation(false);
    return reader.readAll();
  }
  
  protected URILocator makeLocator(String uri) {
    try {
      return new URILocator(uri);
    }
    catch (java.net.MalformedURLException e) {
      System.err.println("(INTERNAL) " + e);
      return null;
    }
  }
    
  // --- Test cases

  public void testNothing() throws IOException {
    try {
      readTopicMap("various", "nothing.xtm");
      fail("reading XML document with no topic map did not throw exception");
    }
    catch (InvalidTopicMapException e) {
    }
    catch (net.ontopia.utils.OntopiaRuntimeException e) {
      if (!(e.getCause() instanceof org.xml.sax.SAXParseException))
        throw e;
    }
  }
  
  public void testEmptyTopicMap() throws IOException {
    TopicMapIF tm = readTopicMap("empty.xtm");
    assertTrue("empty topic map not empty after import",
           tm.getTopics().size() == 0 &&
           tm.getAssociations().size() == 0);
    assertTrue("topic map has no base address",
           tm.getStore().getBaseAddress() != null);
  }

  public void testMultipleTopicMaps() throws IOException {
    Collection tms = readTopicMaps("multiple-tms-read.xtm");
    assertTrue("reader doesn't recognize correct number of topic maps",
           tms.size() == 2);
    Iterator iter = tms.iterator();
    while (iter.hasNext()) {
      TopicMapIF tm = (TopicMapIF)iter.next();
      assertTrue("topic map has't got exactly one topic" + tm.getItemIdentifiers(),
             tm.getTopics().size() == 1);
    }
  }


  public void testImportTopicMaps() throws IOException {
    // Create empty store
    InMemoryTopicMapStore store = new InMemoryTopicMapStore();
    TopicMapIF tm = store.getTopicMap();

    // Import first XTM file
    String file1 = resolveFileName("canonical" + File.separator + "in", "latin1.xtm");
    TopicMapImporterIF importer1 = new XTMTopicMapReader(new File(file1));
    importer1.importInto(tm);

    // Import second XTM file
    String file2 = resolveFileName("canonical" + File.separator + "in", "mergeloop.xtm");
    TopicMapImporterIF importer2 = new XTMTopicMapReader(new File(file2));
    importer2.importInto(tm);

    // Check the result
    assertTrue("topic map has't got exactly three topics: " + tm.getTopics().size(),
           tm.getTopics().size() == 3); 
   
  }

  public void testTopicName() throws IOException {
    TopicMapIF tm = readTopicMap("basename.xtm");
    assertTrue("wrong number of topics after import",
           tm.getTopics().size() == 5);
    assertTrue("spurious topic map constructs found",
           tm.getAssociations().size() == 0);

    TopicIF topic = getTopicById(tm, "country");
    verifySingleTopicName(topic, "Country");
  }

  public void testVariants() throws IOException {
    TopicMapIF tm = readTopicMap("variants.xtm");
    assertTrue("wrong number of topics after import",
           tm.getTopics().size() == 2);
    assertTrue("spurious topic map constructs found",
           tm.getAssociations().size() == 0);

    TopicIF country = getTopicById(tm, "country");
    TopicIF norwegian = getTopicById(tm, "norwegian");
    assertTrue("topic has spurious children",
           country.getTypes().size() == 0 &&
           country.getRoles().size() == 0 &&
           country.getOccurrences().size() == 0);
    assertTrue("topic has wrong number of base names",
           country.getTopicNames().size() == 1);

    TopicNameIF basename = (TopicNameIF) country.getTopicNames().iterator().next();
    assertTrue("wrong basename value: '" + basename.getValue() + "'",
           basename.getValue().equals("Country"));
    assertTrue("wrong number of variant children",
           basename.getVariants().size() == 1);
        
    VariantNameIF variant = (VariantNameIF) basename.getVariants().iterator().next();
    assertTrue("wrong variant value: '" + variant.getValue() + "'",
           variant.getValue().equals("Land"));
    assertTrue("wrong scope of variant",
           variant.getScope().size() == 1 &&
           variant.getScope().iterator().next().equals(norwegian));
  }

  public void testOccurrences() throws IOException {
    TopicMapIF tm = readTopicMap("occurrences.xtm");
    assertTrue("wrong number of topics after import",
           tm.getTopics().size() == 4);
    assertTrue("spurious topic map constructs found",
           tm.getAssociations().size() == 0);

    TopicIF norway = getTopicById(tm, "norway");
    TopicIF homepage = getTopicById(tm, "homepage");
    verifySingleTopicName(homepage, "Home page");
    TopicIF tourism = getTopicById(tm, "tourism");
    verifySingleTopicName(tourism, "Tourism");
    assertTrue("topic has spurious children",
           norway.getTypes().size() == 0 &&
           norway.getRoles().size() == 0 &&
           norway.getTopicNames().size() == 1);
    assertTrue("topic has wrong number of occurrences",
           norway.getOccurrences().size() == 4);

    Iterator it = norway.getOccurrences().iterator();
    LocatorIF norge = makeLocator("http://www.norge.no");
    LocatorIF norwaycom = makeLocator("http://www.norway.com");
    LocatorIF visit = makeLocator("http://www.visitnorway.com");
    while (it.hasNext()) {
      OccurrenceIF occ = (OccurrenceIF) it.next();

      if (occ.getLocator() == null) {
        assertTrue("occurrence " + occ + " has spurious themes",
               occ.getScope().size() == 0);
        assertTrue("occurrence has invalid resource data",
               occ.getValue().equals("Norway is a nice country."));
      } else if (occ.getLocator().equals(norge)) {
        assertTrue("occurrence " + occ + " has spurious themes",
               occ.getScope().size() == 0);
        assertTrue("occurrence " + occ + " has wrong type",
               occ.getType().equals(homepage));
                
      } else if (occ.getLocator().equals(norwaycom)) {
        assertTrue("occurrence " + occ + " has spurious themes",
               occ.getScope().size() == 0);
        assertTrue("occurrence " + occ + " has spurious type",
               occ.getType().getSubjectIdentifiers().contains(PSI.getXTMOccurrence()));

      } else if (occ.getLocator().equals(visit)) {
        assertTrue("occurrence " + occ + " has wrong number of themes",
               occ.getScope().size() == 1);
        assertTrue("occurrence " + occ + " has wrong theme",
               occ.getScope().iterator().next().equals(tourism));
        assertTrue("occurrence " + occ + " has spurious type",
									 occ.getType().getSubjectIdentifiers().contains(PSI.getXTMOccurrence()));
                
      } else
        fail("spurious occurrence: " + occ);
    }
  }

  public void testTopicNameScope() throws IOException {
    TopicMapIF tm = readTopicMap("basename-scope.xtm");
    assertTrue("wrong number of topics after import",
           tm.getTopics().size() == 3);
    assertTrue("spurious topic map constructs found",
           tm.getAssociations().size() == 0);

    TopicIF norway = getTopicById(tm, "norway");
    TopicIF foo = getTopicById(tm, "foo");
    TopicIF bar = getTopicById(tm, "bar");
    verifySingleTopicName(foo, "Foo");
    verifySingleTopicName(bar, "Bar");

    assertTrue("topic has spurious children",
           norway.getTypes().size() == 0 &&
           norway.getRoles().size() == 0 &&
           norway.getOccurrences().size() == 0);
        
    TopicNameIF basename = (TopicNameIF) norway.getTopicNames().iterator().next();
    assertTrue("wrong basename value: '" + basename.getValue() + "'",
           basename.getValue().equals("Norway"));

    assertTrue("wrong scope on basename",
           basename.getScope().size() == 2 &&
           basename.getScope().contains(foo) && 
           basename.getScope().contains(bar));
  }

  // this is a regression test for bug #457
  // see http://www.y12.doe.gov/sgml/sc34/document/0299.htm#merge-prop-srclocs
  public void testTopicMapReification() throws IOException {
    TopicMapIF tm = readTopicMap("canonical" + File.separator + "extra",
                                 "master-of-reified-tm.xtm");
    assertTrue("topic map has been incorrectly merged with child topic map",
               tm.getReifier() == null);
  }

  public void testXMLBaseAndBaseLocator() throws IOException {
    TopicMapIF tm = readTopicMap("canonical" + File.separator + "extra",
                                 "tm-xmlbase.xtm");
    LocatorIF base = tm.getStore().getBaseAddress();
    assertTrue("topicmap.getBaseLocator() should not be set to xml:base",
               base.getAddress().startsWith("file:"));
  }

  // tests for bug #523
  public void testEntity() throws IOException {
    TopicMapIF tm = readTopicMap("canonical" + File.separator + "extra",
                                 "entities.xtm");
    TopicIF topic = (TopicIF) tm.getTopics().iterator().next();
    LocatorIF subjind = (LocatorIF) topic.getSubjectIdentifiers().iterator().next();
    assertTrue("base URI not updated for entities (" + subjind + ")",
               subjind.getAddress().endsWith(".ent"));
  }

  // --- Validation tests

  public void testValidTM() throws IOException {
    readTopicMap("various", "jill.xtm");
  }

  public void testInvalidTM() throws IOException {
    try {
      readTopicMap("canonical" + File.separator + "errors", "badxtm.xtm");
      fail("Invalid topic map was allowed to load");
    } catch (OntopiaRuntimeException e) {
      assertTrue("Error not from XTM validation: " + e.getCause(),
                 e.getCause() instanceof SAXParseException);
    }
  }

  public void testXTMValidIfDTDRead() throws IOException {
    // motivated by bug #864
    readTopicMap("various", "valid-if-dtd-read.xtm");
  }

  public void testXTMValidIfDTDReadButDTDRefBad() throws IOException {
    // motivated by bug #864
    try {
      readTopicMap("various", "valid-but-bad-dtdref.xtm");
      fail("Invalid topic map was allowed to load"); // well, ok, it *is* valid, but
                                                     // we can't know that
    } catch (OntopiaRuntimeException e) {
      assertTrue("Error not from XTM validation: " + e.getCause(),
                 e.getCause() instanceof SAXParseException);
    }
  }

  public void testErrorLocationReporting1() throws IOException {
    // verifies that the XTM 1.0 reader actually reports where in the file
    // invalidity errors occur
    try {
      readTopicMap("various", "invalid1.xtm");
      fail("No error detected in invalid file!");
    } catch (OntopiaRuntimeException e) {
      if (e.getCause() instanceof SAXParseException) {
        SAXParseException ex = (SAXParseException) e.getCause();
        assertTrue("wrong error file: " + ex.getSystemId(),
                   ex.getSystemId().endsWith("invalid1.xtm"));
        assertTrue("wrong error line: " + ex.getLineNumber(),
                   ex.getLineNumber() == 7);
      } else
        fail("Unknown cause of error: " + e);
    }
  }

  public void testErrorLocationReporting2() throws IOException {
    // verifies that the XTM 2.0 reader actually reports where in the file
    // invalidity errors occur
    try {
      readTopicMap("various", "invalid2.xtm");
      fail("No error detected in invalid file!");
    } catch (OntopiaRuntimeException e) {
      if (e.getCause() instanceof SAXParseException) {
        SAXParseException ex = (SAXParseException) e.getCause();
        assertTrue("wrong error file: " + ex.getSystemId(),
                   ex.getSystemId().endsWith("invalid2.xtm"));
        assertTrue("wrong error line: " + ex.getLineNumber(),
                   ex.getLineNumber() == 2);
      } else
        fail("Unknown cause of error: " + e);
    }
  }
  
  // --- Supporting methods

  private void verifySingleTopicName(TopicIF topic, String name) {
    assertTrue("topic has spurious children",
           topic.getTypes().size() == 0 &&
           topic.getRoles().size() == 0 &&
           topic.getOccurrences().size() == 0);
    assertTrue("topic has wrong number of base names",
           topic.getTopicNames().size() == 1);

    TopicNameIF basename = (TopicNameIF) topic.getTopicNames().iterator().next();
    assertTrue("wrong basename value: '" + basename.getValue() + "'",
           basename.getValue().equals(name));
    assertTrue("basename has spurious children",
           basename.getVariants().size() == 0);
    assertTrue("basename has spurious themes",
           basename.getScope().size() == 0);
  }
    
}
