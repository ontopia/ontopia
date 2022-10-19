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

package net.ontopia.topicmaps.utils;

import java.util.Collection;
import java.util.Set;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AssociationWalkerTest extends AbstractUtilsTestCase {
  protected TopicIF bart, homer, marge, gramps, lisa, maggie;
  protected TopicIF at_descendant_of, rt_ancestor, rt_descendant;

  @Test
  public void testSimpleWalker() {
    readFile("transitive2.xtm");

    TopicIF at_greater_than = getTopic("at-greaterThan");
    TopicIF rt_smaller = getTopic("ar-smaller");
    TopicIF rt_larger  = getTopic("ar-larger");
    TopicIF one = getTopic("num1");
    TopicIF two = getTopic("num2");
    TopicIF three = getTopic("num3");
    AssociationWalker twalker = new AssociationWalker(at_greater_than, rt_smaller, rt_larger);
    Set topics = twalker.walkTopics(one);
    Collection paths = twalker.walkPaths(one);
            
    Assert.assertTrue(topics.size() == 2);
    Assert.assertTrue(paths.size() == 1);
            
    topics = twalker.walkTopics(two);
    Assert.assertTrue(topics.size() == 1);

    topics = twalker.walkTopics(three);
    Assert.assertTrue(topics.isEmpty());
  }

  @Before
  public void setUp() {
    TopicMapStoreIF store = new InMemoryTopicMapStore();
    tm = store.getTopicMap();
    TopicMapBuilderIF builder = tm.getBuilder();
    bart = builder.makeTopic();
    homer = builder.makeTopic();
    gramps = builder.makeTopic();

    at_descendant_of = builder.makeTopic();
    rt_ancestor = builder.makeTopic();
    rt_descendant = builder.makeTopic();

    AssociationIF relation;
        
    relation = builder.makeAssociation(at_descendant_of);
    builder.makeAssociationRole(relation, rt_ancestor, homer);
    builder.makeAssociationRole(relation, rt_descendant, bart);
        
    relation = builder.makeAssociation(at_descendant_of);
    builder.makeAssociationRole(relation, rt_ancestor, gramps);
    builder.makeAssociationRole(relation, rt_descendant, homer);
  }
    
  @Test
  public void testWalker() {
    AssociationWalker twalker = new AssociationWalker(at_descendant_of, rt_descendant, rt_ancestor);
    Set ancestors = twalker.walkTopics(bart);
    Assert.assertTrue("Expecting 2 members of association set. Got: " + String.valueOf(ancestors.size()),
           ancestors.size() == 2);
  }

  @Test
  public void testSimpsons() {
    readFile("transitive3.xtm");
    TopicIF descendant_of = getTopic("descendant-of");
    TopicIF ancestor = getTopic("ancestor");
    TopicIF descendant = getTopic("descendant");

    AssociationWalker ancestorsWalker = new AssociationWalker(descendant_of, descendant, ancestor);
    new AssociationWalker(descendant_of, ancestor, descendant);

    TopicIF bart = getTopic("bart");
    TopicIF lisa = getTopic("lisa");
    TopicIF homer = getTopic("homer");
    TopicIF gramps = getTopic("gramps");
    TopicIF great_grandaddy = getTopic("great-grandaddy-simpson");
        
    Set topics = ancestorsWalker.walkTopics(getTopic("bart"));
    // System.out.println("Topics: " + topics);
    Assert.assertTrue(topics.size() == 4);
    Assert.assertTrue("Contains Homer", topics.contains(getTopic("homer")));
    Assert.assertTrue("Contains Marge", topics.contains(getTopic("marge")));
    Assert.assertTrue("Contains Gramps", topics.contains(getTopic("gramps")));
    Assert.assertTrue("Contains Great Granddaddy", topics.contains(getTopic("great-grandaddy-simpson")));
        
    Collection paths = ancestorsWalker.walkPaths(getTopic("bart"));
    // System.out.println("Paths: " + paths);
    Assert.assertTrue(paths.size() == 2);
    Assert.assertTrue(!ancestorsWalker.isAssociated(bart, lisa));
    Assert.assertTrue(ancestorsWalker.isAssociated(bart, homer));
    Assert.assertTrue(ancestorsWalker.isAssociated(bart, gramps));
    Assert.assertTrue(ancestorsWalker.isAssociated(bart, great_grandaddy));
  }
}
