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

package net.ontopia.topicmaps.core.index;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicIF;

public abstract class StatisticsIndexTest extends AbstractIndexTest {

  protected StatisticsIndexIF stats;

  public StatisticsIndexTest(String name) {
    super(name);
  }

  // ---------------------------------------------------------------------------
  // AbstractTopicMapTest
  // ---------------------------------------------------------------------------

  @Override
  protected void setUp() throws Exception {
    stats = (StatisticsIndexIF) super.setUp("StatisticsIndexIF");
  }

  // ---------------------------------------------------------------------------
  // Topic stats
  // ---------------------------------------------------------------------------

  public void testGetTopicCount() {
    assertEquals(0, stats.getTopicCount());
    builder.makeTopic();
    assertEquals(1, stats.getTopicCount());
  }

  public void testGetTypedTopicCount() {
    assertEquals(0, stats.getTypedTopicCount());
    builder.makeTopic();
    assertEquals(0, stats.getTypedTopicCount());
    builder.makeTopic(builder.makeTopic());
    assertEquals(1, stats.getTypedTopicCount());
  }

  public void testGetUntypedTopicCount() {
    assertEquals(0, stats.getUntypedTopicCount());
    builder.makeTopic();
    assertEquals(1, stats.getUntypedTopicCount());
    builder.makeTopic(builder.makeTopic());
    assertEquals(2, stats.getUntypedTopicCount());
  }

  public void testGetTopicTypeCount() {
    assertEquals(0, stats.getTopicTypeCount());
    TopicIF t1 = builder.makeTopic();
    TopicIF t2 = builder.makeTopic();

    Collection<TopicIF> tts = Arrays.asList(t1, t2);

    builder.makeTopic(tts);
    builder.makeTopic(tts);
    assertEquals(2, stats.getTopicTypeCount());
  }

  // ---------------------------------------------------------------------------
  // TopicName stats
  // ---------------------------------------------------------------------------

  public void testGetTopicNameCount() {
    assertEquals(0, stats.getTopicNameCount());
    builder.makeTopicName(builder.makeTopic(), "foo");
    assertEquals(1, stats.getTopicNameCount());
  }

  public void testGetNoNameTopicCount() {
    assertEquals(0, stats.getNoNameTopicCount());
    builder.makeTopicName(builder.makeTopic(), "foo");
    assertEquals(1, stats.getNoNameTopicCount());
  }

  public void testGetTopicNameTypeCount() {
    assertEquals(0, stats.getTopicNameTypeCount());
    builder.makeTopicName(builder.makeTopic(), "foo");
    assertEquals(1, stats.getTopicNameTypeCount());
    builder.makeTopicName(builder.makeTopic(), builder.makeTopic(), "foo");
    assertEquals(2, stats.getTopicNameTypeCount());
  }

  // ---------------------------------------------------------------------------
  // VariantName stats
  // ---------------------------------------------------------------------------

  public void testGetVariantCount() {
    assertEquals(0, stats.getVariantCount());
    List<TopicIF> emptyList = Collections.emptyList();
    builder.makeVariantName(builder.makeTopicName(builder.makeTopic(), "foo"),
            "bar", emptyList);
    assertEquals(1, stats.getVariantCount());
  }

  // ---------------------------------------------------------------------------
  // Occurrence stats
  // ---------------------------------------------------------------------------

  public void testGetOccurrenceCount() {
    assertEquals(0, stats.getOccurrenceCount());
    builder.makeOccurrence(builder.makeTopic(), builder.makeTopic(), "foo");
    assertEquals(1, stats.getOccurrenceCount());
  }

  public void testGetOccurrenceTypeCount() {
    assertEquals(0, stats.getOccurrenceTypeCount());
    builder.makeOccurrence(builder.makeTopic(), builder.makeTopic(), "foo");
    assertEquals(1, stats.getOccurrenceTypeCount());
  }

  // ---------------------------------------------------------------------------
  // Association stats
  // ---------------------------------------------------------------------------

  public void testGetAssociationCount() {
    assertEquals(0, stats.getAssociationCount());
    builder.makeAssociation(builder.makeTopic());
    assertEquals(1, stats.getAssociationCount());
  }

  public void testGetAssociationTypeCount() {
    assertEquals(0, stats.getAssociationTypeCount());
    builder.makeAssociation(builder.makeTopic());
    assertEquals(1, stats.getAssociationTypeCount());
  }

  // ---------------------------------------------------------------------------
  // Association role stats
  // ---------------------------------------------------------------------------

  public void testGetRoleCount() {
    assertEquals(0, stats.getRoleCount());
    TopicIF t = builder.makeTopic();
    builder.makeAssociationRole(builder.makeAssociation(t), t, t);
    assertEquals(1, stats.getRoleCount());
  }

  public void testGetRoleTypeCount() {
    assertEquals(0, stats.getRoleTypeCount());
    TopicIF t = builder.makeTopic();
    builder.makeAssociationRole(builder.makeAssociation(t), t, t);
    assertEquals(1, stats.getRoleTypeCount());
  }

  // ---------------------------------------------------------------------------
  // Locator stats
  // ---------------------------------------------------------------------------

  public void testGetSubjectIdentifierCount() {
    assertEquals(0, stats.getSubjectIdentifierCount());
    builder.makeTopic().addSubjectIdentifier(URILocator.create("foo:bar"));
    assertEquals(1, stats.getSubjectIdentifierCount());
  }

  public void testGetSubjectLocatorCount() {
    assertEquals(0, stats.getSubjectLocatorCount());
    builder.makeTopic().addSubjectLocator(URILocator.create("foo:bar"));
    assertEquals(1, stats.getSubjectLocatorCount());
  }

  public void testGetItemIdentifierCount() {
    assertEquals(0, stats.getItemIdentifierCount());
    builder.makeTopic().addItemIdentifier(URILocator.create("foo:bar"));
    assertEquals(1, stats.getItemIdentifierCount());
  }

}
