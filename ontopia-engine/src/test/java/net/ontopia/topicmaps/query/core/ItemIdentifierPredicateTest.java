
package net.ontopia.topicmaps.query.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;

public class ItemIdentifierPredicateTest extends AbstractPredicateTest {
  
  public ItemIdentifierPredicateTest(String name) {
    super(name);
  }

  /// setup

  public void tearDown() {    
    closeStore();
  }

  /// tests

  public void testCompletelyOpen() throws InvalidQueryException, IOException {
    load("jill.xtm");

    List matches = new ArrayList();
    addSrclocsOf(matches, Collections.singleton(topicmap));
    addSrclocsOf(matches, topicmap.getAssociations());
    addSrclocsOf(matches, topicmap.getTopics());

    Iterator it = topicmap.getTopics().iterator();
    while (it.hasNext()) {
      TopicIF topic = (TopicIF) it.next();
      
      addSrclocsOf(matches, topic.getOccurrences());
      addSrclocsOf(matches, topic.getTopicNames());

      Iterator it2 = topic.getTopicNames().iterator();
      while (it2.hasNext())
        addSrclocsOf(matches, ((TopicNameIF) it2.next()).getVariants());
    }

    it = topicmap.getAssociations().iterator();
    while (it.hasNext()) {
      AssociationIF assoc = (AssociationIF) it.next();      
      addSrclocsOf(matches, assoc.getRoles());
    }
    
    verifyQuery(matches, "item-identifier($OBJ, $LOCATOR)?");  
  }

  private void addSrclocsOf(List matches, Collection objects) {
    Iterator it = objects.iterator();
    while (it.hasNext()) {
      TMObjectIF object = (TMObjectIF) it.next();

      Iterator it2 = object.getItemIdentifiers().iterator();
      while (it2.hasNext())
        addMatch(matches, "OBJ", object, "LOCATOR", ((LocatorIF) it2.next()).getAddress());
    }
  }
  
  public void testTopicToLocator() throws InvalidQueryException, IOException {
    load("jill.xtm");

    LocatorIF base = topicmap.getStore().getBaseAddress();
    List matches = new ArrayList();
    addMatch(matches, "LOCATOR", base.resolveAbsolute("#ontopia").getAddress());
    
    verifyQuery(matches, "item-identifier(ontopia, $LOCATOR)?");
  }

  public void testLocatorToTopic() throws InvalidQueryException, IOException {
    load("jill.xtm");
    LocatorIF base = topicmap.getStore().getBaseAddress();

    List matches = new ArrayList();
    addMatch(matches, "TOPIC", getTopicById("ontopia"));
    
    verifyQuery(matches, "item-identifier($TOPIC, \"" + base.resolveAbsolute("#ontopia").getAddress() + "\")?");
  }

  public void testBothBoundFalse() throws InvalidQueryException, IOException {
    load("instance-of.ltm");
    LocatorIF base = topicmap.getStore().getBaseAddress();

    List matches = new ArrayList();    
    verifyQuery(matches, "item-identifier(type2, \"" + base.resolveAbsolute("#type1").getAddress() + "\")?");
  }

  public void testBothBoundTrue() throws InvalidQueryException, IOException {
    load("instance-of.ltm");
    LocatorIF base = topicmap.getStore().getBaseAddress();

    List matches = new ArrayList();
    matches.add(new HashMap());
    
    verifyQuery(matches, "item-identifier(type1, \"" + base.resolveAbsolute("#type1").getAddress() + "\")?");
  }
  
}
