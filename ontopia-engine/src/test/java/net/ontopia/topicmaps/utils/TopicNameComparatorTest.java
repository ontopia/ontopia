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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TopicNameComparatorTest {
  
  private TopicMapIF topicmap;
  private TopicIF topic;
  
  private TopicNameIF untyped;
  private TopicNameIF untyped_scoped;
  private TopicNameIF typed;
  private TopicNameIF typed_scoped;
  
  private TopicIF scope;
  
  @Before
  public void setUp() {
    
    topicmap = new InMemoryTopicMapStore().getTopicMap();
    TopicMapBuilderIF builder = topicmap.getBuilder();
    
    topic = builder.makeTopic();
    TopicIF type = builder.makeTopic();
    scope = builder.makeTopic();
    
    untyped = builder.makeTopicName(topic, "Untyped name");
    typed = builder.makeTopicName(topic, type, "Typed");
    
    untyped_scoped = builder.makeTopicName(topic, "Untyped, scoped");
    typed_scoped = builder.makeTopicName(topic, type, "Typed, scoped");
    
    untyped_scoped.addTheme(scope);
    typed_scoped.addTheme(scope);
  }
  
  @Test
  public void testIssue307NoScope() {
    
    TopicNameComparator noScopeComp = new TopicNameComparator(new ArrayList());
    
    List<TopicNameIF> names = new ArrayList<TopicNameIF>();
    // add in wrong order
    names.add(typed_scoped);
    names.add(typed);
    names.add(untyped_scoped);
    names.add(untyped);
    
    Collections.sort(names, noScopeComp);
    
    TopicNameIF[] expected = new TopicNameIF[] { untyped, untyped_scoped, typed, typed_scoped };
    Assert.assertArrayEquals("Incorrect unscoped name ordering", expected, names.toArray());
  }
  
  @Test
  public void testIssue307Scoped() {
    
    TopicNameComparator noScopeComp = new TopicNameComparator(Collections.singletonList(scope));
    
    List<TopicNameIF> names = new ArrayList<TopicNameIF>();
    // add in wrong order
    names.add(typed_scoped);
    names.add(typed);
    names.add(untyped_scoped);
    names.add(untyped);
    
    Collections.sort(names, noScopeComp);
    
    TopicNameIF[] expected = new TopicNameIF[] { untyped_scoped, untyped, typed_scoped, typed };
    Assert.assertArrayEquals("Incorrect scoped name ordering", expected, names.toArray());
  }

  @Test
  public void testIssue439() {
    TopicMapIF tm = new InMemoryTopicMapStore().getTopicMap();
    TopicIF topic = tm.getBuilder().makeTopic();
    TopicNameIF name1 = tm.getBuilder().makeTopicName(topic, topic, "value1");
    TopicNameIF name2 = tm.getBuilder().makeTopicName(topic, topic, "value2");
    Assert.assertEquals(-1, new TopicNameComparator(Collections.EMPTY_LIST).compare(name1, name2));
  }
}
