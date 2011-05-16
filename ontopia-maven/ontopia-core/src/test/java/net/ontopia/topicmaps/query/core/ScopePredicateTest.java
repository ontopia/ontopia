
// $Id: ScopePredicateTest.java,v 1.9 2008/06/12 14:37:21 geir.gronmo Exp $

package net.ontopia.topicmaps.query.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.ScopedIF;
import net.ontopia.topicmaps.core.TopicIF;

public class ScopePredicateTest extends AbstractPredicateTest {
  
  public ScopePredicateTest(String name) {
    super(name);
  }

  /// setup

  public void tearDown() {    
    closeStore();
  }

  /// setup

  public void testCompletelyOpen() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    addScopesOf(matches, topicmap.getAssociations());

    Iterator it = topicmap.getTopics().iterator();
    while (it.hasNext()) {
      TopicIF topic = (TopicIF) it.next();
      
      addScopesOf(matches, topic.getOccurrences());
      addScopesOf(matches, topic.getTopicNames());

      Iterator it2 = topic.getTopicNames().iterator();
      while (it2.hasNext())
        addScopesOf(matches, ((TopicNameIF) it2.next()).getVariants());
    }
                
    verifyQuery(matches, "scope($SCOPED, $THEME)?");
  }

  public void testCrossJoin() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    
    verifyQuery(matches, OPT_TYPECHECK_OFF +
                "association-role($ASSOC, $ROLE), scope($ROLE, $THEME)?");
  } 
  
  private void addScopesOf(List matches, Collection scopeds) {
    Iterator it = scopeds.iterator();
    while (it.hasNext()) {
      ScopedIF object = (ScopedIF) it.next();
      Iterator it2 = object.getScope().iterator();
      while (it2.hasNext())
        addMatch(matches, "SCOPED", object, "THEME", it2.next());
    }
  }
  
  public void testTopicNameBothBound() throws InvalidQueryException, IOException {
    makeEmpty();
    TopicIF theme = builder.makeTopic();
    TopicIF topic = builder.makeTopic();
    TopicNameIF thing = builder.makeTopicName(topic, "mybasename");
    thing.addTheme(theme);
    
    List matches = new ArrayList();
    addMatch(matches);
 
    verifyQuery(matches, "scope(@" + thing.getObjectId() + ", @" + theme.getObjectId() + ")?");
  }
  
  public void testTopicNameBothUnbound() throws InvalidQueryException, IOException {
    makeEmpty();
    TopicIF theme = builder.makeTopic();
    TopicIF topic = builder.makeTopic();
    TopicNameIF thing = builder.makeTopicName(topic, "mybasename");
    thing.addTheme(theme);
    
    List matches = new ArrayList();
    addMatch(matches, "THING", thing, "THEME", theme);

    // NOTE: using topic-name predicate here to avoid type cross product
    verifyQuery(matches, "topic-name(@" + topic.getObjectId() + ", $THING), scope($THING, $THEME)?");
  }
  
  public void testTopicNameBound() throws InvalidQueryException, IOException {
    makeEmpty();
    TopicIF theme = builder.makeTopic();
    TopicIF topic = builder.makeTopic();
    TopicNameIF thing = builder.makeTopicName(topic, "mybasename");
    thing.addTheme(theme);
    
    List matches = new ArrayList();
    addMatch(matches, "THEME", theme);
 
    verifyQuery(matches, "scope(@" + thing.getObjectId() + ", $THEME)?");
  }
  
  public void testTopicNameUnbound() throws InvalidQueryException, IOException {
    makeEmpty();
    TopicIF theme = builder.makeTopic();
    TopicIF topic = builder.makeTopic();
    TopicNameIF thing = builder.makeTopicName(topic, "mybasename");
    thing.addTheme(theme);
    
    List matches = new ArrayList();
    addMatch(matches, "THING", thing);
 
    // NOTE: using topic-name predicate here to avoid type cross product
    verifyQuery(matches, "topic-name(@" + topic.getObjectId() + ", $THING), scope($THING, @" + theme.getObjectId() + ")?");
  }
  
  public void testSpecificTopicNameNoScope() throws InvalidQueryException, IOException {
    makeEmpty();
    TopicIF type = builder.makeTopic();
    TopicIF topic = builder.makeTopic(type);
    TopicNameIF bn = builder.makeTopicName(topic, "name");

    List matches = new ArrayList();
    verifyQuery(matches, "scope(@" + bn.getObjectId() + ", $THEME)?");
  }
  
}
