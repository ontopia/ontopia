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
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.ontopia.topicmaps.core.DataTypes;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import org.junit.Test;

public class ValuePredicateTest extends AbstractPredicateTest {
  
  /// tests

  @Test
  public void testGenerateAll() throws InvalidQueryException, IOException {
    load("jill.xtm");
    
    List matches = new ArrayList();
    Iterator it = topicmap.getTopics().iterator();
    while (it.hasNext()) {
      TopicIF topic = (TopicIF) it.next();

      Iterator it2 = topic.getTopicNames().iterator();
      while (it2.hasNext()) {
        TopicNameIF bn = (TopicNameIF) it2.next();
        if (bn.getValue() != null)
          addMatch(matches, "OBJ", bn, "VALUE", bn.getValue());

        Iterator it3 = bn.getVariants().iterator();
        while (it3.hasNext()) {
          VariantNameIF vn = (VariantNameIF) it3.next();
          if (vn.getValue() != null && !Objects.equals(vn.getDataType(), DataTypes.TYPE_URI))
            addMatch(matches, "OBJ", vn, "VALUE", vn.getValue());
        }
      }

      it2 = topic.getOccurrences().iterator();
      while (it2.hasNext()) {
        OccurrenceIF occ = (OccurrenceIF) it2.next();
        if (occ.getValue() != null && !Objects.equals(occ.getDataType(), DataTypes.TYPE_URI))
          addMatch(matches, "OBJ", occ, "VALUE", occ.getValue());
      }
    }
    assertQueryMatches(matches, "value($OBJ, $VALUE)?");
  }

  @Test
  public void testWithSpecificObject() throws InvalidQueryException, IOException {
    load("int-occs.ltm");
    
    assertFindNothing(OPT_TYPECHECK_OFF +
                "value(topic1, $VALUES)?");
  }

  @Test
  public void testWithSpecificObjectAndString()
    throws InvalidQueryException, IOException {
    load("int-occs.ltm");

    assertFindNothing(OPT_TYPECHECK_OFF +
                "value(topic1, \"topic1\")?");
  }
  
  @Test
  public void testWithAnyObjectNoMatch() throws InvalidQueryException, IOException {
    load("family.ltm");

    assertFindNothing("select $TOPIC from " +
                "  value($BNAME, \"skalle\"), " +
                "  topic-name($TOPIC, $BNAME)?");
  }

  @Test
  public void testWithAnyObjectBNMatch() throws InvalidQueryException, IOException {
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "TOPIC", getTopicById("lms"));
    
    assertQueryMatches(matches, "select $TOPIC from " +
                         "  value($BNAME, \"Lars Magne Skalle\"), " +
                         "  topic-name($TOPIC, $BNAME)?");
  }

  @Test
  public void testWithAnyObjectOccMatch() throws InvalidQueryException, IOException {
    load("int-occs.ltm");

    List matches = new ArrayList();
    addMatch(matches, "TOPIC", getTopicById("topic1"));
    
    assertQueryMatches(matches, "select $TOPIC from " +
                         "  value($OCC, \"topic1\"), " +
                         "  occurrence($TOPIC, $OCC)?");
  }

  @Test
  public void testWithAnyObjectVariantMatch() throws InvalidQueryException,
                                                     IOException {
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "TOPIC", getTopicById("petter"));
    
    assertQueryMatches(matches, "select $TOPIC from " +
                         "  value($VNAME, \"2\"), " +
                         "  variant($BNAME, $VNAME), " +
                         "  topic-name($TOPIC, $BNAME)?");
  }

  @Test
  public void testGetTopicNameValue() throws InvalidQueryException, IOException {
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "VALUE", "Lars Magne Skalle");
    
    assertQueryMatches(matches, "select $VALUE from " +
                         "  value($BNAME, $VALUE), " +
                         "  topic-name(lms, $BNAME)?");
  }

  @Test
  public void testValueInRule() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    addMatch(matches, "TOPIC", getTopicById("thequeen"));
    addMatch(matches, "TOPIC", getTopicById("comment1"));

    assertQueryMatches(matches,
                "has-value($TOPIC, $VALUE) :- { " +
                "  value($NAME, $VALUE), topic-name($TOPIC, $NAME) | " +
                "  value($OCC, $VALUE), occurrence($TOPIC, $OCC) " +
                "}." +
                
                "select $TOPIC from " +
                "  has-value($TOPIC, \"2003-06-03\")?");
  }


  @Test
  public void testValueOfValue() throws InvalidQueryException, IOException {
    load("family.ltm");
    assertFindNothing(OPT_TYPECHECK_OFF +
                "value($A, $A)?");
  }

  @Test
  public void testWithSingleQuote() throws InvalidQueryException, IOException {
    load("family.ltm");
    assertFindNothing("select $TOPIC from " +
                "  value($BNAME, \"foo'bar\"), " +
                "  topic-name($TOPIC, $BNAME)?");
  }

}
