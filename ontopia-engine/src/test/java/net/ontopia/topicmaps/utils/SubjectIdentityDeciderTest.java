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

package net.ontopia.topicmaps.utils;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SubjectIdentityDeciderTest {
  protected TopicMapIF        topicmap; 
  protected TopicMapBuilderIF builder;

  @Before
  public void setUp() {
    topicmap = makeTopicMap();
    TopicIF a = makeTopic("A");
    TopicIF b = makeTopic("B");
    a.addType(makeTopic("C"));
    b.addType(makeTopic("D"));
  }

  // --- Tests

  @Test
  public void testSubjectIdentifiers() {
    LocatorIF locA = makeLocator("A");
    makeLocator("B");
    TopicIF topicA = getTopic("A");
    TopicIF topicB = getTopic("B");

    SubjectIdentityDecider decider = new SubjectIdentityDecider(locA);
    Assert.assertTrue("Decider did not recognize topic",
               decider.test(topicA));
               
    Assert.assertTrue("Decider ok-d topic with wrong identifier",
               !decider.test(topicB));
  }

  @Test
  public void testSubjectAddresses() {
    LocatorIF locA = makeLocator("A");
    TopicIF topic = builder.makeTopic();
    topic.addSubjectLocator(locA);

    SubjectIdentityDecider decider = new SubjectIdentityDecider(locA);
    Assert.assertTrue("Decider recognized topic which had locator as subject address",
               !decider.test(topic));
  }

  @Test
  public void testInstances() {
    LocatorIF locC = makeLocator("C");
    makeLocator("D");
    TopicIF topicA = getTopic("A");
    TopicIF topicB = getTopic("B");

    SubjectIdentityDecider decider = new SubjectIdentityDecider(locC);
    Assert.assertTrue("Decider did not recognize instance of topic",
               decider.test(topicA));
               
    Assert.assertTrue("Decider ok-d topic which was instance of topic with wrong identifier",
               !decider.test(topicB));
  }

  @Test
  public void testNoType() {
    LocatorIF locD = makeLocator("D");
		TopicIF otype = builder.makeTopic();
    OccurrenceIF occC = builder.makeOccurrence(getTopic("C"), otype, locD);

    SubjectIdentityDecider decider = new SubjectIdentityDecider(locD);
    Assert.assertTrue("Decider recognized occurrence it shouldn't recognize",
               !decider.test(occC));
  }
  
  // --- Internal helpers
  
  protected TopicMapIF makeTopicMap() {
    InMemoryTopicMapStore store = new InMemoryTopicMapStore();
    builder = store.getTopicMap().getBuilder();
    return store.getTopicMap();
  }

  protected TopicIF makeTopic(String name) {
    TopicIF topic = builder.makeTopic();
    topic.addSubjectIdentifier(makeLocator(name));
    return topic;
  }
  
  protected TopicIF getTopic(String name) {
    return topicmap.getTopicBySubjectIdentifier(makeLocator(name));
  }
  
  public URILocator makeLocator(String uri) {
    return URILocator.create("http://psi.ontopia.net/fake/" + uri);
  }
  
}
