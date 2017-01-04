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
import java.util.Iterator;
import java.util.Map;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import org.junit.Assert;
import org.junit.Test;

// FIXME: test with parameters
// FIXME: test with bad URLs in delete functions
// FIXME: test with topic which has MANY identifiers

public class DeleteTest extends AbstractQueryTest {
  
  /// empty topic map
  
  @Test
  public void testEmptyDelete() throws InvalidQueryException {
    makeEmpty();
    assertUpdate("delete $A, $B from direct-instance-of($A, $B)");
  }

  /// instance-of topic map
  
  @Test
  public void testStaticDelete() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    int before = topicmap.getTopics().size();
    Assert.assertTrue("topic4 missing", getTopicById("topic4") != null);
    
    assertUpdate("delete topic4");

    Assert.assertTrue("wrong number of topics after delete",
               topicmap.getTopics().size() == (before - 1));
    Assert.assertTrue("topic4 still available after delete",
               getTopicById("topic4") == null);
  }

  @Test
  public void testSimpleDelete() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    int before = topicmap.getTopics().size();
    Assert.assertTrue("topic4 missing", getTopicById("topic4") != null);
    
    assertUpdate("delete $A from $A = topic4");

    Assert.assertTrue("wrong number of topics after delete",
               topicmap.getTopics().size() == (before - 1));
    Assert.assertTrue("topic4 still available after delete",
               getTopicById("topic4") == null);
  }

  @Test
  public void testProjectedDelete() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    int before = topicmap.getTopics().size();
    Assert.assertTrue("topic4 missing", getTopicById("topic4") != null);
    
    assertUpdate("delete $A from $A = topic4, $B = topic3");

    Assert.assertTrue("wrong number of topics after delete",
               topicmap.getTopics().size() == (before - 1));
    Assert.assertTrue("topic4 still available after delete",
               getTopicById("topic4") == null);
    Assert.assertTrue("topic3 not still available after delete",
               getTopicById("topic3") != null);
  }
  
  @Test
  public void testMixedDelete() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    int before = topicmap.getTopics().size();
    Assert.assertTrue("topic4 missing", getTopicById("topic4") != null);
    Assert.assertTrue("topic3 missing", getTopicById("topic3") != null);
    
    assertUpdate("delete $A, topic3 from $A = topic4");

    Assert.assertTrue("wrong number of topics after delete",
               topicmap.getTopics().size() == (before - 2));
    Assert.assertTrue("topic4 still available after delete",
               getTopicById("topic4") == null);
    Assert.assertTrue("topic3 still available after delete",
               getTopicById("topic3") == null);
  }

  @Test
  public void testTopicTypeDelete() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    int before = topicmap.getTopics().size();
    Assert.assertTrue("topic4 missing", getTopicById("topic4") != null);
    Assert.assertTrue("topic3 missing", getTopicById("topic3") != null);
    Assert.assertTrue("type2 missing", getTopicById("type2") != null);
    
    assertUpdate("delete type2");

    Assert.assertTrue("wrong number of topics after delete",
               topicmap.getTopics().size() == (before - 3));
    Assert.assertTrue("type2 still available after delete",
               getTopicById("type2") == null);
    Assert.assertTrue("topic4 still available after delete",
               getTopicById("topic4") == null);
    Assert.assertTrue("topic3 still available after delete",
               getTopicById("topic3") == null);
  }  

  @Test
  public void testDeleteTwice() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    int before = topicmap.getTopics().size();
    Assert.assertTrue("topic4 missing", getTopicById("topic4") != null);
    
    assertUpdate("delete $A, topic4 from $A = topic4");

    Assert.assertTrue("wrong number of topics after delete",
               topicmap.getTopics().size() == (before - 1));
    Assert.assertTrue("topic4 still available after delete",
               getTopicById("topic4") == null);
  }

  @Test
  public void testBiggerDelete() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    int before = topicmap.getTopics().size();
    Assert.assertTrue("topic4 missing", getTopicById("topic4") != null);
    Assert.assertTrue("topic3 missing", getTopicById("topic3") != null);
    
    assertUpdate("delete $A from instance-of($A, type2)");

    Assert.assertTrue("wrong number of topics after delete",
               topicmap.getTopics().size() == (before - 2));
    Assert.assertTrue("topic4 still available after delete",
               getTopicById("topic4") == null);
    Assert.assertTrue("topic3 still available after delete",
               getTopicById("topic3") == null);
  }  

  @Test
  public void testDeleteAll() throws InvalidQueryException, IOException {
    load("instance-of.ltm");
    
    assertUpdate("delete $A, $B from instance-of($A, $B)");

    // only the implicitly defined default name type remains after this
    Assert.assertTrue("wrong number of topics after delete",
               topicmap.getTopics().size() == 1);
  }

  /// delete function tests

  @Test
  public void testIIStatic() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    TopicIF topic4 = getTopicById("topic4");
    LocatorIF ii = (LocatorIF) topic4.getItemIdentifiers().iterator().next();
    
    assertUpdate("delete item-identifier(topic4, \"" + ii.getAddress() + "\")");

    Assert.assertTrue("topic retains item identifier after delete",
               topic4.getItemIdentifiers().isEmpty());
  }

  @Test
  public void testIIDynamic() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    TopicIF topic4 = getTopicById("topic4");
    
    assertUpdate("delete item-identifier(topic4, $II) from item-identifier(topic4, $II)");

    Assert.assertTrue("topic retains item identifier after delete",
               topic4.getItemIdentifiers().isEmpty());
  }

  @Test
  public void testSIStatic() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    TopicIF topic = getTopicById("type1");
    LocatorIF si = (LocatorIF) topic.getSubjectIdentifiers().iterator().next();
    
    assertUpdate("delete subject-identifier(type1, \"" + si.getAddress() + "\")");

    Assert.assertTrue("topic retains subject identifier after delete",
               topic.getSubjectIdentifiers().isEmpty());
  }

  @Test
  public void testSIDynamic() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    TopicIF topic = getTopicById("type1");
    
    assertUpdate("delete subject-identifier(type1, $SI) from subject-identifier(type1, $SI)");

    Assert.assertTrue("topic retains subject identifier after delete",
               topic.getSubjectIdentifiers().isEmpty());
  }

  @Test
  public void testSLStatic() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    TopicIF topic = getTopicById("type2");
    LocatorIF sl = (LocatorIF) topic.getSubjectLocators().iterator().next();
    
    assertUpdate("delete subject-locator(type2, \"" + sl.getAddress() + "\")");

    Assert.assertTrue("topic retains subject locator after delete",
               topic.getSubjectLocators().isEmpty());
  }

  @Test
  public void testSLDynamic() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    TopicIF topic = getTopicById("type2");
    
    assertUpdate("delete subject-locator(type2, $SL) from subject-locator(type2, $SL)");

    Assert.assertTrue("topic retains subject locator after delete",
               topic.getSubjectLocators().isEmpty());
  }

  @Test
  public void testDIOStatic() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    TopicIF topic = getTopicById("topic1");
    
    assertUpdate("delete direct-instance-of(topic1, type1)");

    Assert.assertTrue("topic retains type after delete",
               topic.getTypes().isEmpty());
  }

  @Test
  public void testDIODynamic() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    TopicIF topic = getTopicById("topic1");
    
    assertUpdate("delete direct-instance-of($I, $T) from $I = topic1, $T = type1");

    Assert.assertTrue("topic retains type after delete",
               topic.getTypes().isEmpty());
  }

  @Test
  public void testScopeDynamic() throws InvalidQueryException, IOException {
    load("bb-ontologi.ltm");

    TopicIF topic = getTopicById("bbtype");
    Iterator it = topic.getTopicNames().iterator();
    TopicNameIF name = (TopicNameIF) it.next();
    if (name.getScope().isEmpty()) // need the one with scope
      name = (TopicNameIF) it.next();
    
    assertUpdate("delete scope($N, english) from topic-name(bbtype, $N), scope($N, english)");

    Assert.assertTrue("name retains scope after delete",
               name.getScope().isEmpty());
  }

  @Test
  public void testReifiesDynamic() throws InvalidQueryException, IOException {
    load("jill.xtm");

    TopicIF topic = getTopicById("jill-ontopia-topic");
    AssociationIF reified = (AssociationIF) topic.getReified();
    
    assertUpdate("delete reifies($T, $A) from instance-of($T, employment), reifies($T, $A)");

    Assert.assertTrue("topic retains reified after delete",
               topic.getReified() == null);
    Assert.assertTrue("reified retains reifier after delete",
               reified.getReifier() == null);
  }

  @Test
  public void testReifiesDynamic2() throws InvalidQueryException, IOException {
    load("jill.xtm");

    TopicIF contract = getTopicById("jills-contract-topic");
    TopicIF employment = getTopicById("jill-ontopia-topic");

    // this test tries to delete an association that is *not* reified
    // by jills-contract-topic, and so it shouldn't do anything
    assertUpdate("delete reifies(jills-contract-topic, $A) from instance-of($T, employment), reifies($T, $A)");

    Assert.assertTrue("topic lost reified after delete",
               contract.getReified() != null);
    Assert.assertTrue("topic lost reified after delete",
               employment.getReified() != null);
  }

  @Test
  public void testQName() throws InvalidQueryException, IOException {
    load("subclasses.ltm");

    int topics = topicmap.getTopics().size();
    TopicIF subclass = getTopicById("subclass");

    assertUpdate("using xtm for i\"http://www.topicmaps.org/xtm/1.0/core.xtm#\" " +
           "delete xtm:subclass");

    Assert.assertTrue("topic still attached to TM after delete",
               subclass.getTopicMap() == null);
    Assert.assertTrue("wrong number of topics after delete",
               topicmap.getTopics().size() == (topics - 1));
  }  

  @Test
  public void testQName2() throws InvalidQueryException, IOException {
    load("subclasses.ltm");

    int topics = topicmap.getTopics().size();
    TopicIF subclass = getTopicById("subclass");

    assertUpdate("using xtm for i\"http://www.topicmaps.org/xtm/1.0/core.xtm#\" " +
           "delete $A from $A = xtm:subclass");

    Assert.assertTrue("topic still attached to TM after delete",
               subclass.getTopicMap() == null);
    Assert.assertTrue("wrong number of topics after delete",
               topicmap.getTopics().size() == (topics - 1));
  }  

  @Test
  public void testDeclarationContext()
    throws InvalidQueryException, IOException {
    load("subclasses.ltm");

    int topics = topicmap.getTopics().size();
    TopicIF subclass = getTopicById("subclass");

    DeclarationContextIF context = parseContext("using xtm for i\"http://www.topicmaps.org/xtm/1.0/core.xtm#\"");
    
    assertUpdate("delete xtm:subclass", context);

    Assert.assertTrue("topic still attached to TM after delete",
               subclass.getTopicMap() == null);
    Assert.assertTrue("wrong number of topics after delete",
               topicmap.getTopics().size() == (topics - 1));
  }
  
  @Test
  public void testParam() throws InvalidQueryException, IOException {
    load("subclasses.ltm");

    int topics = topicmap.getTopics().size();
    TopicIF subclass = getTopicById("subclass");
    Map params = makeArguments("topic", subclass);

    assertUpdate("delete %topic%", params);

    Assert.assertTrue("topic still attached to TM after delete",
               subclass.getTopicMap() == null);
    Assert.assertTrue("wrong number of topics after delete",
               topicmap.getTopics().size() == (topics - 1));
  }  

  @Test
  public void testParam2() throws InvalidQueryException, IOException {
    load("subclasses.ltm");

    int topics = topicmap.getTopics().size();
    TopicIF subclass = getTopicById("subclass");
    Map params = makeArguments("topic", subclass);

    assertUpdate("delete $A from $A = %topic%", params);

    Assert.assertTrue("topic still attached to TM after delete",
               subclass.getTopicMap() == null);
    Assert.assertTrue("wrong number of topics after delete",
               topicmap.getTopics().size() == (topics - 1));
  }

  @Test
  public void testParam3() throws InvalidQueryException, IOException {
    load("subclasses.ltm");

    int topics = topicmap.getTopics().size();
    TopicIF subclass = getTopicById("subclass");
    TopicIF superclass = getTopicById("superclass");
    Map params = makeArguments("topic", subclass);

    assertUpdate("delete $A, %topic% from $A = superclass", params);

    Assert.assertTrue("topic still attached to TM after delete",
               subclass.getTopicMap() == null);
    Assert.assertTrue("topic still attached to TM after delete",
               superclass.getTopicMap() == null);
    Assert.assertTrue("wrong number of topics after delete",
               topicmap.getTopics().size() == (topics - 2));
  }
  
  @Test
  public void testFunctionWithParam() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    TopicIF topic = getTopicById("topic1");
    TopicIF type = getTopicById("type1");
    
    assertUpdate("delete direct-instance-of(topic1, %type%)",
           makeArguments("type", type));

    Assert.assertTrue("topic retains type after delete",
               topic.getTypes().isEmpty());
  }
  
  /// error tests
    
  @Test
  public void testVariableButNoFrom() throws InvalidQueryException {
    makeEmpty();
    assertUpdateError("delete $A");
  }

  @Test
  public void testNoSuchVariable() throws InvalidQueryException {
    makeEmpty();
    assertUpdateError("delete $A from $B = 1");
  }

  @Test
  public void testBadType() throws InvalidQueryException {
    makeEmpty();
    assertUpdateError("delete $A from $A = 1");
  }

  @Test
  public void testWrongArgNo() throws InvalidQueryException, IOException {
    load("instance-of.ltm");
    assertUpdateError("delete item-identifier(topic4)");
  }

  @Test
  public void testWrongArgNo2() throws InvalidQueryException, IOException {
    load("instance-of.ltm");
    assertUpdateError("delete item-identifier(topic4, \"foo:bar\", topic3)");
  }

  @Test
  public void testWrongArgType1() throws InvalidQueryException, IOException {
    load("instance-of.ltm");
    assertUpdateError("delete item-identifier(\"foo:bar\", \"foo:bar\")");
  }

  @Test
  public void testWrongArgType2() throws InvalidQueryException, IOException {
    load("instance-of.ltm");
    assertUpdateError("delete item-identifier(topic1, topic2)");
  }
  
  @Test
  public void testFunctionVariableButNoFrom() throws InvalidQueryException {
    makeEmpty();
    assertUpdateError("delete item-identifier($A, $B)");
  }
  
  @Test
  public void testNoSuchFunction() throws InvalidQueryException {
    makeEmpty();
    assertUpdateError("delete gurble(topic4, \"http://example.org\")");
  }
}
