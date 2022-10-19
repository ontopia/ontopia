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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public abstract class StatisticsIndexTest extends AbstractIndexTest {

  protected StatisticsIndexIF stats;

  // ---------------------------------------------------------------------------
  // AbstractTopicMapTest
  // ---------------------------------------------------------------------------

  @Override
  @Before
  public void setUp() throws Exception {
    stats = (StatisticsIndexIF) super.setUp("StatisticsIndexIF");
  }

  // ---------------------------------------------------------------------------
  // Topic stats
  // ---------------------------------------------------------------------------

  @Test
  public void testGetTopicCount() {
    Assert.assertEquals(0, stats.getTopicCount());
    builder.makeTopic();
    Assert.assertEquals(1, stats.getTopicCount());
  }

  @Test
  public void testGetTypedTopicCount() {
    Assert.assertEquals(0, stats.getTypedTopicCount());
    builder.makeTopic();
    Assert.assertEquals(0, stats.getTypedTopicCount());
    builder.makeTopic(builder.makeTopic());
    Assert.assertEquals(1, stats.getTypedTopicCount());
  }

  @Test
  public void testGetUntypedTopicCount() {
    Assert.assertEquals(0, stats.getUntypedTopicCount());
    builder.makeTopic();
    Assert.assertEquals(1, stats.getUntypedTopicCount());
    builder.makeTopic(builder.makeTopic());
    Assert.assertEquals(2, stats.getUntypedTopicCount());
  }

  @Test
  public void testGetTopicTypeCount() {
    Assert.assertEquals(0, stats.getTopicTypeCount());
    TopicIF t1 = builder.makeTopic();
    TopicIF t2 = builder.makeTopic();

    Collection<TopicIF> tts = Arrays.asList(t1, t2);

    builder.makeTopic(tts);
    builder.makeTopic(tts);
    Assert.assertEquals(2, stats.getTopicTypeCount());
  }

  // ---------------------------------------------------------------------------
  // TopicName stats
  // ---------------------------------------------------------------------------

  @Test
  public void testGetTopicNameCount() {
    Assert.assertEquals(0, stats.getTopicNameCount());
    builder.makeTopicName(builder.makeTopic(), "foo");
    Assert.assertEquals(1, stats.getTopicNameCount());
  }

  @Test
  public void testGetNoNameTopicCount() {
    Assert.assertEquals(0, stats.getNoNameTopicCount());
    builder.makeTopicName(builder.makeTopic(), "foo");
    Assert.assertEquals(1, stats.getNoNameTopicCount());
  }

  @Test
  public void testGetTopicNameTypeCount() {
    Assert.assertEquals(0, stats.getTopicNameTypeCount());
    builder.makeTopicName(builder.makeTopic(), "foo");
    Assert.assertEquals(1, stats.getTopicNameTypeCount());
    builder.makeTopicName(builder.makeTopic(), builder.makeTopic(), "foo");
    Assert.assertEquals(2, stats.getTopicNameTypeCount());
  }

  // ---------------------------------------------------------------------------
  // VariantName stats
  // ---------------------------------------------------------------------------

  @Test
  public void testGetVariantCount() {
    Assert.assertEquals(0, stats.getVariantCount());
    List<TopicIF> emptyList = Collections.emptyList();
    builder.makeVariantName(builder.makeTopicName(builder.makeTopic(), "foo"),
            "bar", emptyList);
    Assert.assertEquals(1, stats.getVariantCount());
  }

  // ---------------------------------------------------------------------------
  // Occurrence stats
  // ---------------------------------------------------------------------------

  @Test
  public void testGetOccurrenceCount() {
    Assert.assertEquals(0, stats.getOccurrenceCount());
    builder.makeOccurrence(builder.makeTopic(), builder.makeTopic(), "foo");
    Assert.assertEquals(1, stats.getOccurrenceCount());
  }

  @Test
  public void testGetOccurrenceTypeCount() {
    Assert.assertEquals(0, stats.getOccurrenceTypeCount());
    builder.makeOccurrence(builder.makeTopic(), builder.makeTopic(), "foo");
    Assert.assertEquals(1, stats.getOccurrenceTypeCount());
  }

  // ---------------------------------------------------------------------------
  // Association stats
  // ---------------------------------------------------------------------------

  @Test
  public void testGetAssociationCount() {
    Assert.assertEquals(0, stats.getAssociationCount());
    builder.makeAssociation(builder.makeTopic());
    Assert.assertEquals(1, stats.getAssociationCount());
  }

  @Test
  public void testGetAssociationTypeCount() {
    Assert.assertEquals(0, stats.getAssociationTypeCount());
    builder.makeAssociation(builder.makeTopic());
    Assert.assertEquals(1, stats.getAssociationTypeCount());
  }

  // ---------------------------------------------------------------------------
  // Association role stats
  // ---------------------------------------------------------------------------

  @Test
  public void testGetRoleCount() {
    Assert.assertEquals(0, stats.getRoleCount());
    TopicIF t = builder.makeTopic();
    builder.makeAssociationRole(builder.makeAssociation(t), t, t);
    Assert.assertEquals(1, stats.getRoleCount());
  }

  @Test
  public void testGetRoleTypeCount() {
    Assert.assertEquals(0, stats.getRoleTypeCount());
    TopicIF t = builder.makeTopic();
    builder.makeAssociationRole(builder.makeAssociation(t), t, t);
    Assert.assertEquals(1, stats.getRoleTypeCount());
  }

  // ---------------------------------------------------------------------------
  // Locator stats
  // ---------------------------------------------------------------------------

  @Test
  public void testGetSubjectIdentifierCount() {
    Assert.assertEquals(0, stats.getSubjectIdentifierCount());
    builder.makeTopic().addSubjectIdentifier(URILocator.create("foo:bar"));
    Assert.assertEquals(1, stats.getSubjectIdentifierCount());
  }

  @Test
  public void testGetSubjectLocatorCount() {
    Assert.assertEquals(0, stats.getSubjectLocatorCount());
    builder.makeTopic().addSubjectLocator(URILocator.create("foo:bar"));
    Assert.assertEquals(1, stats.getSubjectLocatorCount());
  }

  @Test
  public void testGetItemIdentifierCount() {
    Assert.assertEquals(0, stats.getItemIdentifierCount());
    builder.makeTopic().addItemIdentifier(URILocator.create("foo:bar"));
    Assert.assertEquals(1, stats.getItemIdentifierCount());
  }

}
