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

package net.ontopia.infoset.fulltext.topicmaps;

import java.util.Collection;
import java.util.Collections;
import net.ontopia.infoset.fulltext.core.DocumentIF;
import net.ontopia.infoset.fulltext.core.FieldIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DefaultTMDocGeneratorTest {
  protected TopicMapIF topicmap;       // topic map of object being tested
  protected TopicMapBuilderIF builder; // builder used for creating new objects
  protected DefaultTopicMapDocumentGenerator generator;
  
  @Before
  public void setUp() {
    TopicMapStoreIF store = new InMemoryTopicMapStore();
    topicmap = store.getTopicMap();
    builder = topicmap.getBuilder();
    generator = DefaultTopicMapDocumentGenerator.INSTANCE;
  }

  // --- test cases

  @Test
  public void testAssocGenerate() {
    AssociationIF assoc = builder.makeAssociation(builder.makeTopic());
    DocumentIF doc = generator.generate(assoc);

    Collection<FieldIF> fields = doc.getFields();
    Assert.assertTrue("Association DocumentIF has " + fields.size() + " fields",
	   fields.size() == 2);

    verifyObjectId(assoc, doc.getField("object_id"));
    verifyClass(doc.getField("class"), "A");
  }

  @Test
  public void testAssocRoleGenerate() {
    AssociationIF assoc = builder.makeAssociation(builder.makeTopic());
    AssociationRoleIF role = builder.makeAssociationRole(assoc, builder.makeTopic(), builder.makeTopic());
    DocumentIF doc = generator.generate(role);

    Collection<FieldIF> fields = doc.getFields();
    Assert.assertTrue("Association role DocumentIF has " + fields.size() + " fields",
	   fields.size() == 2);

    verifyObjectId(role, doc.getField("object_id"));
    verifyClass(doc.getField("class"), "R");
  }

  @Test
  public void testTopicNameGenerate() {
    TopicIF topic = builder.makeTopic();
    TopicNameIF bn = builder.makeTopicName(topic, "foo");
    DocumentIF doc = generator.generate(bn);

    Collection<FieldIF> fields = doc.getFields();
    Assert.assertTrue("Base name DocumentIF has " + fields.size() + " fields",
	   fields.size() == 3);

    verifyObjectId(bn, doc.getField("object_id"));
    verifyClass(doc.getField("class"), "B");
    verifyContent(doc.getField("content"), "foo");
  }

  @Test
  public void testOccurrenceGenerate() {
    TopicIF topic = builder.makeTopic();
    TopicIF otype = builder.makeTopic();
    OccurrenceIF occ = builder.makeOccurrence(topic, otype, "value");
    DocumentIF doc = generator.generate(occ);

    Collection<FieldIF> fields = doc.getFields();
    Assert.assertTrue("Occurrence DocumentIF has " + fields.size() + " fields",
	   fields.size() == 3);

    verifyObjectId(occ, doc.getField("object_id"));
    verifyClass(doc.getField("class"), "O");
    verifyContent(doc.getField("content"), "value");
  }
  
  @Test
  public void testOccurrenceGenerate2() {
    TopicIF topic = builder.makeTopic();
    TopicIF otype = builder.makeTopic();
    OccurrenceIF occ = builder.makeOccurrence(topic, otype, URILocator.create("http://www.ontopia.net"));
    DocumentIF doc = generator.generate(occ);

    Collection<FieldIF> fields = doc.getFields();
    Assert.assertTrue("Occurrence DocumentIF has " + fields.size() + " fields",
	   fields.size() == 4);

    verifyObjectId(occ, doc.getField("object_id"));
    verifyClass(doc.getField("class"), "O");
    verifyLocator(doc, "http://www.ontopia.net");
  }
  
  @Test
  public void testTopicGenerate() {
    TopicIF topic = builder.makeTopic();
    DocumentIF doc = generator.generate(topic);

    Collection<FieldIF> fields = doc.getFields();
    Assert.assertTrue("Topic DocumentIF has " + fields.size() + " fields",
	   fields.size() == 2);

    verifyObjectId(topic, doc.getField("object_id"));
    verifyClass(doc.getField("class"), "T");
  }

  @Test
  public void testTopicMapGenerate() {
    DocumentIF doc = generator.generate(topicmap);

    Collection<FieldIF> fields = doc.getFields();
    Assert.assertTrue("Topic DocumentIF has " + fields.size() + " fields",
	   fields.size() == 2);

    verifyObjectId(topicmap, doc.getField("object_id"));
    verifyClass(doc.getField("class"), "M");
  }

  @Test
  public void testVariantGenerate() {
    TopicIF topic = builder.makeTopic();
    TopicNameIF bn = builder.makeTopicName(topic, "foo");
    VariantNameIF vn = builder.makeVariantName(bn, "value", Collections.emptySet());
    DocumentIF doc = generator.generate(vn);

    Collection<FieldIF> fields = doc.getFields();
    Assert.assertTrue("Variant DocumentIF has " + fields.size() + " fields",
	   fields.size() == 3);

    verifyObjectId(vn, doc.getField("object_id"));
    verifyClass(doc.getField("class"), "N");
    verifyContent(doc.getField("content"), "value");
  }
  
  @Test
  public void testVariantGenerate2() {
    TopicIF topic = builder.makeTopic();
    TopicNameIF bn = builder.makeTopicName(topic, "foo");
    VariantNameIF vn = builder.makeVariantName(bn, URILocator.create("http://www.ontopia.no"), Collections.emptySet());
    DocumentIF doc = generator.generate(vn);

    Collection<FieldIF> fields = doc.getFields();
    Assert.assertTrue("Variant DocumentIF has " + fields.size() + " fields",
	   fields.size() == 4);

    verifyObjectId(vn, doc.getField("object_id"));
    verifyClass(doc.getField("class"), "N");
    verifyLocator(doc, "http://www.ontopia.no");
  }
  
  // --- helper methods

  private void verifyObjectId(TMObjectIF object, FieldIF field) {
    Assert.assertTrue("object_id had value " + field.getValue() + " instead of " +
	   object.getObjectId(),
	   field.getValue() != null &&
	   field.getValue().equals(object.getObjectId()));
  }

  private void verifyClass(FieldIF field, String _class) {
    Assert.assertTrue("class was " + field.getValue() + " instead of " + _class,
	   field.getValue() != null && field.getValue().equals(_class));
  }

  private void verifyContent(FieldIF field, String content) {
    Assert.assertTrue("content should have been " + content + " but was " + field.getValue(),
	   field.getValue() != null && field.getValue().equals(content));
  }

  private void verifyLocator(DocumentIF doc, String locator) {
    FieldIF field = doc.getField("notation");
    Assert.assertTrue("notation " + field.getValue() + " instead of URI",
	   field.getValue() != null && field.getValue().equals("URI"));

    field = doc.getField("address");
    Assert.assertTrue("address was " + field.getValue(),
	   field.getValue() != null && field.getValue().equals(locator));
  }
}
