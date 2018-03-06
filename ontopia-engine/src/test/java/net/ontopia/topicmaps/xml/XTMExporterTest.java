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
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.utils.TestFileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class XTMExporterTest extends AbstractXMLTestCase {
  
  private final static String testdataDirectory = "canonical";

  @Before
  public void setVersion() {
    version = XTMVersion.XTM_1_0; // ensure export() uses XTM 1.0
  }

  // --- Test cases

  @Test
  public void testEncoding() throws IOException {
    TopicMapIF tm = load(testdataDirectory, "in", "latin1.xtm");
    File out = TestFileUtils.getTestOutputFile(testdataDirectory, "out", "tmp-latin1.xtm");
    XTMTopicMapWriter writer = new XTMTopicMapWriter(out, "iso-8859-1");
    writer.setVersion(XTMVersion.XTM_1_0);
    writer.write(tm);
    TopicMapIF tm2 = new XTMTopicMapReader(out).read();
    // check for a topic that has at least one name
    for (Object obj : tm2.getTopics()) {
      Collection<TopicNameIF> names = ((TopicIF) obj).getTopicNames();
      if (names != null && names.size() > 0) {
        TopicNameIF bn = names.iterator().next();
        Assert.assertTrue("base name value did not survive encoding change roundtrip",
               bn.getValue().equals("B\u00E6 b\u00E6 lille lam, har du noe \u00F8l"));
      }
    }
  }

  @Test
  public void testEncoding2() throws IOException {
    TopicMapIF tm = load(testdataDirectory, "in", "latin1.xtm");
    File out = TestFileUtils.getTestOutputFile(testdataDirectory, "out", "tmp-utf-8.xtm");
    XTMTopicMapWriter writer = new XTMTopicMapWriter(out);
    writer.setVersion(XTMVersion.XTM_1_0);
    writer.write(tm);
    TopicMapIF tm2 = new XTMTopicMapReader(out).read();
    // check for a topic that has at least one name
    for (Object obj : tm2.getTopics()) {
      Collection<TopicNameIF> names = ((TopicIF) obj).getTopicNames();
      if (names != null && names.size() > 0) {
        TopicNameIF bn = names.iterator().next();
        Assert.assertTrue("base name value did not survive encoding change roundtrip",
            bn.getValue().equals("B\u00E6 b\u00E6 lille lam, har du noe \u00F8l"));
      }
    }
  }

  /// id preservation
  
  @Test
  public void testPreservesTopicmapID() throws IOException {
    prepareTopicMap();
    topicmap.addItemIdentifier(sourceLoc);
    reload();
    assertXTM("topic map", topicmap);
  }

  @Test
  public void testPreservesTopicID() throws IOException {
    prepareTopicMap();

    TopicIF topic = builder.makeTopic();
    topic.addItemIdentifier(sourceLoc);
  
    reload();
    assertXTM("topic", getTopicById(topicmap, "id"));
  }

  @Test
  public void testPreservesTopicID2() throws IOException {
    prepareTopicMap();

    LocatorIF loc = tmbase.resolveAbsolute("#ide");
    TopicIF topic = builder.makeTopic();
    topic.addItemIdentifier(loc);
  
    reload();
    assertXTM("topic", getTopicById(topicmap, "ide"), loc);
  }

  @Test
  public void testPreservesBasenameID() throws IOException {
    prepareTopicMap();

    TopicIF topic = builder.makeTopic();
    TopicNameIF bn = builder.makeTopicName(topic, "bongomonog");
    bn.addItemIdentifier(sourceLoc);
  
    reload();
    
    // get a topic with at least one name
    topic = null;
    for (Object obj : topicmap.getTopics()) {
      Collection<TopicNameIF> names = ((TopicIF) obj).getTopicNames();
      if (names != null && names.size() > 0) {
        topic = (TopicIF) obj;
        break;
      }
    }
    
    Assert.assertNotNull("no topic found with a topic name after reload", topic);
    assertXTM("base name", (TopicNameIF) topic.getTopicNames().iterator().next());
  }

  @Test
  public void testPreservesVariantnameID() throws IOException {
    prepareTopicMap();

    TopicIF topic = builder.makeTopic();
    TopicNameIF bn = builder.makeTopicName(topic, "bongomonog");
    VariantNameIF vn = builder.makeVariantName(bn, "bongomonog", Collections.emptySet());
    vn.addItemIdentifier(sourceLoc);
  
    reload();
    
    // get a topic with at least one name
    topic = null;
    for (Object obj : topicmap.getTopics()) {
      Collection<TopicNameIF> names = ((TopicIF) obj).getTopicNames();
      if (names != null && names.size() > 0) {
        topic = (TopicIF) obj;
        break;
      }
    }
    
    Assert.assertNotNull("no topic found with a topic name after reload", topic);
    bn = (TopicNameIF) topic.getTopicNames().iterator().next();
    assertXTM("variant name", (VariantNameIF) bn.getVariants().iterator().next());
  }

  @Test
  public void testPreservesOccurrenceID() throws IOException {
    prepareTopicMap();

    TopicIF topic = builder.makeTopic();
		LocatorIF loc = URILocator.create("http://www.ontopia.net");
		topic.addSubjectIdentifier(loc);
    TopicIF otype = builder.makeTopic();
    OccurrenceIF occ = builder.makeOccurrence(topic, otype, loc);
    occ.addItemIdentifier(sourceLoc);
  
    reload();
    topic = topicmap.getTopicBySubjectIdentifier(loc);
    assertXTM("occurrence", (OccurrenceIF) topic.getOccurrences().iterator().next());
  }

//   @Test
//   public void testPreservesAssociationID() throws IOException {
//     prepareTopicMap();

//     AssociationIF assoc = builder.makeAssociation(builder.makeTopic());
//     assoc.addItemIdentifier(sourceLoc);
//     builder.makeAssociationRole(assoc);
  
//     reload();
//     assoc = (AssociationIF) topicmap.getAssociations().iterator().next();
//     check("assoc", assoc);
//   }
  
  /// empty strings and nulls

  @Test
  public void testEmptyTopicName() throws IOException {
    prepareTopicMap();

    TopicIF topic = builder.makeTopic();
    TopicNameIF bn = builder.makeTopicName(topic, "");

    reload();

    // get a topic with at least one name
    topic = null;
    for (Object obj : topicmap.getTopics()) {
      Collection<TopicNameIF> names = ((TopicIF) obj).getTopicNames();
      if (names != null && names.size() > 0) {
        topic = (TopicIF) obj;
        break;
      }
    }
    
    Assert.assertNotNull("no topic found with a topic name after reload", topic);

    Iterator it = topic.getTopicNames().iterator();
    Assert.assertTrue("empty base name lost on export and re-import",
           it.hasNext());
    bn = (TopicNameIF) it.next();
    Assert.assertTrue("empty base name has '" + bn.getValue() +
           "' instead of empty string on re-import",
           bn.getValue() != null && bn.getValue().equals(""));
  }

  @Test
  public void testEmptyVariantName() throws IOException {
    prepareTopicMap();

    TopicIF topic = builder.makeTopic();
    TopicNameIF bn = builder.makeTopicName(topic, "empty");
    VariantNameIF vn = builder.makeVariantName(bn, "", Collections.emptySet());

    reload();

    // get a topic with at least one name
    topic = null;
    for (Object obj : topicmap.getTopics()) {
      Collection<TopicNameIF> names = ((TopicIF) obj).getTopicNames();
      if (names != null && names.size() > 0) {
        topic = (TopicIF) obj;
        break;
      }
    }
    
    Assert.assertNotNull("no topic found with a topic name after reload", topic);
    bn = (TopicNameIF) topic.getTopicNames().iterator().next();
    
    Iterator it = bn.getVariants().iterator();
    Assert.assertTrue("empty variant name lost on export and re-import",
           it.hasNext());
    vn = (VariantNameIF) it.next();
    Assert.assertTrue("empty variant name has '" + vn.getValue() +
           "' instead of empty string on re-import",
           vn.getValue() != null && vn.getValue().equals(""));
  }
  
  @Test
  public void testEmptyOccurrence() throws IOException {
    prepareTopicMap();

    TopicIF topic = builder.makeTopic();
		LocatorIF loc = URILocator.create("http://www.ontopia.net");
		topic.addSubjectIdentifier(loc);
    TopicIF otype = builder.makeTopic();
    OccurrenceIF occ = builder.makeOccurrence(topic, otype, "");

    reload();

    topic = topicmap.getTopicBySubjectIdentifier(loc);

    Iterator it = topic.getOccurrences().iterator();
    Assert.assertTrue("empty occurrence lost on export and re-import",
           it.hasNext());
    occ = (OccurrenceIF) it.next();
    Assert.assertTrue("empty occurrence has '" + occ.getValue() +
           "' instead of empty string on re-import",
           occ.getValue() != null && occ.getValue().equals(""));
  }

  @Test
  public void testNullOccurrence() throws IOException {
    prepareTopicMap();

    TopicIF topic = builder.makeTopic();
		LocatorIF psi = URILocator.create("test:1");
		topic.addSubjectIdentifier(psi);
    TopicIF otype = builder.makeTopic();
    OccurrenceIF occ = builder.makeOccurrence(topic, otype, "");
    reload();

    topic = topicmap.getTopicBySubjectIdentifier(psi);

    Iterator it = topic.getOccurrences().iterator();
    Assert.assertTrue("null occurrence lost on export and re-import",
           it.hasNext());
    occ = (OccurrenceIF) it.next();
    Assert.assertTrue("null occurrence has '" + occ.getValue() +
           "' instead of empty string on re-import",
           occ.getValue() != null && occ.getValue().equals(""));
  }

  /// id collisions

  @Test
  public void testDuplicateIDs() throws IOException {
    // importing and exporting this file causes duplicate IDs
    // these are detected on re-import
    tmfile = TestFileUtils.getTestOutputFile(testdataDirectory, "out", "duplicate-ids.xtm");
    topicmap = load("various", "duplicate-ids.xtm");
    reload();
    
    Assert.assertEquals(4, topicmap.getTopics().size());
  }

  /// skipping ids

  @Test
  public void testOmittingIDs() throws IOException {
    prepareTopicMap();
    TopicIF topic = builder.makeTopic();
    TopicIF otype = builder.makeTopic();
    OccurrenceIF occ = builder.makeOccurrence(topic, otype, "huhei");
    
    XTMTopicMapWriter writer = new XTMTopicMapWriter(tmfile);
    writer.setAddIds(false);
    writer.write(topicmap);
    XTMTopicMapReader reader = new XTMTopicMapReader(tmfile);
    topicmap = reader.read();

    
    Iterator it = topicmap.getTopics().iterator();
    while (it.hasNext()) {
      topic = (TopicIF) it.next();
      if (!topic.getOccurrences().isEmpty()) {
        break;
      }
    }
    occ = (OccurrenceIF) topic.getOccurrences().iterator().next();
    Assert.assertTrue("occurrence had ID!", occ.getItemIdentifiers().isEmpty());
  }

  @Test
  public void testOmittingIDs2() throws IOException {
    prepareTopicMap();
    TopicIF topic = builder.makeTopic();
    TopicIF otype = builder.makeTopic();
    OccurrenceIF occ = builder.makeOccurrence(topic, otype, "huhei");
    TopicIF topic2 = builder.makeTopic();
    occ.setReifier(topic2);

    XTMTopicMapWriter writer = new XTMTopicMapWriter(tmfile);
    writer.setAddIds(false);
    writer.write(topicmap);
    XTMTopicMapReader reader = new XTMTopicMapReader(tmfile);
    topicmap = reader.read();

    Iterator it = topicmap.getTopics().iterator();
    while (it.hasNext()) {
      topic = (TopicIF) it.next();
      if (!topic.getOccurrences().isEmpty()) {
        break;
      }
    }

    occ = (OccurrenceIF) topic.getOccurrences().iterator().next();
    topic2 = occ.getReifier();
    Assert.assertTrue("reification relationship was lost on export and reimport",
               topic2 != null);
  }

  // motivated by bug #1426
  @Test
  public void testOmittingIDs3() throws IOException {
    prepareTopicMap();
    
    sourceLoc = tmbase.resolveAbsolute("#--reified--id");
    topicmap.addItemIdentifier(sourceLoc);
    
    reload(); // screwup most likely causes crash here
    Assert.assertTrue("topic map retained syntactically invalid id",
               !topicmap.getItemIdentifiers().contains(sourceLoc));
  }

  @Test
  public void testOmittingIDsPreserveReification() throws IOException {
    prepareTopicMap();
    
    TopicIF reifier = builder.makeTopic();
    topicmap.setReifier(reifier);
    
    reload();

    // now for the real test
		reifier = topicmap.getReifier();
    Assert.assertTrue("reification connection broken on export", reifier != null);
  }
  
  /// exporting invalid structures

  @Test
  public void testEmptyAssociation() throws IOException {
    prepareTopicMap();
    
    builder.makeAssociation(builder.makeTopic());
    reload(true); // validation will make this fail if bug #1024 is present
    
    Assert.assertEquals(0, topicmap.getAssociations().size());
  }

  @Test
  public void testBug654OnRDBMS() throws IOException {    
    // this test verifies that not are source locators of the form
    // id34234 not used to form IDs on export, but neither are those
    // of the form idT34234
    prepareTopicMap();

    String tid = "idT234212";
    TopicIF topic = builder.makeTopic();
    topic.addItemIdentifier(tmbase.resolveAbsolute("#" + tid));
    XTMTopicMapExporter exp = new XTMTopicMapExporter();
    String id = exp.getElementId(topic);
    Assert.assertTrue("unacceptable ID used", !id.equals(tid));
  }
  
  @Test
  public void testWriteToFile() throws IOException {
    prepareTopicMap();
    tmfile = TestFileUtils.getTestOutputFile("xtm", "io-f.xtm");
    new XTMTopicMapWriter(tmfile).write(topicmap);
    Assert.assertTrue(Files.size(tmfile.toPath()) > 0);
  }

  @Test
  public void testWriteToOutputStream() throws IOException {
    prepareTopicMap();
    tmfile = TestFileUtils.getTestOutputFile("xtm", "io-o.xtm");
    new XTMTopicMapWriter(new FileOutputStream(tmfile), "utf-8").write(topicmap);
    Assert.assertTrue(Files.size(tmfile.toPath()) > 0);
  }

  @Test
  public void testWriteToWriter() throws IOException {
    prepareTopicMap();
    tmfile = TestFileUtils.getTestOutputFile("xtm", "io-w.xtm");
    new XTMTopicMapWriter(new FileWriter(tmfile), "utf-8").write(topicmap);
    Assert.assertTrue(Files.size(tmfile.toPath()) > 0);
  }
  
  // --- Internal helper methods

  private void reload() throws IOException {
    reload(false);
  }

  private void reload(boolean validate) throws IOException {
    export();
    XTMTopicMapReader reader = new XTMTopicMapReader(tmfile);
    reader.setValidation(validate);
    topicmap = reader.read();
  }  

  private void assertXTM(String what, TMObjectIF obj) {
    assertXTM(what, obj, sourceLoc);
  }

  private void assertXTM(String what, TMObjectIF obj, LocatorIF srcloc) {
    Iterator it = obj.getItemIdentifiers().iterator();
    Assert.assertTrue(what + " id lost on export and re-import",
               it.hasNext());
    Assert.assertTrue(what + " source locator corrupted",
               it.next().equals(srcloc));
  }

  private TopicMapIF load(String dir, String subdir, String file) throws IOException {
    return load(dir + "/" + subdir, file);
  }
  private TopicMapIF load(String dir, String file) throws IOException {
    return new XTMTopicMapReader(TestFileUtils.getTestInputURL(TestFileUtils.getTestInputFile(dir, file))).read();
  }
}
