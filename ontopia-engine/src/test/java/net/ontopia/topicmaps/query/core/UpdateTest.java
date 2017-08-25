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
import java.util.HashMap;
import java.io.IOException;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.DataTypes;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.OccurrenceIF;

// FIXME: value() with three parameters
// FIXME: URLs which aren't really

public class UpdateTest extends AbstractQueryTest {
  
  public UpdateTest(String name) {
    super(name);
  }

  /// context management

  public void tearDown() {
    closeStore();
  }

  /// empty topic map
  
  public void testEmptyUpdate() throws InvalidQueryException {
    makeEmpty();
    update("update value($TN, \"foo\") from topic-name($T, $TN)");
  }

  /// instance-of topic map

  public void testStaticNameChange() throws InvalidQueryException, IOException {
    load("jill.xtm");

    TopicNameIF name = (TopicNameIF) getObjectById("jills-name");
    
    update("update value(jills-name, \"Jill R. Hacker\")");

    assertTrue("name not changed after update",
               name.getValue().equals("Jill R. Hacker"));
  }
  
  public void testDynamicNameChange() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    TopicIF topic1 = getTopicById("topic1");
    TopicNameIF name = topic1.getTopicNames().iterator().next();
    
    update("update value($N, \"TOPIC1\") from topic-name(topic1, $N)");

    assertTrue("name not changed after update",
               name.getValue().equals("TOPIC1"));
  }

  public void testStaticOccChange() throws InvalidQueryException, IOException {
    load("jill.xtm");

    OccurrenceIF occ = (OccurrenceIF) getObjectById("jills-contract");
    
    update("update value(jills-contract, \"No such contract\")");

    assertTrue("occurrence not changed after update",
               occ.getValue().equals("No such contract"));
    assertTrue("incorrect datatype after update",
               occ.getDataType().equals(DataTypes.TYPE_STRING));
  }
  
  public void testDynamicOccChange() throws InvalidQueryException, IOException {
    load("jill.xtm");

    OccurrenceIF occ = (OccurrenceIF) getObjectById("jills-contract");
    
    update("update value($C, \"No such contract\") from type($C, contract)");

    assertTrue("occurrence not changed after update",
               occ.getValue().equals("No such contract"));
    assertTrue("incorrect datatype after update",
               occ.getDataType().equals(DataTypes.TYPE_STRING));
  }

  public void testStaticResource() throws InvalidQueryException, IOException {
    load("jill.xtm");

    OccurrenceIF occ = (OccurrenceIF) getObjectById("jills-contract");
    
    update("update resource(jills-contract, \"http://example.com\")");

    assertTrue("occurrence not changed after update: " + occ.getLocator(),
               occ.getLocator().getAddress().equals("http://example.com/"));
    assertTrue("incorrect datatype after update",
               occ.getDataType().equals(DataTypes.TYPE_URI));
  }

  public void testDynamicResource() throws InvalidQueryException, IOException {
    load("jill.xtm");

    OccurrenceIF occ = (OccurrenceIF) getObjectById("jills-contract");
    
    update("update resource($C, \"http://example.com\") " +
           "from type($C, contract)");

    assertTrue("occurrence not changed after update: " + occ.getLocator(),
               occ.getLocator().getAddress().equals("http://example.com/"));
    assertTrue("incorrect datatype after update",
               occ.getDataType().equals(DataTypes.TYPE_URI));
  }

  public void testParam() throws InvalidQueryException, IOException {
    load("subclasses.ltm");

    TopicIF subclass = getTopicById("subclass");
    TopicNameIF name = subclass.getTopicNames().iterator().next();
    Map params = makeArguments("name", name);

    update("update value(%name%, \"SUBCLASS\")", params);

    assertTrue("name value not changed",
               name.getValue().equals("SUBCLASS"));
  }  

  public void testParam2() throws InvalidQueryException, IOException {
    load("subclasses.ltm");

    TopicIF subclass = getTopicById("subclass");
    TopicNameIF name = subclass.getTopicNames().iterator().next();
    Map params = new HashMap();
    params.put("v", "SUBCLASS");

    update("update value(@" + name.getObjectId() + ", %v%)", params);

    assertTrue("name value not changed",
               name.getValue().equals("SUBCLASS"));
  }

  public void testParam3() throws InvalidQueryException, IOException {
    load("subclasses.ltm");

    TopicIF subclass = getTopicById("subclass");
    TopicNameIF name = subclass.getTopicNames().iterator().next();
    Map params = makeArguments("name", name);

    update("update value($N, \"SUBCLASS\") from $N = %name%", params);

    assertTrue("name value not changed",
               name.getValue().equals("SUBCLASS"));
  }
  
  /// error tests

  public void testNotAString() throws InvalidQueryException, IOException {
    load("jill.xtm");
    updateError("update value(jills-contract, 5)");
  }

  public void testNotAString2() throws InvalidQueryException, IOException {
    load("jill.xtm");
    updateError("update value(jills-contract, jill)");
  }

  public void testHasNoValue() throws InvalidQueryException, IOException {
    load("jill.xtm");
    updateError("update value(jill, \"foo\")");
  }
}
