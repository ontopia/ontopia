
// $Id: SubjectIdentityDeciderTest.java,v 1.7 2008/06/13 08:36:29 geir.gronmo Exp $

package net.ontopia.topicmaps.utils.test;

import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.test.AbstractTopicMapTestCase;
import net.ontopia.topicmaps.utils.SubjectIdentityDecider;

public class SubjectIdentityDeciderTest extends AbstractTopicMapTestCase {
  protected TopicMapIF        topicmap; 
  protected TopicMapBuilderIF builder;

  public SubjectIdentityDeciderTest(String name) {
    super(name);
  }
    
  public void setUp() {
    topicmap = makeTopicMap();
    TopicIF a = makeTopic("A");
    TopicIF b = makeTopic("B");
    a.addType(makeTopic("C"));
    b.addType(makeTopic("D"));
  }

  // --- Tests

  public void testSubjectIdentifiers() {
    LocatorIF locA = makeLocator("A");
    LocatorIF locB = makeLocator("B");
    TopicIF topicA = getTopic("A");
    TopicIF topicB = getTopic("B");

    SubjectIdentityDecider decider = new SubjectIdentityDecider(locA);
    assertTrue("Decider did not recognize topic",
               decider.ok(topicA));
               
    assertTrue("Decider ok-d topic with wrong identifier",
               !decider.ok(topicB));
  }

  public void testSubjectAddresses() {
    LocatorIF locA = makeLocator("A");
    TopicIF topic = builder.makeTopic();
    topic.addSubjectLocator(locA);

    SubjectIdentityDecider decider = new SubjectIdentityDecider(locA);
    assertTrue("Decider recognized topic which had locator as subject address",
               !decider.ok(topic));
  }

  public void testInstances() {
    LocatorIF locC = makeLocator("C");
    LocatorIF locD = makeLocator("D");
    TopicIF topicA = getTopic("A");
    TopicIF topicB = getTopic("B");

    SubjectIdentityDecider decider = new SubjectIdentityDecider(locC);
    assertTrue("Decider did not recognize instance of topic",
               decider.ok(topicA));
               
    assertTrue("Decider ok-d topic which was instance of topic with wrong identifier",
               !decider.ok(topicB));
  }

  public void testNoType() {
    LocatorIF locD = makeLocator("D");
		TopicIF otype = builder.makeTopic();
    OccurrenceIF occC = builder.makeOccurrence(getTopic("C"), otype, locD);

    SubjectIdentityDecider decider = new SubjectIdentityDecider(locD);
    assertTrue("Decider recognized occurrence it shouldn't recognize",
               !decider.ok(occC));
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
    try {
      return new URILocator("http://psi.ontopia.net/fake/" + uri);
    }
    catch (java.net.MalformedURLException e) {
      fail("malformed URL given: " + e);
      return null; // never executed...
    }
  }
  
}
