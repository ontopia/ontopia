
// $Id: XTMExporterTest.java,v 1.36 2008/06/25 11:28:58 lars.garshol Exp $

package net.ontopia.topicmaps.xml.test;

import java.io.*;
import java.util.Iterator;
import junit.framework.*;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.infoset.impl.basic.GenericLocator;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.xml.*;

public class XTMExporterTest extends AbstractXMLTestCase {
  private TopicMapBuilderIF builder;
  private TopicMapIF topicmap;
  private LocatorIF sourceLoc;
  private LocatorIF tmbase;
  private File tmfile;
  
  public XTMExporterTest(String name) {
    super(name);
  }
    
  public void setUp() {
    String root = getTestDirectory();
    verifyDirectory(root, "canonical", "out");
  }

  // --- Test cases

  public void testEncoding() throws IOException {
    TopicMapIF tm = load("canonical" + File.separator + "in", "latin1.xtm");
    String out = resolveFileName("canonical" + File.separator + "out", "tmp-latin1.xtm");
    XTMTopicMapWriter writer = new XTMTopicMapWriter(new File(out), "iso-8859-1");
    writer.setVersion(1);
    writer.write(tm);
    TopicMapIF tm2 = new XTMTopicMapReader(new File(out)).read();
    TopicIF topic = (TopicIF) tm2.getTopics().iterator().next();
    TopicNameIF bn = (TopicNameIF) topic.getTopicNames().iterator().next();
    assertTrue("base name value did not survive encoding change roundtrip",
           bn.getValue().equals("B\u00E6 b\u00E6 lille lam, har du noe \u00F8l"));
  }

  public void testEncoding2() throws IOException {
    TopicMapIF tm = load("canonical" + File.separator + "in", "latin1.xtm");
    String out = resolveFileName("canonical" + File.separator + "out", "tmp-utf-8.xtm");
    XTMTopicMapWriter writer = new XTMTopicMapWriter(new File(out));
    writer.setVersion(1);
    writer.write(tm);
    TopicMapIF tm2 = new XTMTopicMapReader(new File(out)).read();
    TopicIF topic = (TopicIF) tm2.getTopics().iterator().next();
    TopicNameIF bn = (TopicNameIF) topic.getTopicNames().iterator().next();
    assertTrue("base name value did not survive encoding change roundtrip",
           bn.getValue().equals("B\u00E6 b\u00E6 lille lam, har du noe \u00F8l"));
  }

  /// id preservation
  
  public void testPreservesTopicmapID() throws IOException {
    prepareTopicMap();
    topicmap.addItemIdentifier(sourceLoc);
    reload();
    check("topic map", topicmap);
  }

  public void testPreservesTopicID() throws IOException {
    prepareTopicMap();

    TopicIF topic = builder.makeTopic();
    topic.addItemIdentifier(sourceLoc);
  
    reload();
    check("topic", getTopicById(topicmap, "id"));
  }

  public void testPreservesTopicID2() throws IOException {
    prepareTopicMap();

    LocatorIF loc = tmbase.resolveAbsolute("#ide");
    TopicIF topic = builder.makeTopic();
    topic.addItemIdentifier(loc);
  
    reload();
    check("topic", getTopicById(topicmap, "ide"), loc);
  }

  public void testPreservesBasenameID() throws IOException {
    prepareTopicMap();

    TopicIF topic = builder.makeTopic();
    TopicNameIF bn = builder.makeTopicName(topic, "bongomonog");
    bn.addItemIdentifier(sourceLoc);
  
    reload();
    topic = (TopicIF) topicmap.getTopics().iterator().next();
    check("base name", (TopicNameIF) topic.getTopicNames().iterator().next());
  }

  public void testPreservesVariantnameID() throws IOException {
    prepareTopicMap();

    TopicIF topic = builder.makeTopic();
    TopicNameIF bn = builder.makeTopicName(topic, "bongomonog");
    VariantNameIF vn = builder.makeVariantName(bn, "bongomonog");
    vn.addItemIdentifier(sourceLoc);
  
    reload();
    topic = (TopicIF) topicmap.getTopics().iterator().next();
    bn = (TopicNameIF) topic.getTopicNames().iterator().next();
    check("variant name", (VariantNameIF) bn.getVariants().iterator().next());
  }

  public void testPreservesOccurrenceID() throws IOException {
    prepareTopicMap();

    TopicIF topic = builder.makeTopic();
		LocatorIF loc = new URILocator("http://www.ontopia.net");
		topic.addSubjectIdentifier(loc);
    TopicIF otype = builder.makeTopic();
    OccurrenceIF occ = builder.makeOccurrence(topic, otype, loc);
    occ.addItemIdentifier(sourceLoc);
  
    reload();
    topic = topicmap.getTopicBySubjectIdentifier(loc);
    check("occurrence", (OccurrenceIF) topic.getOccurrences().iterator().next());
  }

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

  public void testEmptyTopicName() throws IOException {
    prepareTopicMap();

    TopicIF topic = builder.makeTopic();
    TopicNameIF bn = builder.makeTopicName(topic, "");

    reload();

    topic = (TopicIF) topicmap.getTopics().iterator().next();

    Iterator it = topic.getTopicNames().iterator();
    assertTrue("empty base name lost on export and re-import",
           it.hasNext());
    bn = (TopicNameIF) it.next();
    assertTrue("empty base name has '" + bn.getValue() +
           "' instead of empty string on re-import",
           bn.getValue() != null && bn.getValue().equals(""));
  }

  public void testEmptyVariantName() throws IOException {
    prepareTopicMap();

    TopicIF topic = builder.makeTopic();
    TopicNameIF bn = builder.makeTopicName(topic, "empty");
    VariantNameIF vn = builder.makeVariantName(bn, "");

    reload();

    topic = (TopicIF) topicmap.getTopics().iterator().next();
    bn = (TopicNameIF) topic.getTopicNames().iterator().next();
    
    Iterator it = bn.getVariants().iterator();
    assertTrue("empty variant name lost on export and re-import",
           it.hasNext());
    vn = (VariantNameIF) it.next();
    assertTrue("empty variant name has '" + vn.getValue() +
           "' instead of empty string on re-import",
           vn.getValue() != null && vn.getValue().equals(""));
  }
  
  public void testEmptyOccurrence() throws IOException {
    prepareTopicMap();

    TopicIF topic = builder.makeTopic();
		LocatorIF loc = new URILocator("http://www.ontopia.net");
		topic.addSubjectIdentifier(loc);
    TopicIF otype = builder.makeTopic();
    OccurrenceIF occ = builder.makeOccurrence(topic, otype, "");

    reload();

    topic = topicmap.getTopicBySubjectIdentifier(loc);

    Iterator it = topic.getOccurrences().iterator();
    assertTrue("empty occurrence lost on export and re-import",
           it.hasNext());
    occ = (OccurrenceIF) it.next();
    assertTrue("empty occurrence has '" + occ.getValue() +
           "' instead of empty string on re-import",
           occ.getValue() != null && occ.getValue().equals(""));
  }

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
    assertTrue("null occurrence lost on export and re-import",
           it.hasNext());
    occ = (OccurrenceIF) it.next();
    assertTrue("null occurrence has '" + occ.getValue() +
           "' instead of empty string on re-import",
           occ.getValue() != null && occ.getValue().equals(""));
  }

  /// id collisions

  public void testDuplicateIDs() throws IOException {
    // importing and exporting this file causes duplicate IDs
    // these are detected on re-import
    tmfile = new File(resolveFileName("canonical" + File.separator + "out", "duplicate-ids.xtm"));
    topicmap = load("various", "duplicate-ids.xtm");
    reload();
  }

  /// skipping ids

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
      if (!topic.getOccurrences().isEmpty())
        break;
    }
    occ = (OccurrenceIF) topic.getOccurrences().iterator().next();
    assertTrue("occurrence had ID!", occ.getItemIdentifiers().isEmpty());
  }

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
      if (!topic.getOccurrences().isEmpty())
        break;
    }

    occ = (OccurrenceIF) topic.getOccurrences().iterator().next();
    topic2 = occ.getReifier();
    assertTrue("reification relationship was lost on export and reimport",
               topic2 != null);
  }

  // motivated by bug #1426
  public void testOmittingIDs3() throws IOException {
    prepareTopicMap();
    
    sourceLoc = tmbase.resolveAbsolute("#--reified--id");
    topicmap.addItemIdentifier(sourceLoc);
    
    reload(); // screwup most likely causes crash here
    assertTrue("topic map retained syntactically invalid id",
               !topicmap.getItemIdentifiers().contains(sourceLoc));
  }

  public void testOmittingIDsPreserveReification() throws IOException {
    prepareTopicMap();
    
    TopicIF reifier = builder.makeTopic();
    topicmap.setReifier(reifier);
    
    reload();

    // now for the real test
		reifier = topicmap.getReifier();
    assertTrue("reification connection broken on export", reifier != null);
  }
  
  /// exporting invalid structures

  public void testEmptyAssociation() throws IOException {
    prepareTopicMap();
    
    AssociationIF assoc = builder.makeAssociation(builder.makeTopic());
    reload(true); // validation will make this fail if bug #1024 is present
  }

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
    assertTrue("unacceptable ID used", !id.equals(tid));
  }
  
  // --- Internal helper methods

  private void prepareTopicMap() throws IOException {
    tmfile = new File(resolveFileName("canonical" + File.separator + "out", "tmid.xtm"));
    tmbase = new URILocator(tmfile.toURL());
    sourceLoc = tmbase.resolveAbsolute("#id");
    
    InMemoryTopicMapStore store = new InMemoryTopicMapStore();
    store.setBaseAddress(tmbase);
    topicmap = store.getTopicMap();
    builder = topicmap.getBuilder();
  }

  private void reload() throws IOException {
    reload(false);
  }
  
  private void reload(boolean validate) throws IOException { 
    XTMTopicMapWriter writer = new XTMTopicMapWriter(tmfile);
    writer.setVersion(1);
    writer.setAddIds(true);
    writer.write(topicmap);
    XTMTopicMapReader reader = new XTMTopicMapReader(tmfile);
    reader.setValidation(validate);
    topicmap = reader.read();
  }  

  private void check(String what, TMObjectIF obj) {
    check(what, obj, sourceLoc);
  }

  private void check(String what, TMObjectIF obj, LocatorIF srcloc) {
    Iterator it = obj.getItemIdentifiers().iterator();
    assertTrue(what + " id lost on export and re-import",
               it.hasNext());
    assertTrue(what + " source locator corrupted",
               it.next().equals(srcloc));
  }

  private TopicMapIF load(String dir, String file) throws IOException {
    return new XTMTopicMapReader(new File(resolveFileName(dir, file))).read();
  }
}
