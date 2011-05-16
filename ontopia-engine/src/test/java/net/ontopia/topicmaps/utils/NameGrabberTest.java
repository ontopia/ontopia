// $Id: NameGrabberTest.java,v 1.12 2008/06/13 08:17:55 geir.gronmo Exp $

package net.ontopia.topicmaps.utils;

import java.net.MalformedURLException;
import java.util.*;
import junit.framework.TestCase;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.core.*;
import net.ontopia.utils.GrabberIF;

public class NameGrabberTest extends TestCase {

  InMemoryTopicMapStore store;
  TopicMapBuilderIF builder;
  TopicMapIF topicmap; 
  TopicIF topicSort;
  TopicIF topicPlay;
  TopicIF topicWriter;
  TopicIF topic1;
  TopicNameIF basename1;
  VariantNameIF variant1A;
  TopicIF topic2;
  TopicNameIF basename2;
  VariantNameIF variant2A;
  TopicIF topic3;
  TopicNameIF basename3;
  VariantNameIF variant3A;
  TopicIF topic4;
  TopicNameIF basename4;
  VariantNameIF variant4A;
  TopicIF topic5;
  TopicNameIF basename5A, basename5B, basename5C;
  VariantNameIF variant5A, variant5B, variant5C;
  
  public NameGrabberTest(String name) {
    super(name);
  }
  
  public void setUp() throws MalformedURLException {
    store = new InMemoryTopicMapStore();
    builder = store.getTopicMap().getBuilder();
    
    topicmap = makeTopicMap(); 

    topicSort = builder.makeTopic();
    LocatorIF sortRef = new URILocator("http://www.topicmaps.org/xtm/1.0/core.xtm#sort");
    topicSort.addSubjectIdentifier(sortRef);
    topicPlay = builder.makeTopic();
    topicWriter = builder.makeTopic();

    
    topic1 = builder.makeTopic();
    basename1 = builder.makeTopicName(topic1, "Wilhelmine von Hillern");
    basename1.addTheme(topicWriter);
    variant1A = builder.makeVariantName(basename1, "Hillern, Wilhelmine");
    variant1A.addTheme(topicSort);
    
    topic2 = builder.makeTopic();
    basename2 = builder.makeTopicName(topic2, "Alphonse Daudet");
    basename2.addTheme(topicWriter);
    variant2A = builder.makeVariantName(basename2, "Daudet, Alphonse");
    variant2A.addTheme(topicSort);

    topic3 = builder.makeTopic();
    basename3 = builder.makeTopicName(topic3, "El trovador");
    basename3.addTheme(topicPlay);
    variant3A = builder.makeVariantName(basename3, "Trovador");
    variant3A.addTheme(topicSort);

    topic4 = builder.makeTopic();
    basename4 = builder.makeTopicName(topic4, "The Merry Wives of Windsor");
    basename4.addTheme(topicPlay);
    variant4A = builder.makeVariantName(basename4, "Merry Wives of Windsor");
    variant4A.addTheme(topicSort);

    topic5 = builder.makeTopic();
    basename5A = builder.makeTopicName(topic5, "Die Jungfrau von Orleans");
    basename5A.addTheme(topicPlay);
    variant5A = builder.makeVariantName(basename5A, "Jungfrau von Orleans");
    variant5A.addTheme(topicSort);
    
    basename5B = builder.makeTopicName(topic5, "Jungfrau von Orleans");

    basename5C = builder.makeTopicName(topic5, "Jungfrau von Orleans, Die");
    variant5C = builder.makeVariantName(basename5A, "Jungfrau von Orleans");
    variant5C.addTheme(topicPlay);
  }

  public TopicMapIF makeTopicMap() {
    return store.getTopicMap();
  }
 
  // --- Test cases

  public void testNameGrabber5A() {
    List basenameScope = new ArrayList();
    basenameScope.add(topicPlay);
    List variantScope = new ArrayList();
    variantScope.add(topicSort);
    GrabberIF grabber = new NameGrabber(basenameScope, variantScope);

    assertTrue("wrong base name grabbed",
           ((TopicNameIF) grabber.grab(topic5)).equals(basename5A));
  }
  
  public void testNameGrabber5B() {
    GrabberIF grabber = new NameGrabber(Collections.EMPTY_LIST);

    assertTrue("wrong base name grabbed",
           ((TopicNameIF) grabber.grab(topic5)).equals(basename5B));
  }

  public void testNameGrabber5C() {
    List variantScope = new ArrayList();
    variantScope.add(topicSort);
    GrabberIF grabber = new NameGrabber(Collections.EMPTY_LIST, variantScope);

    assertTrue("wrong base name grabbed",
           ((TopicNameIF) grabber.grab(topic5)).equals(basename5A));
  }
}




