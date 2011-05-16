
// $Id: VariantPredicateTest.java,v 1.4 2008/06/12 14:37:21 geir.gronmo Exp $

package net.ontopia.topicmaps.query.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.VariantNameIF;

public class VariantPredicateTest extends AbstractPredicateTest {
  
  public VariantPredicateTest(String name) {
    super(name);
  }

  /// tests
  
  public void testCompletelyOpen() throws InvalidQueryException, IOException {
    load("family.ltm");

    List matches = new ArrayList();
    Iterator it = topicmap.getTopics().iterator();
    while (it.hasNext()) {
      TopicIF topic = (TopicIF) it.next();
      Iterator it2 = topic.getTopicNames().iterator();
      while (it2.hasNext()) {
        TopicNameIF bn = (TopicNameIF) it2.next();

        Iterator it3 = bn.getVariants().iterator();
        while (it3.hasNext()) {
          VariantNameIF vn = (VariantNameIF) it3.next();
          addMatch(matches, "TNAME", bn, "VNAME", vn);
        }
      }
    }
    
    verifyQuery(matches, "variant($TNAME, $VNAME)?");
    closeStore();
  }

  public void testWithSpecificParent() throws InvalidQueryException, IOException {
    load("family.ltm");

    List matches = new ArrayList();
    addVariantNames(matches, "VNAME", getTopicById("edvin"));
    
    verifyQuery(matches, "select $VNAME from " +
                         "topic-name(edvin, $BNAME), variant($BNAME, $VNAME)?");
    closeStore();
  }

  public void testCrossJoin() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList(); // should not match anything
    
    verifyQuery(matches, OPT_TYPECHECK_OFF +
                "occurrence(white-horse, $OCC), topic-name($T, $TN), " +
                "variant($TN, $OCC)?");
    closeStore();
  }

  public void testWithSpecificVariant() throws InvalidQueryException, IOException {
    load("family.ltm");

    List matches = new ArrayList();
    TopicIF topic = getTopicById("kfg");
    TopicNameIF bn = (TopicNameIF) topic.getTopicNames().iterator().next();
    VariantNameIF vn = (VariantNameIF) bn.getVariants().iterator().next();

    addMatch(matches, "TN", bn);
    
    verifyQuery(matches, "variant($TN, @" + vn.getObjectId() + ")?");
    closeStore();
  }

  public void testWithBothBoundTrue() throws InvalidQueryException, IOException {
    load("family.ltm");

    List matches = new ArrayList();
    TopicIF topic = getTopicById("petter");
    TopicNameIF bn = (TopicNameIF) topic.getTopicNames().iterator().next();
    VariantNameIF vn = (VariantNameIF) bn.getVariants().iterator().next();

    matches.add(new HashMap());
    verifyQuery(matches,
                "variant(@" + bn.getObjectId() + ", @" + vn.getObjectId() + ")?");
    closeStore();
  }
  
  public void testWithBothBoundFalse() throws InvalidQueryException, IOException {
    load("family.ltm");

    List matches = new ArrayList();
    TopicIF topic1 = getTopicById("petter");
    TopicNameIF bn1 = (TopicNameIF) topic1.getTopicNames().iterator().next();
    
    TopicIF topic2 = getTopicById("asle");
    TopicNameIF bn2 = (TopicNameIF) topic2.getTopicNames().iterator().next();
    VariantNameIF vn = (VariantNameIF) bn2.getVariants().iterator().next();

    verifyQuery(matches, "variant(@" + bn1.getObjectId() + ", @" + vn.getObjectId() + ")?");
    closeStore();
  }

  /// helpers

  private void addVariantNames(List matches, String var, TopicIF topic) {
    Iterator it = topic.getTopicNames().iterator();
    while (it.hasNext()) {
      TopicNameIF bn = (TopicNameIF) it.next();

      Iterator it2 = bn.getVariants().iterator();
      while (it2.hasNext()) {
        VariantNameIF vn = (VariantNameIF) it2.next();
        addMatch(matches, var, vn);
      }
    }
  }
  
}
