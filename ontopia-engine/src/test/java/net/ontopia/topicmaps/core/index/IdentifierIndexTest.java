/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2017 The Ontopia Project
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

import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public abstract class IdentifierIndexTest extends AbstractIndexTest {

  private final URILocator loc1 = URILocator.create("foo:1");
  private final URILocator loc2 = URILocator.create("foo:2");
  private final URILocator loc3 = URILocator.create("foo:3");
  private final URILocator loc4 = URILocator.create("bar:3");

  private IdentifierIndexIF ix;

  @Override
  @Before
  public void setUp() throws Exception {
    ix = (IdentifierIndexIF) super.setUp("IdentifierIndexIF");
  }

  @Test
  public void testItemIdentifiers() {
    Assert.assertEquals(0, ix.getItemIdentifiers().size());
    
    TopicIF topic1 = builder.makeTopic();
    TopicIF topic2 = builder.makeTopic();
    
    topic1.addItemIdentifier(loc1);
    topic2.addItemIdentifier(loc2);
    Assert.assertEquals(2, ix.getItemIdentifiers().size());
    
    OccurrenceIF o = builder.makeOccurrence(topic1, topic1, "foo");
    o.addItemIdentifier(loc3);
    Assert.assertEquals(3, ix.getItemIdentifiers().size());
    
    Assert.assertTrue(ix.getItemIdentifiers().contains(loc1));
    Assert.assertTrue(ix.getItemIdentifiers().contains(loc2));
    Assert.assertTrue(ix.getItemIdentifiers().contains(loc3));
    
    o.remove();
    Assert.assertEquals(2, ix.getItemIdentifiers().size());
    
    topic1.remove();
    Assert.assertEquals(1, ix.getItemIdentifiers().size());
  }
  
  @Test
  public void testItemIdentifiersByPrefix() {
    Assert.assertEquals(0, ix.getItemIdentifiersByPrefix("foo").size());
    Assert.assertEquals(0, ix.getItemIdentifiersByPrefix("bar").size());
    
    TopicIF topic1 = builder.makeTopic();
    TopicIF topic2 = builder.makeTopic();
    
    topic1.addItemIdentifier(loc1);
    topic2.addItemIdentifier(loc2);
    Assert.assertEquals(2, ix.getItemIdentifiersByPrefix("foo").size());
    Assert.assertEquals(0, ix.getItemIdentifiersByPrefix("bar").size());
    
    OccurrenceIF o = builder.makeOccurrence(topic1, topic1, "foo");
    o.addItemIdentifier(loc3);
    Assert.assertEquals(3, ix.getItemIdentifiersByPrefix("foo").size());
    Assert.assertEquals(0, ix.getItemIdentifiersByPrefix("bar").size());
    
    Assert.assertTrue(ix.getItemIdentifiers().contains(loc1));
    Assert.assertTrue(ix.getItemIdentifiers().contains(loc2));
    Assert.assertTrue(ix.getItemIdentifiers().contains(loc3));
    
    o.remove();
    Assert.assertEquals(2, ix.getItemIdentifiersByPrefix("foo").size());
    Assert.assertEquals(0, ix.getItemIdentifiersByPrefix("bar").size());
    
    topic1.remove();
    Assert.assertEquals(1, ix.getItemIdentifiersByPrefix("foo").size());
    Assert.assertEquals(0, ix.getItemIdentifiersByPrefix("bar").size());
    
    topic1 = builder.makeTopic();
    topic1.addItemIdentifier(loc4);
    Assert.assertEquals(1, ix.getItemIdentifiersByPrefix("foo").size());
    Assert.assertEquals(1, ix.getItemIdentifiersByPrefix("bar").size());
  }
  
  @Test
  public void testSubjectIdentifiers() {
    Assert.assertEquals(0, ix.getSubjectIdentifiers().size());
    
    TopicIF topic1 = builder.makeTopic();
    TopicIF topic2 = builder.makeTopic();
    
    topic1.addSubjectIdentifier(loc1);
    topic2.addSubjectIdentifier(loc2);
    Assert.assertEquals(2, ix.getSubjectIdentifiers().size());
    
    
    Assert.assertTrue(ix.getSubjectIdentifiers().contains(loc1));
    Assert.assertTrue(ix.getSubjectIdentifiers().contains(loc2));
    
    topic1.getSubjectIdentifiers();


    topic1.remove();
    Assert.assertEquals(1, ix.getSubjectIdentifiers().size());
  }
  
  @Test
  public void testSubjectIdentifiersByPrefix() {
    Assert.assertEquals(0, ix.getSubjectIdentifiersByPrefix("foo").size());
    Assert.assertEquals(0, ix.getSubjectIdentifiersByPrefix("bar").size());
    
    TopicIF topic1 = builder.makeTopic();
    TopicIF topic2 = builder.makeTopic();
    
    topic1.addSubjectIdentifier(loc1);
    topic2.addSubjectIdentifier(loc2);
    Assert.assertEquals(2, ix.getSubjectIdentifiersByPrefix("foo").size());
    Assert.assertEquals(0, ix.getSubjectIdentifiersByPrefix("bar").size());
    
    Assert.assertTrue(ix.getSubjectIdentifiers().contains(loc1));
    Assert.assertTrue(ix.getSubjectIdentifiers().contains(loc2));
    
    topic1.remove();
    Assert.assertEquals(1, ix.getSubjectIdentifiersByPrefix("foo").size());
    Assert.assertEquals(0, ix.getSubjectIdentifiersByPrefix("bar").size());
    
    topic1 = builder.makeTopic();
    topic1.addSubjectIdentifier(loc4);
    Assert.assertEquals(1, ix.getSubjectIdentifiersByPrefix("foo").size());
    Assert.assertEquals(1, ix.getSubjectIdentifiersByPrefix("bar").size());
  }
}
