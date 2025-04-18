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
import java.util.HashMap;
import java.util.Map;
import net.ontopia.topicmaps.core.DataTypes;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import org.junit.Assert;
import org.junit.Test;

// FIXME: value() with three parameters
// FIXME: URLs which aren't really

public class UpdateTest extends AbstractQueryTest {
  
  /// empty topic map
  
  @Test
  public void testEmptyUpdate() throws InvalidQueryException {
    makeEmpty();
    assertUpdate("update value($TN, \"foo\") from topic-name($T, $TN)");
  }

  /// instance-of topic map

  @Test
  public void testStaticNameChange() throws InvalidQueryException, IOException {
    load("jill.xtm");

    TopicNameIF name = (TopicNameIF) getObjectById("jills-name");
    
    assertUpdate("update value(jills-name, \"Jill R. Hacker\")");

    Assert.assertTrue("name not changed after update",
               name.getValue().equals("Jill R. Hacker"));
  }
  
  @Test
  public void testDynamicNameChange() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    TopicIF topic1 = getTopicById("topic1");
    TopicNameIF name = topic1.getTopicNames().iterator().next();
    
    assertUpdate("update value($N, \"TOPIC1\") from topic-name(topic1, $N)");

    Assert.assertTrue("name not changed after update",
               name.getValue().equals("TOPIC1"));
  }

  @Test
  public void testStaticOccChange() throws InvalidQueryException, IOException {
    load("jill.xtm");

    OccurrenceIF occ = (OccurrenceIF) getObjectById("jills-contract");
    
    assertUpdate("update value(jills-contract, \"No such contract\")");

    Assert.assertTrue("occurrence not changed after update",
               occ.getValue().equals("No such contract"));
    Assert.assertTrue("incorrect datatype after update",
               occ.getDataType().equals(DataTypes.TYPE_STRING));
  }
  
  @Test
  public void testDynamicOccChange() throws InvalidQueryException, IOException {
    load("jill.xtm");

    OccurrenceIF occ = (OccurrenceIF) getObjectById("jills-contract");
    
    assertUpdate("update value($C, \"No such contract\") from type($C, contract)");

    Assert.assertTrue("occurrence not changed after update",
               occ.getValue().equals("No such contract"));
    Assert.assertTrue("incorrect datatype after update",
               occ.getDataType().equals(DataTypes.TYPE_STRING));
  }

  @Test
  public void testStaticResource() throws InvalidQueryException, IOException {
    load("jill.xtm");

    OccurrenceIF occ = (OccurrenceIF) getObjectById("jills-contract");
    
    assertUpdate("update resource(jills-contract, \"http://example.com\")");

    Assert.assertTrue("occurrence not changed after update: " + occ.getLocator(),
               occ.getLocator().getAddress().equals("http://example.com"));
    Assert.assertTrue("incorrect datatype after update",
               occ.getDataType().equals(DataTypes.TYPE_URI));
  }

  @Test
  public void testDynamicResource() throws InvalidQueryException, IOException {
    load("jill.xtm");

    OccurrenceIF occ = (OccurrenceIF) getObjectById("jills-contract");
    
    assertUpdate("update resource($C, \"http://example.com\") " +
           "from type($C, contract)");

    Assert.assertTrue("occurrence not changed after update: " + occ.getLocator(),
               occ.getLocator().getAddress().equals("http://example.com"));
    Assert.assertTrue("incorrect datatype after update",
               occ.getDataType().equals(DataTypes.TYPE_URI));
  }

  @Test
  public void testParam() throws InvalidQueryException, IOException {
    load("subclasses.ltm");

    TopicIF subclass = getTopicById("subclass");
    TopicNameIF name = subclass.getTopicNames().iterator().next();
    Map params = makeArguments("name", name);

    assertUpdate("update value(%name%, \"SUBCLASS\")", params);

    Assert.assertTrue("name value not changed",
               name.getValue().equals("SUBCLASS"));
  }  

  @Test
  public void testParam2() throws InvalidQueryException, IOException {
    load("subclasses.ltm");

    TopicIF subclass = getTopicById("subclass");
    TopicNameIF name = subclass.getTopicNames().iterator().next();
    Map params = new HashMap();
    params.put("v", "SUBCLASS");

    assertUpdate("update value(@" + name.getObjectId() + ", %v%)", params);

    Assert.assertTrue("name value not changed",
               name.getValue().equals("SUBCLASS"));
  }

  @Test
  public void testParam3() throws InvalidQueryException, IOException {
    load("subclasses.ltm");

    TopicIF subclass = getTopicById("subclass");
    TopicNameIF name = subclass.getTopicNames().iterator().next();
    Map params = makeArguments("name", name);

    assertUpdate("update value($N, \"SUBCLASS\") from $N = %name%", params);

    Assert.assertTrue("name value not changed",
               name.getValue().equals("SUBCLASS"));
  }
  
  /// error tests

  @Test
  public void testNotAString() throws InvalidQueryException, IOException {
    load("jill.xtm");
    assertUpdateError("update value(jills-contract, 5)");
  }

  @Test
  public void testNotAString2() throws InvalidQueryException, IOException {
    load("jill.xtm");
    assertUpdateError("update value(jills-contract, jill)");
  }

  @Test
  public void testHasNoValue() throws InvalidQueryException, IOException {
    load("jill.xtm");
    assertUpdateError("update value(jill, \"foo\")");
  }
}
