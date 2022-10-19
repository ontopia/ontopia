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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TopicStringifiersTest {

  protected TopicMapIF        topicmap; 
  protected TopicIF           topic; 
  protected TopicMapBuilderIF builder;
  protected Collection        empty;

  @Before
  public void setUp() {
    topicmap = makeTopicMap();
    topic = builder.makeTopic();
    empty = Collections.EMPTY_SET;
  }
    
  protected TopicMapIF makeTopicMap() {
    InMemoryTopicMapStore store = new InMemoryTopicMapStore();
    builder = store.getTopicMap().getBuilder();
    return store.getTopicMap();
  }
 
  // --- Test cases

  // getTopicNameStringifier

  @Test
  public void testBNSEmpty() {
    Function sf = TopicStringifiers.getTopicNameStringifier(empty);
    Assert.assertTrue("Stringifying topic with no names did not give \"[No name]\"",
           sf.apply(topic).equals("[No name]"));
  }
        
  @Test
  public void testBNSSingle() {
    Function sf = TopicStringifiers.getTopicNameStringifier(empty);
    builder.makeTopicName(topic, "Name");
                
    Assert.assertTrue("Stringifying topic with one name did not give that name",
           sf.apply(topic).equals("Name"));
  }

  @Test
  public void testBNSDouble1() {
    TopicIF theme = builder.makeTopic();

    builder.makeTopicName(topic, "Name1");
    TopicNameIF bn2 = builder.makeTopicName(topic, "Name2");
    bn2.addTheme(theme);

    Set scope = new HashSet();
    scope.add(theme);
    Function sf = TopicStringifiers.getTopicNameStringifier(scope);
                
    Assert.assertTrue("Stringifying topic gave wrong name",
           sf.apply(topic).equals("Name2"));
  }

  @Test
  public void testBNSDouble2() {
    TopicIF theme = builder.makeTopic();

    builder.makeTopicName(topic, "Name1");
    TopicNameIF bn2 = builder.makeTopicName(topic, "Name2");
    bn2.addTheme(theme);

    Function sf = TopicStringifiers.getTopicNameStringifier(empty);
                
    Assert.assertTrue("Stringifying topic gave wrong name",
           sf.apply(topic).equals("Name1"));
  }

  // getDefaultStringifier

  @Test
  public void testDSEmpty() {
    Assert.assertTrue("Stringifying topic with no names did not give \"[No name]\"",
           TopicStringifiers.toString(topic).equals("[No name]"));
  }
        
  @Test
  public void testDSSingle() {
    builder.makeTopicName(topic, "Name");
                
    Assert.assertTrue("Stringifying topic with one name did not give that name",
           TopicStringifiers.toString(topic).equals("Name"));
  }

  @Test
  public void testDSComplex() {
    TopicIF theme = builder.makeTopic();

    builder.makeTopicName(topic, "Name1");
    TopicNameIF bn2 = builder.makeTopicName(topic, "Name2");
    bn2.addTheme(theme);

    Assert.assertTrue("Stringifying topic gave wrong name",
           TopicStringifiers.toString(topic).equals("Name1"));
  }

  @Test
  public void testDSDisplay() {
    TopicIF display = builder.makeTopic();
    display.addSubjectIdentifier(PSI.getXTMDisplay());

    TopicNameIF bn1 = builder.makeTopicName(topic, "Name1");
    VariantNameIF vn1 = builder.makeVariantName(bn1, "Display name", Collections.emptySet());
    vn1.addTheme(display);
    builder.makeTopicName(topic, "Name2");
    builder.makeTopicName(topic, "Name3");
    builder.makeVariantName(bn1, "Blecch", Collections.emptySet());

    Assert.assertTrue("Stringifying topic gave wrong display name",
           TopicStringifiers.toString(topic).equals("Display name"));
  }

  @Test
  public void testDSNull() {
    String result = TopicStringifiers.toString(null);
    String wanted = "[No name]";
    
    Assert.assertTrue("Stringifying null gave '" + result + "' instead of '" +
               wanted + "'", wanted.equals(TopicStringifiers.toString(null)));
  }
  
  // getSortNameStringifier

  @Test
  public void testSSEmpty() {
    Function sf = TopicStringifiers.getSortNameStringifier();
    Assert.assertTrue("Stringifying topic with no names did not give \"[No name]\"",
           sf.apply(topic).equals("[No name]"));
  }
        
  @Test
  public void testSSSingle() {
    Function sf = TopicStringifiers.getSortNameStringifier();
    builder.makeTopicName(topic, "Name");
                
    Assert.assertTrue("Stringifying topic with one name did not give that name",
           sf.apply(topic).equals("Name"));
  }

  @Test
  public void testSSComplex() {
    TopicIF theme = builder.makeTopic();

    builder.makeTopicName(topic, "Name1");
    TopicNameIF bn2 = builder.makeTopicName(topic, "Name2");
    bn2.addTheme(theme);

    Function sf = TopicStringifiers.getSortNameStringifier();
                
    Assert.assertTrue("Stringifying topic gave wrong name",
           sf.apply(topic).equals("Name1"));
  }

  @Test
  public void testSSSort() {
    TopicIF sort = builder.makeTopic();
    sort.addSubjectIdentifier(PSI.getXTMSort());

    TopicNameIF bn1 = builder.makeTopicName(topic, "Name1");
    VariantNameIF vn1 = builder.makeVariantName(bn1, "Sort name", Collections.emptySet());
    vn1.addTheme(sort);
    builder.makeTopicName(topic, "Name2");
    builder.makeTopicName(topic, "Name3");
    builder.makeVariantName(bn1, "Blecch", Collections.emptySet());

    Function sf = TopicStringifiers.getSortNameStringifier();
    Assert.assertTrue("Stringifying topic gave wrong sort name",
           sf.apply(topic).equals("Sort name"));
  }

  @Test
  public void testVariantName() {
    TopicIF sort = builder.makeTopic();
    sort.addSubjectIdentifier(PSI.getXTMSort());

    TopicNameIF bn1 = builder.makeTopicName(topic, "Name1");
    VariantNameIF vn1 = builder.makeVariantName(bn1, "Sort name", Collections.emptySet());
    vn1.addTheme(sort);
    builder.makeTopicName(topic, "Name2");
    builder.makeTopicName(topic, "Name3");
    builder.makeVariantName(bn1, "Blecch", Collections.emptySet());

    Function sf =
      TopicStringifiers.getVariantNameStringifier(Collections.singleton(sort));
    Assert.assertTrue("Stringifying topic gave wrong variant name",
           sf.apply(topic).equals("Sort name"));
  }
}
