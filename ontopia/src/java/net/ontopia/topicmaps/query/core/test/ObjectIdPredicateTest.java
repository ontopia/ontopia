
// $Id: ObjectIdPredicateTest.java,v 1.4 2008/06/12 14:37:21 geir.gronmo Exp $

package net.ontopia.topicmaps.query.core.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;

public class ObjectIdPredicateTest extends AbstractPredicateTest {
  
  public ObjectIdPredicateTest(String name) {
    super(name);
  }

  /// tests
  
  public void testCompletelyOpen() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    addMatch(matches, "OBJECT", topicmap, "ID", topicmap.getObjectId());
    
    Iterator it = topicmap.getTopics().iterator();
    while (it.hasNext()) {
      TopicIF topic = (TopicIF) it.next();
      addMatch(matches, "OBJECT", topic, "ID", topic.getObjectId());

      Iterator it2 = topic.getTopicNames().iterator();
      while (it2.hasNext()) {
        TopicNameIF bn = (TopicNameIF) it2.next();
        addMatch(matches, "OBJECT", bn, "ID", bn.getObjectId());

        Iterator it3 = bn.getVariants().iterator();
        while (it3.hasNext()) {
          VariantNameIF vn = (VariantNameIF) it3.next();
          addMatch(matches, "OBJECT", vn, "ID", vn.getObjectId());
        }
      }

      it2 = topic.getOccurrences().iterator();
      while (it2.hasNext()) {
        OccurrenceIF occ = (OccurrenceIF) it2.next();
        addMatch(matches, "OBJECT", occ, "ID", occ.getObjectId());
      }
    }

    it = topicmap.getAssociations().iterator();
    while (it.hasNext()) {
      AssociationIF assoc = (AssociationIF) it.next();
      addMatch(matches, "OBJECT", assoc, "ID", assoc.getObjectId());

      Iterator it2 = assoc.getRoles().iterator();
      while (it2.hasNext()) {
        AssociationRoleIF role = (AssociationRoleIF) it2.next();
        addMatch(matches, "OBJECT", role, "ID", role.getObjectId());
      }
    }
    
    verifyQuery(matches, "object-id($OBJECT, $ID)?");
    closeStore();
  }

  public void testWithSpecificTopic() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    TopicIF horse = getTopicById("horse");

    List matches = new ArrayList();
    addMatch(matches, "ID", horse.getObjectId());
    
    verifyQuery(matches, "object-id(horse, $ID)?");
    closeStore();
  }

  public void testWithSpecificId() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    TopicIF topic = getTopicById("thequeen");
    addMatch(matches, "TOPIC", topic);
    
    verifyQuery(matches, "object-id($TOPIC, \"" + topic.getObjectId() + "\")?");
    closeStore();
  }

  public void testWithTopicNames() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    findNothing(OPT_TYPECHECK_OFF +
                "object-id(horse, $BN), topic-name($T, $BN)?");
    closeStore();
  }
  
  public void testWithBothBoundTrue() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    matches.add(new HashMap());

    TopicIF topic = getTopicById("thequeen");
    
    verifyQuery(matches, "object-id(thequeen, \"" + topic.getObjectId() +"\")?");
    closeStore();
  }
  
  public void testWithBothBoundFalse() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    TopicIF topic = getTopicById("thequeen");
    
    verifyQuery(matches, "object-id(equation, \"" + topic.getObjectId() + "\")?");
    closeStore();
  }
  
}
