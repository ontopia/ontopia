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

package net.ontopia.topicmaps.query.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import org.junit.Test;

public class VariantPredicateTest extends AbstractPredicateTest {
  
  /// tests
  
  @Test
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
    
    assertQueryMatches(matches, "variant($TNAME, $VNAME)?");
    closeStore();
  }

  @Test
  public void testWithSpecificParent() throws InvalidQueryException, IOException {
    load("family.ltm");

    List matches = new ArrayList();
    addVariantNames(matches, "VNAME", getTopicById("edvin"));
    
    assertQueryMatches(matches, "select $VNAME from " +
                         "topic-name(edvin, $BNAME), variant($BNAME, $VNAME)?");
    closeStore();
  }

  @Test
  public void testCrossJoin() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList(); // should not match anything
    
    assertQueryMatches(matches, OPT_TYPECHECK_OFF +
                "occurrence(white-horse, $OCC), topic-name($T, $TN), " +
                "variant($TN, $OCC)?");
    closeStore();
  }

  @Test
  public void testWithSpecificVariant() throws InvalidQueryException, IOException {
    load("family.ltm");

    List matches = new ArrayList();
    TopicIF topic = getTopicById("kfg");
    TopicNameIF bn = (TopicNameIF) topic.getTopicNames().iterator().next();
    VariantNameIF vn = (VariantNameIF) bn.getVariants().iterator().next();

    addMatch(matches, "TN", bn);
    
    assertQueryMatches(matches, "variant($TN, @" + vn.getObjectId() + ")?");
    closeStore();
  }

  @Test
  public void testWithBothBoundTrue() throws InvalidQueryException, IOException {
    load("family.ltm");

    List matches = new ArrayList();
    TopicIF topic = getTopicById("petter");
    TopicNameIF bn = (TopicNameIF) topic.getTopicNames().iterator().next();
    VariantNameIF vn = (VariantNameIF) bn.getVariants().iterator().next();

    matches.add(new HashMap());
    assertQueryMatches(matches,
                "variant(@" + bn.getObjectId() + ", @" + vn.getObjectId() + ")?");
    closeStore();
  }
  
  @Test
  public void testWithBothBoundFalse() throws InvalidQueryException, IOException {
    load("family.ltm");

    List matches = new ArrayList();
    TopicIF topic1 = getTopicById("petter");
    TopicNameIF bn1 = (TopicNameIF) topic1.getTopicNames().iterator().next();
    
    TopicIF topic2 = getTopicById("asle");
    TopicNameIF bn2 = (TopicNameIF) topic2.getTopicNames().iterator().next();
    VariantNameIF vn = (VariantNameIF) bn2.getVariants().iterator().next();

    assertQueryMatches(matches, "variant(@" + bn1.getObjectId() + ", @" + vn.getObjectId() + ")?");
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
