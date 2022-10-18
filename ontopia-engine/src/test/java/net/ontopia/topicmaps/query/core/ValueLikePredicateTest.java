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
import java.util.List;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.OntopiaUnsupportedException;
import org.junit.Assert;
import org.junit.Test;

public class ValueLikePredicateTest extends AbstractPredicateTest {
  
  /// tests
  @Test
  public void testWithoutFulltextIndex() throws IOException, InvalidQueryException {
    load("int-occs.ltm", false);
    try {
      assertFindNothing("value-like($foo, \"foo\")?");
      Assert.fail("Value-like on a topicmap without a fulltext index should Assert.fail, but didn't");
    } catch (OntopiaRuntimeException e) {
      if (!(e.getCause() instanceof OntopiaUnsupportedException)) {
        Assert.fail("Value-like on a topicmap without a fulltext index threw wrong exception");
      }
      // expected
    }
  }

  @Test
  public void testWithSpecificOccurrenceAndString()
    throws InvalidQueryException, IOException {
    load("int-occs.ltm", true);

    TopicIF topic = getTopicById("topic1");
    OccurrenceIF oc = topic.getOccurrences().iterator().next();
    String oid = oc.getObjectId();

    // first argument must be unbound
    assertGetParseError("value-like(@" + oid + ", \"topic1\")?");
  }

  @Test
  public void testWithSpecificTopicAndVariable() throws InvalidQueryException, IOException {
    load("int-occs.ltm", true);
    
    // this predicate will Assert.fail because the first argument is of type TopicIF
    assertFindNothing(OPT_TYPECHECK_OFF +
                "value-like(topic1, $VALUE)?");
  }

  @Test
  public void testWithSpecificTopicAndString() throws InvalidQueryException, IOException {
    load("int-occs.ltm", true);
    
    // this predicate will Assert.fail because the first argument is of type TopicIF
    assertFindNothing(OPT_TYPECHECK_OFF +
                "value-like(topic1, \"topic1\")?");
  }

  @Test
  public void testWithUnboundBoth1() throws InvalidQueryException, IOException {
    load("int-occs.ltm", true);

    // query parser will complain because the second argument is unbound
    assertGetParseError("value-like($FOO, $VALUE)?");
  }

  @Test
  public void testWithUnboundBoth2() throws InvalidQueryException, IOException {
    load("int-occs.ltm", true);

    // this test will Assert.fail because the $TOPIC variable is of type TopicIF
    assertFindNothing(OPT_TYPECHECK_OFF +
                "topic($TOPIC), value-like($TOPIC, $VALUE)?");
  }

  @Test
  public void testWithUnboundPatternValue() throws InvalidQueryException, IOException {
    load("int-occs.ltm", true);

    TopicIF topic = getTopicById("topic1");
    OccurrenceIF oc = topic.getOccurrences().iterator().next();
    String oid = oc.getObjectId();
    
    assertGetParseError("value-like(@" + oid + ", $VALUE)?");
  }
  
  @Test
  public void testWithAnyObject() throws InvalidQueryException, IOException {
    load("family.ltm", true);

    List matches = new ArrayList();
    addMatch(matches, "TOPIC", getTopicById("lms"));
    addMatch(matches, "TOPIC", getTopicById("gerd"));
    addMatch(matches, "TOPIC", getTopicById("asle"));
    
    assertQueryMatches(matches, "select $TOPIC from " +
                         "  value-like($BNAME, \"skalle\"), " +
                         "  topic-name($TOPIC, $BNAME)?");
  }
  
  @Test
  public void testWithScoreBound() throws InvalidQueryException, IOException {
    load("family.ltm", true);
    
    assertGetParseError("select $TOPIC from " +
                  "  value-like($BNAME, \"skalle\", 0.54), " +
                  "  topic-name($TOPIC, $BNAME)?");
  }
  
  // filtering on score is possible but not advised as it is searcher dependent and
  // might not be percentage based (like with lucene > 3.x)
  @Test
  public void testWithScoreAbove001() throws InvalidQueryException, IOException {
    load("family.ltm", true);

    List matches = new ArrayList();
    addMatch(matches, "TOPIC", getTopicById("lms"));
    addMatch(matches, "TOPIC", getTopicById("gerd"));
    addMatch(matches, "TOPIC", getTopicById("asle"));
    
    assertQueryMatches(matches, "select $TOPIC from " +
                         "  value-like($BNAME, \"skalle\", $SCORE), " +
                         "  topic-name($TOPIC, $BNAME), $SCORE > 0.01?");
  }
  
  @Test
  public void testWithEscapedQuotes() throws InvalidQueryException, IOException {
    load("family.ltm", true);

    // WARNING: rdbms: this will cause a parse error with Oracle Text
    assertFindNothing("select $TOPIC from " +
                "  value-like($BNAME, \"and \"\"ho\"\" ho\"), " +
                "  topic-name($TOPIC, $BNAME)?");
  }

  @Test
  public void testWithSingleQuote() throws InvalidQueryException, IOException {
    load("family.ltm", true);
    
    assertFindNothing("select $TOPIC from " +
                "  value-like($BNAME, \"foo'bar\"), " +
                "  topic-name($TOPIC, $BNAME)?");
  }

  // see bug #955
  @Test
  public void testWithEmptyString() throws InvalidQueryException, IOException {
    load("family.ltm", true);

    assertFindNothing("select $TOPIC from " +
                "  value-like($BNAME, \"\"), " +
                "  topic-name($TOPIC, $BNAME)?");
  }

  @Test
  public void testBug987() throws InvalidQueryException, IOException {
    load("int-occs.ltm", true);

    List matches = new ArrayList();
    addMatch(matches, "TOPIC", getTopicById("topic1"));

    assertQueryMatches(matches,
                "select $TOPIC from " +
                "  type($OCC, description), " +
                "  occurrence($TOPIC, $OCC), " + 
                "  value-like($OCC, \"topic1\")?");
  }

  @Test
  public void testIssue302() throws InvalidQueryException, IOException {
    load("int-occs.ltm", true);

    List matches = new ArrayList();
    addMatch(matches, "TOPIC", getTopicById("topic1"));

    assertQueryMatches(matches,
                "select $TOPIC from " +
                "  $query = \"topic1\", " +
                "  type($OCC, description), " +
                "  occurrence($TOPIC, $OCC), " + 
                "  value-like($OCC, $query)?");
  }
}
