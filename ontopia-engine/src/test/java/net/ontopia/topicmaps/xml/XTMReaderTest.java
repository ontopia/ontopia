/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

package net.ontopia.topicmaps.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapReaderIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.utils.PSI;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.TestFileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

public class XTMReaderTest extends AbstractXMLTestCase {

  private final static String testdataDirectory = "canonical";

  // --- Utilities

  protected TopicMapIF readTopicMap(String filename) throws IOException {
    filename = TestFileUtils.getTestInputFile(testdataDirectory, "in", filename);
    XTMTopicMapReader reader = new XTMTopicMapReader(TestFileUtils.getTestInputURL(filename));
    reader.setValidation(false);
    TopicMapIF tm = reader.read();
    Assert.assertTrue(
        "attempting to read second (non-existent) topic map did not give null",
        reader.read() == null);
    return tm;
  }

  // NOTE: this one validates!
  protected TopicMapIF assertReadTopicMap(String dir, String filename)
      throws IOException {
    filename = TestFileUtils.getTestInputFile(dir, filename);
    return new XTMTopicMapReader(TestFileUtils.getTestInputURL(filename)).read();
  }

  protected TopicMapIF assertReadTopicMap(String dir, String subdir, String filename)
      throws IOException {
    filename = TestFileUtils.getTestInputFile(dir, subdir, filename);
    return new XTMTopicMapReader(TestFileUtils.getTestInputURL(filename)).read();
  }

  protected Collection assertReadTopicMaps(String filename) throws IOException {
    filename = TestFileUtils.getTestInputFile(testdataDirectory, "in", filename);
    XTMTopicMapReader reader = new XTMTopicMapReader(TestFileUtils.getTestInputURL(filename));
    reader.setValidation(false);
    return reader.readAll();
  }

  protected URILocator makeLocator(String uri) {
    return URILocator.create(uri);
  }

  // --- Test cases

  @Test
  public void testNothing() throws IOException {
    try {
      assertReadTopicMap("various", "nothing.xtm");
      Assert.fail("reading XML document with no topic map did not throw exception");
    } catch (InvalidTopicMapException e) {
    } catch (net.ontopia.utils.OntopiaRuntimeException e) {
      if (!(e.getCause() instanceof org.xml.sax.SAXParseException)) {
        throw e;
      }
    }
  }

  @Test
  public void testEmptyTopicMap() throws IOException {
    TopicMapIF tm = readTopicMap("empty.xtm");
    Assert.assertTrue("empty topic map not empty after import",
        tm.getTopics().size() == 0 && tm.getAssociations().size() == 0);
    Assert.assertTrue("topic map has no base address",
        tm.getStore().getBaseAddress() != null);
  }

  @Test
  public void testMultipleTopicMaps() throws IOException {
    Collection tms = assertReadTopicMaps("multiple-tms-read.xtm");
    Assert.assertTrue("reader doesn't recognize correct number of topic maps", tms
        .size() == 2);
    Iterator iter = tms.iterator();
    while (iter.hasNext()) {
      TopicMapIF tm = (TopicMapIF) iter.next();
      Assert.assertTrue("topic map has't got exactly two topics"
          + tm.getItemIdentifiers(), tm.getTopics().size() == 2);
    }
  }

  @Test
  public void testImportTopicMaps() throws IOException {
    // Create empty store
    InMemoryTopicMapStore store = new InMemoryTopicMapStore();
    TopicMapIF tm = store.getTopicMap();

    // Import first XTM file
    String file1 = TestFileUtils.getTestInputFile(testdataDirectory, "in",
        "latin1.xtm");
    TopicMapReaderIF importer1 = new XTMTopicMapReader(TestFileUtils.getTestInputURL(file1));
    importer1.importInto(tm);

    // Import second XTM file
    String file2 = TestFileUtils.getTestInputFile(testdataDirectory, "in",
        "mergeloop.xtm");
    TopicMapReaderIF importer2 = new XTMTopicMapReader(TestFileUtils.getTestInputURL(file2));
    importer2.importInto(tm);

    // Check the result
    Assert.assertTrue("topic map has't got exactly four topics: "
        + tm.getTopics().size(), tm.getTopics().size() == 4);

  }

  @Test
  public void testTopicName() throws IOException {
    TopicMapIF tm = readTopicMap("basename.xtm");
    Assert.assertTrue("wrong number of topics after import",
        tm.getTopics().size() == 6);
    Assert.assertTrue("spurious topic map constructs found", tm.getAssociations()
        .size() == 0);

    TopicIF topic = getTopicById(tm, "country");
    verifySingleTopicName(topic, "Country");
  }

  @Test
  public void testVariants() throws IOException {
    TopicMapIF tm = readTopicMap("variants.xtm");
    Assert.assertTrue("wrong number of topics after import",
        tm.getTopics().size() == 3);
    Assert.assertTrue("spurious topic map constructs found", tm.getAssociations()
        .size() == 0);

    TopicIF country = getTopicById(tm, "country");
    TopicIF norwegian = getTopicById(tm, "norwegian");
    Assert.assertTrue("topic has spurious children", country.getTypes().size() == 0
        && country.getRoles().size() == 0
        && country.getOccurrences().size() == 0);
    Assert.assertTrue("topic has wrong number of base names", country.getTopicNames()
        .size() == 1);

    TopicNameIF basename = (TopicNameIF) country.getTopicNames().iterator()
        .next();
    Assert.assertTrue("wrong basename value: '" + basename.getValue() + "'", basename
        .getValue().equals("Country"));
    Assert.assertTrue("wrong number of variant children", basename.getVariants()
        .size() == 1);

    VariantNameIF variant = (VariantNameIF) basename.getVariants().iterator()
        .next();
    Assert.assertTrue("wrong variant value: '" + variant.getValue() + "'", variant
        .getValue().equals("Land"));
    Assert.assertTrue("wrong scope of variant", variant.getScope().size() == 1
        && variant.getScope().iterator().next().equals(norwegian));
  }

  @Test
  public void testOccurrences() throws IOException {
    TopicMapIF tm = readTopicMap("occurrences.xtm");
    Assert.assertTrue("wrong number of topics after import",
        tm.getTopics().size() == 5);
    Assert.assertTrue("spurious topic map constructs found", tm.getAssociations()
        .size() == 0);

    TopicIF norway = getTopicById(tm, "norway");
    TopicIF homepage = getTopicById(tm, "homepage");
    verifySingleTopicName(homepage, "Home page");
    TopicIF tourism = getTopicById(tm, "tourism");
    verifySingleTopicName(tourism, "Tourism");
    Assert.assertTrue("topic has spurious children", norway.getTypes().size() == 0
        && norway.getRoles().size() == 0 && norway.getTopicNames().size() == 1);
    Assert.assertTrue("topic has wrong number of occurrences", norway.getOccurrences()
        .size() == 4);

    Iterator it = norway.getOccurrences().iterator();
    LocatorIF norge = makeLocator("http://www.norge.no/");
    LocatorIF norwaycom = makeLocator("http://www.norway.com/");
    LocatorIF visit = makeLocator("http://www.visitnorway.com/");
    while (it.hasNext()) {
      OccurrenceIF occ = (OccurrenceIF) it.next();

      if (occ.getLocator() == null) {
        Assert.assertTrue("occurrence " + occ + " has spurious themes", occ.getScope()
            .size() == 0);
        Assert.assertTrue("occurrence has invalid resource data", occ.getValue()
            .equals("Norway is a nice country."));
      } else if (occ.getLocator().equals(norge)) {
        Assert.assertTrue("occurrence " + occ + " has spurious themes", occ.getScope()
            .size() == 0);
        Assert.assertTrue("occurrence " + occ + " has wrong type", occ.getType()
            .equals(homepage));

      } else if (occ.getLocator().equals(norwaycom)) {
        Assert.assertTrue("occurrence " + occ + " has spurious themes", occ.getScope()
            .size() == 0);
        Assert.assertTrue("occurrence " + occ + " has spurious type", occ.getType()
            .getSubjectIdentifiers().contains(PSI.getXTMOccurrence()));

      } else if (occ.getLocator().equals(visit)) {
        Assert.assertTrue("occurrence " + occ + " has wrong number of themes", occ
            .getScope().size() == 1);
        Assert.assertTrue("occurrence " + occ + " has wrong theme", occ.getScope()
            .iterator().next().equals(tourism));
        Assert.assertTrue("occurrence " + occ + " has spurious type", occ.getType()
            .getSubjectIdentifiers().contains(PSI.getXTMOccurrence()));

      } else {
        Assert.fail("spurious occurrence: " + occ);
      }
    }
  }

  @Test
  public void testTopicNameScope() throws IOException {
    TopicMapIF tm = readTopicMap("basename-scope.xtm");
    Assert.assertTrue("wrong number of topics after import",
        tm.getTopics().size() == 4);
    Assert.assertTrue("spurious topic map constructs found", tm.getAssociations()
        .size() == 0);

    TopicIF norway = getTopicById(tm, "norway");
    TopicIF foo = getTopicById(tm, "foo");
    TopicIF bar = getTopicById(tm, "bar");
    verifySingleTopicName(foo, "Foo");
    verifySingleTopicName(bar, "Bar");

    Assert.assertTrue("topic has spurious children", norway.getTypes().size() == 0
        && norway.getRoles().size() == 0 && norway.getOccurrences().size() == 0);

    TopicNameIF basename = (TopicNameIF) norway.getTopicNames().iterator()
        .next();
    Assert.assertTrue("wrong basename value: '" + basename.getValue() + "'", basename
        .getValue().equals("Norway"));

    Assert.assertTrue("wrong scope on basename", basename.getScope().size() == 2
        && basename.getScope().contains(foo)
        && basename.getScope().contains(bar));
  }

  // this is a regression test for bug #457
  // see http://www.y12.doe.gov/sgml/sc34/document/0299.htm#merge-prop-srclocs
  @Test
  public void testTopicMapReification() throws IOException {
    TopicMapIF tm = assertReadTopicMap(testdataDirectory, "extra",
        "master-of-reified-tm.xtm");
    Assert.assertTrue("topic map has been incorrectly merged with child topic map", tm
        .getReifier() == null);
  }

  @Test
  public void testXMLBaseAndBaseLocator() throws IOException {
    TopicMapIF tm = assertReadTopicMap(testdataDirectory, "extra",
        "tm-xmlbase.xtm");
    LocatorIF base = tm.getStore().getBaseAddress();
    Assert.assertTrue("topicmap.getBaseLocator() should not be set to xml:base", base
        .getAddress().startsWith("file:"));
  }

  // tests for bug #523
  @Test
  public void testEntity() throws IOException {
    TopicMapIF tm = assertReadTopicMap(testdataDirectory, "extra",
        "entities.xtm");
    Collection<TopicIF> topics = tm.getTopics();
    for (TopicIF topic : topics) {
      Collection<TopicNameIF> names = topic.getTopicNames();
      if (names != null && names.size() > 0) {
        LocatorIF subjind = (LocatorIF) topic.getSubjectIdentifiers()
            .iterator().next();
        Assert.assertTrue("base URI not updated for entities (" + subjind + ")",
            subjind.getAddress().endsWith(".ent"));
      }
    }
  }

  // --- Validation tests

  @Test
  public void testValidTM() throws IOException {
    assertReadTopicMap("various", "jill.xtm");
  }

  @Test
  public void testInvalidTM() throws IOException {
    try {
      assertReadTopicMap(testdataDirectory, "errors", "badxtm.xtm");
      Assert.fail("Invalid topic map was allowed to load");
    } catch (OntopiaRuntimeException e) {
      Assert.assertTrue("Error not from XTM validation: " + e.getCause(),
          e.getCause() instanceof SAXParseException);
    }
  }

  @Test
  public void testXTMValidIfDTDRead() throws IOException {
    // motivated by bug #864
    assertReadTopicMap("various", "valid-if-dtd-read.xtm");
  }

  @Test
  public void testXTMValidIfDTDReadButDTDRefBad() throws IOException {
    // motivated by bug #864
    try {
      assertReadTopicMap("various", "valid-but-bad-dtdref.xtm");
      Assert.fail("Invalid topic map was allowed to load"); // well, ok, it *is* valid,
                                                     // but
      // we can't know that
    } catch (OntopiaRuntimeException e) {
      Assert.assertTrue("Error not from XTM validation: " + e.getCause(),
          e.getCause() instanceof SAXParseException);
    }
  }

  @Test
  public void testErrorLocationReporting1() throws IOException {
    // verifies that the XTM 1.0 reader actually reports where in the file
    // invalidity errors occur
    try {
      assertReadTopicMap("various", "invalid1.xtm");
      Assert.fail("No error detected in invalid file!");
    } catch (OntopiaRuntimeException e) {
      if (e.getCause() instanceof SAXParseException) {
        SAXParseException ex = (SAXParseException) e.getCause();
        Assert.assertTrue("wrong error file: " + ex.getSystemId(), ex.getSystemId()
            .endsWith("invalid1.xtm"));
        Assert.assertTrue("wrong error line: " + ex.getLineNumber(), ex
            .getLineNumber() == 7);
      } else {
        Assert.fail("Unknown cause of error: " + e);
      }
    }
  }

  @Test
  public void testErrorLocationReporting2() throws IOException {
    // verifies that the XTM 2.0 reader actually reports where in the file
    // invalidity errors occur
    try {
      assertReadTopicMap("various", "invalid2.xtm");
      Assert.fail("No error detected in invalid file!");
    } catch (OntopiaRuntimeException e) {
      if (e.getCause() instanceof SAXParseException) {
        SAXParseException ex = (SAXParseException) e.getCause();
        Assert.assertTrue("wrong error file: " + ex.getSystemId(), ex.getSystemId()
            .endsWith("invalid2.xtm"));
        Assert.assertTrue("wrong error line: " + ex.getLineNumber(), ex
            .getLineNumber() == 2);
      } else {
        Assert.fail("Unknown cause of error: " + e);
      }
    }
  }

  @Test
  public void testReificationMergeBug() throws IOException {
    // tests a tricky case where topics are merged because of reification,
    // and this causes the current topic to become a merged-away stub, thus
    // leading to failures down the line.

    // first read one topic map
    TopicMapIF tm = assertReadTopicMap("various", "reification-bug-1.xtm");

    // then import the second one into it
    String file = TestFileUtils.getTestInputFile("various", "reification-bug-2.xtm");
    XTMTopicMapReader reader = new XTMTopicMapReader(TestFileUtils.getTestInputURL(file));
    reader.importInto(tm); // this should not crash!

    // do some testing verifying that the XTM was interpreted correctly
    Assert.assertTrue("wrong number of topics", tm.getTopics().size() == 2);
    Assert.assertTrue("topic map is reified", tm.getReifier() != null);
    TopicIF reifier = getTopicById(tm, "reifier");
    Assert.assertTrue("topic has no name", !reifier.getTopicNames().isEmpty());
  }

  @Test
  public void testReadFromURL() throws IOException {
    TopicMapIF tm = new XTMTopicMapReader(TestFileUtils.getTestInputURL("various", "jill.xtm")).read();
    Assert.assertNotNull(tm);
    Assert.assertEquals(39, tm.getTopics().size());
    
  }

  @Test
  public void testReadFromFile() throws IOException {
    TestFileUtils.transferTestInputDirectory("various");
    TopicMapIF tm = new XTMTopicMapReader(TestFileUtils.getTransferredTestInputFile("various", "jill.xtm")).read();
    Assert.assertNotNull(tm);
    Assert.assertEquals(39, tm.getTopics().size());
  }

  @Test
  public void testReadFromInputStream() throws IOException {
    TestFileUtils.transferTestInputDirectory("various");
    File file = TestFileUtils.getTransferredTestInputFile("various", "jill.xtm");
    TopicMapIF tm = new XTMTopicMapReader(new FileInputStream(file), new URILocator(file)).read();
    Assert.assertNotNull(tm);
    Assert.assertEquals(39, tm.getTopics().size());
  }

  @Test
  public void testReadFromReader() throws IOException {
    TestFileUtils.transferTestInputDirectory("various");
    File file = TestFileUtils.getTransferredTestInputFile("various", "jill.xtm");
    TopicMapIF tm = new XTMTopicMapReader(new FileReader(file), new URILocator(file)).read();
    Assert.assertNotNull(tm);
    Assert.assertEquals(39, tm.getTopics().size());
  }

  @Test
  public void testReadFromInputSource() throws IOException {
    TestFileUtils.transferTestInputDirectory("various");
    File file = TestFileUtils.getTransferredTestInputFile("various", "jill.xtm");
    TopicMapIF tm = new XTMTopicMapReader(new InputSource(new FileReader(file)), new URILocator(file)).read();
    Assert.assertNotNull(tm);
    Assert.assertEquals(39, tm.getTopics().size());
  }

  // --- Supporting methods

  private void verifySingleTopicName(TopicIF topic, String name) {
    Assert.assertTrue("topic has spurious children", topic.getTypes().size() == 0
        && topic.getRoles().size() == 0 && topic.getOccurrences().size() == 0);
    Assert.assertTrue("topic has wrong number of base names", topic.getTopicNames()
        .size() == 1);

    TopicNameIF basename = (TopicNameIF) topic.getTopicNames().iterator()
        .next();
    Assert.assertTrue("wrong basename value: '" + basename.getValue() + "'", basename
        .getValue().equals(name));
    Assert.assertTrue("basename has spurious children",
        basename.getVariants().size() == 0);
    Assert.assertTrue("basename has spurious themes", basename.getScope().size() == 0);
  }

}
