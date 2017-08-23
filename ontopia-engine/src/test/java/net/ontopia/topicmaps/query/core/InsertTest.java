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

import java.util.Map;
import java.io.IOException;
import java.net.MalformedURLException;

import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.query.utils.QueryUtils;

public class InsertTest extends AbstractQueryTest {
  
  public InsertTest(String name) {
    super(name);
  }

  /// context management

  public void setUp() {
  }

  public void tearDown() {
    closeStore();
  }

  /// empty topic map
  
  public void testEmptyInsert() throws InvalidQueryException {
    makeEmpty();
    update("insert topic . ");

    TopicIF topic = getTopicById("topic");
    assertTrue("wrong number of topics after insert",
               topicmap.getTopics().size() == 1);
    assertTrue("topic not found after insert",
               topic != null);
  }

  public void testEmptyInsert2() throws InvalidQueryException {
    makeEmpty();
    update("insert http://example.com . ");

    TopicIF topic = (TopicIF) topicmap.getTopics().iterator().next();
    assertTrue("wrong number of topics after insert",
               topicmap.getTopics().size() == 1);
    assertTrue("topic not found after insert",
               topic != null);
    assertTrue("topic does not have subject identifier",
               topic.getSubjectIdentifiers().size() == 1);
  }

  public void testEmptyInsert3() throws InvalidQueryException {
    makeEmpty();
    update("insert = http://example.com . ");

    TopicIF topic = (TopicIF) topicmap.getTopics().iterator().next();
    assertTrue("wrong number of topics after insert",
               topicmap.getTopics().size() == 1);
    assertTrue("topic not found after insert",
               topic != null);
    assertTrue("topic does not have subject locator",
               topic.getSubjectLocators().size() == 1);
  }

  public void testEmptyInsert4() throws InvalidQueryException {
    makeEmpty();
    update("insert topic isa type . ");

    TopicIF topic = getTopicById("topic");
    TopicIF type = getTopicById("type");
    assertTrue("wrong number of topics after insert",
               topicmap.getTopics().size() == 2);
    assertTrue("topic not found after insert",
               topic != null);
    assertTrue("type not found after insert",
               type != null);
    assertTrue("topic not instance of type",
               topic.getTypes().contains(type));
  }

  public void testEmptyInsert5() throws InvalidQueryException {
    makeEmpty();
    update("using foo for i\"http://example.com/\" " +
           "insert foo:bar . ");

    TopicIF topic = (TopicIF) topicmap.getTopics().iterator().next();
    assertTrue("wrong number of topics after insert",
               topicmap.getTopics().size() == 1);
    LocatorIF subjid = (LocatorIF) topic.getSubjectIdentifiers().iterator().next();
    assertTrue("topic has wrong subject identifier: " + subjid,
               subjid.getAddress().equals("http://example.com/bar"));
  }

  public void testEmptyWildcard() throws InvalidQueryException {
    makeEmpty();
    update("insert ?foo . ");

    assertTrue("wrong number of topics after insert",
               topicmap.getTopics().size() == 1);
  }

  public void testEmptyInsert6() throws InvalidQueryException {
    makeEmpty();
    update("insert ^ http://example.com/test . ");

    LocatorIF iid = URILocator.create("http://example.com/test");
    TopicIF topic = (TopicIF) topicmap.getObjectByItemIdentifier(iid);
    assertTrue("couldn't find inserted topic", topic != null);
    assertTrue("wrong size of topic map after insert",
               topicmap.getTopics().size() == 1);
  }

  public void testEmptyInsert7() throws InvalidQueryException {
    makeEmpty();
    update("insert ^ <file:/example/test#foo> . ");

    LocatorIF iid = URILocator.create("file:/example/test#foo");
    TopicIF topic = (TopicIF) topicmap.getObjectByItemIdentifier(iid);
    assertTrue("couldn't find inserted topic", topic != null);
    assertTrue("wrong size of topic map after insert",
               topicmap.getTopics().size() == 1);
  }
  
  /// instance-of topic map

  public void testName() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    update("insert topic1 - \"Emne1\" .");

    TopicIF topic = getTopicById("topic1");
    assertTrue("topic did not get new name",
               topic.getTopicNames().size() == 2);
  }
  
  public void testAddOccurrence() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    update("insert $topic newocctype: \"hey\" . from $topic = topic1");

    TopicIF topic = getTopicById("topic1");
    assertTrue("topic did not get new occurrence",
               topic.getOccurrences().size() == 1);
  }

  public void testAddOccurrence2() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    update("insert $topic objid: $id . from " +
           "$topic = topic1, object-id($topic, $id)");

    TopicIF topic = getTopicById("topic1");
    assertTrue("topic did not get new occurrence",
               topic.getOccurrences().size() == 1);
  }

  public void testWildcard() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    int topicsbefore = topicmap.getTopics().size();
    update("insert $type isa ?newtype . from " +
           "$type = type1");

    TopicIF topic = getTopicById("type1");
    assertTrue("wrong number of topics after insert",
               topicmap.getTopics().size() == topicsbefore + 1);
    assertTrue("topic does not have new type after insert",
               !topic.getTypes().isEmpty());
  }

  public void testWildcard2() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    int topicsbefore = topicmap.getTopics().size();
    update("insert $type isa ?newtype . from " +
           "{ $type = type1 | $type = type2 }");

    TopicIF topic1 = getTopicById("type1");
    TopicIF topic2 = getTopicById("type2");
    assertEquals("wrong number of topics after insert",
                 topicmap.getTopics().size(), topicsbefore + 2);
    assertTrue("topic1 does not have new type after insert",
               !topic1.getTypes().isEmpty());
    assertTrue("topic2 does not have new type after insert",
               !topic2.getTypes().isEmpty());
    TopicIF type1 = (TopicIF) topic1.getTypes().iterator().next();
    TopicIF type2 = (TopicIF) topic2.getTypes().iterator().next();
    assertFalse("topics have the same type",
               type1.equals(type2));
  }

  public void testWildcard3() throws InvalidQueryException {
    makeEmpty();

    update("insert ?topic . ");

    assertTrue("topic not created?", topicmap.getTopics().size() == 1);

    update("insert ?topic . "); // should create *another* topic

    assertEquals("wildcard topics merged across queries",
                 topicmap.getTopics().size(), 2);
  }

  public void testWildcard4() throws InvalidQueryException {
    makeEmpty();

    update("insert ? . ");

    assertEquals("problem in topic creation", topicmap.getTopics().size(), 1);

    update("insert ? . "); // should create *another* topic

    assertEquals("wildcard topics merged across queries",
                 topicmap.getTopics().size(), 2);
  }

  public void testWildcard5() throws InvalidQueryException {
    makeEmpty();

    update("insert ? . ? .");

    assertEquals("problem in topic creation", topicmap.getTopics().size(), 2);
  }  

  public void testWildcard6() throws InvalidQueryException {
    makeEmpty();

    update("insert ?topic . ?topic .");

    assertEquals("problem in topic creation", topicmap.getTopics().size(), 1);
  }  
  
  public void testQName() throws InvalidQueryException, IOException {
    load("subclasses.ltm");

    int topics = topicmap.getTopics().size();

    update("using xtm for i\"http://www.topicmaps.org/xtm/1.0/core.xtm#\" " +
           "insert xtm:test . ");

    assertTrue("wrong number of topics after insert",
               topicmap.getTopics().size() == (topics + 1));

    TopicIF test = topicmap.getTopicBySubjectIdentifier(new URILocator("http://www.topicmaps.org/xtm/1.0/core.xtm#test"));
    assertTrue("no xtm:test after insert", test != null);
  }  

  public void testParam() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    TopicIF topic = getTopicById("topic1");
    Map params = makeArguments("topic", topic);
    update("insert $topic newocctype: \"hey\" . from $topic = %topic%", params);

    assertTrue("topic did not get new occurrence",
               topic.getOccurrences().size() == 1);
  }

  public void testNoBaseAddress() throws InvalidQueryException {
    makeEmpty(false); // don't set base address

    // this one is valid because there are no relative URIs
    update("insert <urn:uuid:d84e2777-8928-4bd4-a3e4-8ca835f92304> .");

    LocatorIF si;
    try {
      si = new URILocator("urn:uuid:d84e2777-8928-4bd4-a3e4-8ca835f92304");
    } catch (MalformedURLException e) {
      throw new OntopiaRuntimeException(e);
    }
    
    TopicIF topic = topicmap.getTopicBySubjectIdentifier(si);
    assertTrue("topic was not inserted", topic != null);
  }

  public void testNoBaseAddress2() throws InvalidQueryException {
    makeEmpty(false); // don't set base address

    // this one is invalid because "#topic" isn't an absolute URI
    updateError("insert topic .");
  }

  public void testIssue211() throws InvalidQueryException, IOException {
    load("JillsMusic.xtm");

    update("using on for i\"http://psi.ontopia.net/ontology/\" " +
           "insert $ATYPE - $VALUE @ $RTYPE . " +
           "from " +
           "on:has-role-type($RF : on:role-field, $RTYPE : on:role-type), " +
           "topic-name($RF, $RFN), not(type($RFN, $RFNTYPE)), " +
           "not(scope($RFN, $RFNTHEME)), value($RFN, $VALUE), " +
           "on:has-association-field($RF : on:role-field, $AF : on:association-field), " +
           "on:has-association-type($AF : on:association-field, $ATYPE : on:association-type)");
  }

  public void testTurnStringIntoURI() throws InvalidQueryException, IOException {
    makeEmpty();

    update("insert " +
           "  topic $psi . " +
           "from " +
           "  $psi = \"http://example.com\" ");

    LocatorIF psi = new URILocator("http://example.com");
    TopicIF topic = topicmap.getTopicBySubjectIdentifier(psi);
    assertTrue("topic not found by PSI", topic != null);
  }

  // tests for CTM/tolog integration

  // ===== VALID

  public void testFromParsing() throws InvalidQueryException {
    makeEmpty();
    update("insert topic isa $tt . # from \n" +
           "  from instance-of($t, $tt)");
  }  

  public void testFromParsing2() throws InvalidQueryException {
    makeEmpty();
    update("insert topic - \"Topic from CTM\" .");
  }

  public void testQNameContext() throws InvalidQueryException, IOException {
    load("subclasses.ltm");

    int topics = topicmap.getTopics().size();

    DeclarationContextIF ctxt = QueryUtils.parseDeclarations(topicmap, "using xtm for i\"http://www.topicmaps.org/xtm/1.0/core.xtm#\"");
    update("insert xtm:test . ", ctxt);

    assertTrue("wrong number of topics after insert",
               topicmap.getTopics().size() == (topics + 1));

    TopicIF test = topicmap.getTopicBySubjectIdentifier(new URILocator("http://www.topicmaps.org/xtm/1.0/core.xtm#test"));
    assertTrue("no xtm:test after insert", test != null);
  }  

  public void testIidContext() throws InvalidQueryException, IOException {
    makeEmpty();

    DeclarationContextIF ctxt = QueryUtils.parseDeclarations(topicmap, "using xtm for i\"http://www.topicmaps.org/xtm/1.0/core.xtm#\"");

    update("insert ^ http://example.com/test isa xtm:subject . ", ctxt);

    LocatorIF iid = URILocator.create("http://example.com/test");
    TopicIF topic = (TopicIF) topicmap.getObjectByItemIdentifier(iid);
    assertTrue("couldn't find inserted topic", topic != null);
    assertTrue("wrong size of topic map after insert",
               topicmap.getTopics().size() == 2);
  }  

  public void testIidContext2() throws InvalidQueryException, IOException {
    makeEmpty();

    DeclarationContextIF ctxt = QueryUtils.parseDeclarations(topicmap, "using lr for i\"http://example.com/\"");

    update(
      "insert lr:contains( lr:container : ^ <file:/foo/bar#baz> , " +
      "                    lr:containee : other )", ctxt);

    LocatorIF iid = URILocator.create("file:/foo/bar#baz");
    TopicIF topic = (TopicIF) topicmap.getObjectByItemIdentifier(iid);
    assertTrue("couldn't find inserted topic", topic != null);
    assertTrue("wrong size of topic map after insert",
               topicmap.getTopics().size() == 5);
  }  
  

// positions reported by lexer.getStartOfToken() make no sense after
// multiline comments. don't know why, and can't see any way to fix it.
// therefore disabling this test for now.
//   public void testFromParsing3() throws InvalidQueryException {
//     makeEmpty();
//     update("insert topic isa $tt . #( from )# " +
//            "  from instance-of($t, $tt)");
//   }  

  public void testFromParsing4() throws InvalidQueryException {
    makeEmpty();
    update("/* insert ... from test */ " +
           "insert topic isa $tt . " +
           "  from instance-of($t, $tt)");
  }  

  public void testFromParsing5() throws InvalidQueryException {
    makeEmpty();
    updateError("insert from isa topic .");
  }  
}
