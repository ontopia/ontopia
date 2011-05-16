// $Id: DefaultTMDocGeneratorTest.java,v 1.12 2009/05/18 12:54:01 geir.gronmo Exp $

package net.ontopia.infoset.fulltext.topicmaps;

import java.net.MalformedURLException;
import java.util.Collection;
import junit.framework.TestCase;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.fulltext.core.DocumentIF;
import net.ontopia.infoset.fulltext.core.FieldIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;

public class DefaultTMDocGeneratorTest extends TestCase {
  protected TopicMapIF topicmap;       // topic map of object being tested
  protected TopicMapBuilderIF builder; // builder used for creating new objects
  protected DefaultTopicMapDocumentGenerator generator;
  
  public DefaultTMDocGeneratorTest(String name) {
    super(name);
  }
  
  public void setUp() {
    TopicMapStoreIF store = new InMemoryTopicMapStore();
    topicmap = store.getTopicMap();
    builder = topicmap.getBuilder();
    generator = DefaultTopicMapDocumentGenerator.INSTANCE;
  }

  // --- test cases

  public void testAssocGenerate() {
    AssociationIF assoc = builder.makeAssociation(builder.makeTopic());
    DocumentIF doc = generator.generate(assoc);

    Collection fields = doc.getFields();
    assertTrue("Association DocumentIF has " + fields.size() + " fields",
	   fields.size() == 2);

    verifyObjectId(assoc, doc.getField("object_id"));
    verifyClass(doc.getField("class"), "A");
  }

  public void testAssocRoleGenerate() {
    AssociationIF assoc = builder.makeAssociation(builder.makeTopic());
    AssociationRoleIF role = builder.makeAssociationRole(assoc, builder.makeTopic(), builder.makeTopic());
    DocumentIF doc = generator.generate(role);

    Collection fields = doc.getFields();
    assertTrue("Association role DocumentIF has " + fields.size() + " fields",
	   fields.size() == 2);

    verifyObjectId(role, doc.getField("object_id"));
    verifyClass(doc.getField("class"), "R");
  }

  public void testTopicNameGenerate() {
    TopicIF topic = builder.makeTopic();
    TopicNameIF bn = builder.makeTopicName(topic, "foo");
    DocumentIF doc = generator.generate(bn);

    Collection fields = doc.getFields();
    assertTrue("Base name DocumentIF has " + fields.size() + " fields",
	   fields.size() == 3);

    verifyObjectId(bn, doc.getField("object_id"));
    verifyClass(doc.getField("class"), "B");
    verifyContent(doc.getField("content"), "foo");
  }

  public void testOccurrenceGenerate() {
    TopicIF topic = builder.makeTopic();
    TopicIF otype = builder.makeTopic();
    OccurrenceIF occ = builder.makeOccurrence(topic, otype, "value");
    DocumentIF doc = generator.generate(occ);

    Collection fields = doc.getFields();
    assertTrue("Occurrence DocumentIF has " + fields.size() + " fields",
	   fields.size() == 3);

    verifyObjectId(occ, doc.getField("object_id"));
    verifyClass(doc.getField("class"), "O");
    verifyContent(doc.getField("content"), "value");
  }
  
  public void testOccurrenceGenerate2() {
    TopicIF topic = builder.makeTopic();
    TopicIF otype = builder.makeTopic();
    OccurrenceIF occ = builder.makeOccurrence(topic, otype, makeLocator("http://www.ontopia.net"));
    DocumentIF doc = generator.generate(occ);

    Collection fields = doc.getFields();
    assertTrue("Occurrence DocumentIF has " + fields.size() + " fields",
	   fields.size() == 4);

    verifyObjectId(occ, doc.getField("object_id"));
    verifyClass(doc.getField("class"), "O");
    verifyLocator(doc, "http://www.ontopia.net/");
  }
  
  public void testTopicGenerate() {
    TopicIF topic = builder.makeTopic();
    DocumentIF doc = generator.generate(topic);

    Collection fields = doc.getFields();
    assertTrue("Topic DocumentIF has " + fields.size() + " fields",
	   fields.size() == 2);

    verifyObjectId(topic, doc.getField("object_id"));
    verifyClass(doc.getField("class"), "T");
  }

  public void testTopicMapGenerate() {
    DocumentIF doc = generator.generate(topicmap);

    Collection fields = doc.getFields();
    assertTrue("Topic DocumentIF has " + fields.size() + " fields",
	   fields.size() == 2);

    verifyObjectId(topicmap, doc.getField("object_id"));
    verifyClass(doc.getField("class"), "M");
  }

  public void testVariantGenerate() {
    TopicIF topic = builder.makeTopic();
    TopicNameIF bn = builder.makeTopicName(topic, "foo");
    VariantNameIF vn = builder.makeVariantName(bn, "value");
    DocumentIF doc = generator.generate(vn);

    Collection fields = doc.getFields();
    assertTrue("Variant DocumentIF has " + fields.size() + " fields",
	   fields.size() == 3);

    verifyObjectId(vn, doc.getField("object_id"));
    verifyClass(doc.getField("class"), "N");
    verifyContent(doc.getField("content"), "value");
  }
  
  public void testVariantGenerate2() {
    TopicIF topic = builder.makeTopic();
    TopicNameIF bn = builder.makeTopicName(topic, "foo");
    VariantNameIF vn = builder.makeVariantName(bn, makeLocator("http://www.ontopia.no"));
    DocumentIF doc = generator.generate(vn);

    Collection fields = doc.getFields();
    assertTrue("Variant DocumentIF has " + fields.size() + " fields",
	   fields.size() == 4);

    verifyObjectId(vn, doc.getField("object_id"));
    verifyClass(doc.getField("class"), "N");
    verifyLocator(doc, "http://www.ontopia.no/");
  }
  
  // --- helper methods

  private void verifyObjectId(TMObjectIF object, FieldIF field) {
    assertTrue("object_id had value " + field.getValue() + " instead of " +
	   object.getObjectId(),
	   field.getValue() != null &&
	   field.getValue().equals(object.getObjectId()));
  }

  private void verifyClass(FieldIF field, String _class) {
    assertTrue("class was " + field.getValue() + " instead of " + _class,
	   field.getValue() != null && field.getValue().equals(_class));
  }

  private void verifyContent(FieldIF field, String content) {
    assertTrue("content should have been " + content + " but was " + field.getValue(),
	   field.getValue() != null && field.getValue().equals(content));
  }

  private void verifyLocator(DocumentIF doc, String locator) {
    FieldIF field = doc.getField("notation");
    assertTrue("notation " + field.getValue() + " instead of URI",
	   field.getValue() != null && field.getValue().equals("URI"));

    field = doc.getField("address");
    assertTrue("address was " + field.getValue(),
	   field.getValue() != null && field.getValue().equals(locator));
  }


  private LocatorIF makeLocator(String uri) {
    try {
      return new URILocator(uri);
    }
    catch (MalformedURLException e) {
      fail("INTERNAL: URI was malformed");
      return null; // never returns
    }
  }
  
}






