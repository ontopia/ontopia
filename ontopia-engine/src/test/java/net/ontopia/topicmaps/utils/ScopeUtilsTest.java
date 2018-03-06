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
import java.util.function.Predicate;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.ScopedIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ScopeUtilsTest {
  protected TopicMapIF    topicmap; 
  protected TopicMapBuilderIF builder;

  @Before
  public void setUp() {
    topicmap = makeTopicMap();
    makeTopic("A");
    makeTopic("B");
  }

  // intended to be overridden
  protected TopicMapIF makeTopicMap() {
    InMemoryTopicMapStore store = new InMemoryTopicMapStore();
    builder = store.getTopicMap().getBuilder();
    return store.getTopicMap();
  }

  protected TopicIF makeTopic(String name) {
    TopicIF topic = builder.makeTopic();
    topic.addSubjectIdentifier(makeLocator("http://psi.ontopia.net/fake/" + name));
    return topic;
  }
  
  protected TopicIF getTopic(String name) {
    return topicmap.getTopicBySubjectIdentifier(makeLocator("http://psi.ontopia.net/fake/" + name));
  }
  
  public URILocator makeLocator(String uri) {
    return URILocator.create(uri);
  }

  // public void check(String deciderName, String object, String user, boolean res) {
  //   Collection userScope = makeContext(user);
  //   DeciderIF decider = null;
  //   if (deciderName == "Broad")
  //     decider = new InBroadScopeDecider(userScope);
  //   else if (deciderName == "Narrow")
  //     decider = new InNarrowScopeDecider(userScope);
  //   else if (deciderName == "Identical")
  //     decider = new InIdenticalScopeDecider(userScope);
  //   else if (deciderName == "Related")
  //     decider = new InRelatedScopeDecider(userScope);
  //   else
  //     Assert.fail("bad decider name given");
  //   
  //   Assert.assertTrue(deciderName + " decider got wrong result: " + object + "/" + user,
  //          decider.ok(makeScoped(object)) == res);
  // }

  public Collection makeContext(String spec) {
    Collection scope = new HashSet();
    String[] tokens = StringUtils.split(spec, ",");
    for (int ix = 0; ix < tokens.length; ix++) {
      String token = tokens[ix].trim();
      if (!token.isEmpty()) {
        scope.add(getTopic(token));
      }
    }
    return scope;
  }

  public ScopedIF makeScoped(String spec) {
    return new Scoped(makeContext(spec));
  }
  
  // --- Test cases
    
  // public void testScopeDeciders() {
  //   makeTopic("A");
  //   makeTopic("B");
  //   makeTopic("C");
  //   makeTopic("D");
  //   makeTopic("E");
  //   makeTopic("F");
  //   
  //   check("Broad", "     ", "", true);
  //   check("Narrow", "    ", "", true);
  //   check("Identical", " ", "", true);
  //   check("Related", "   ", "", true);
  //   check("Broad", "     ", "A", true);
  //   check("Narrow", "    ", "A", true);
  //   check("Identical", " ", "A", false);
  //   check("Related", "   ", "A", true);
  //   check("Broad", "     ", "A, B", true);
  //   check("Narrow", "    ", "A, B", true);
  //   check("Identical", " ", "A, B", false);
  //   check("Related", "   ", "A, B", true);
  //   check("Broad", "     ", "A, B, C", true);
  //   check("Narrow", "    ", "A, B, C", true);
  //   check("Identical", " ", "A, B, C", false);
  //   check("Related", "   ", "A, B, C", true);
  //   check("Broad", "     A", "", true);
  //   check("Narrow", "    A", "", true);
  //   check("Identical", " A", "", false);
  //   check("Related", "   A", "", true);
  //   check("Broad", "     A", "A", true);
  //   check("Narrow", "    A", "A", true);
  //   check("Identical", " A", "A", true);
  //   check("Related", "   A", "A", true);
  //   check("Broad", "     A", "A, B", false);
  //   check("Narrow", "    A", "A, B", true);
  //   check("Identical", " A", "A, B", false);
  //   check("Related", "   A", "A, B", true);
  //   check("Broad", "     A", "A, B, C", false);
  //   check("Narrow", "    A", "A, B, C", true);
  //   check("Identical", " A", "A, B, C", false);
  //   check("Related", "   A", "A, B, C", true);
  //   check("Broad", "     A, B", "", true);
  //   check("Narrow", "    A, B", "", true);
  //   check("Identical", " A, B", "", false);
  //   check("Related", "   A, B", "", true);
  //   check("Broad", "     A, B", "A", true);
  //   check("Narrow", "    A, B", "A", false);
  //   check("Identical", " A, B", "A", false);
  //   check("Related", "   A, B", "A", true);
  //   check("Broad", "     A, B", "A, B", true);
  //   check("Narrow", "    A, B", "A, B", true);
  //   check("Identical", " A, B", "A, B", true);
  //   check("Related", "   A, B", "A, B", true);
  //   check("Broad", "     A, B", "A, B, C", false);
  //   check("Narrow", "    A, B", "A, B, C", true);
  //   check("Identical", " A, B", "A, B, C", false);
  //   check("Related", "   A, B", "A, B, C", true);
  //   check("Broad", "     A, B, C", "", true);
  //   check("Narrow", "    A, B, C", "", true);
  //   check("Identical", " A, B, C", "", false);
  //   check("Related", "   A, B, C", "", true);
  //   check("Broad", "     A, B, C", "A", true);
  //   check("Narrow", "    A, B, C", "A", false);
  //   check("Identical", " A, B, C", "A", false);
  //   check("Related", "   A, B, C", "A", true);
  //   check("Broad", "     A, B, C", "A, B", true);
  //   check("Narrow", "    A, B, C", "A, B", false);
  //   check("Identical", " A, B, C", "A, B", false);
  //   check("Related", "   A, B, C", "A, B", true);
  //   check("Broad", "     A, B, C", "A, B, C", true);
  //   check("Narrow", "    A, B, C", "A, B, C", true);
  //   check("Identical", " A, B, C", "A, B, C", true);
  //   check("Related", "   A, B, C", "A, B, C", true);
  //   check("Broad", "     ", "", true);
  //   check("Narrow", "    ", "", true);
  //   check("Identical", " ", "", true);
  //   check("Related", "   ", "", true);
  //   check("Broad", "     ", "D", true);
  //   check("Narrow", "    ", "D", true);
  //   check("Identical", " ", "D", false);
  //   check("Related", "   ", "D", true);
  //   check("Broad", "     ", "D, E", true);
  //   check("Narrow", "    ", "D, E", true);
  //   check("Identical", " ", "D, E", false);
  //   check("Related", "   ", "D, E", true);
  //   check("Broad", "     ", "D, E, F", true);
  //   check("Narrow", "    ", "D, E, F", true);
  //   check("Identical", " ", "D, E, F", false);
  //   check("Related", "   ", "D, E, F", true);
  //   check("Broad", "     A", "", true);
  //   check("Narrow", "    A", "", true);
  //   check("Identical", " A", "", false);
  //   check("Related", "   A", "", true);
  //   check("Broad", "     A", "D", false);
  //   check("Narrow", "    A", "D", false);
  //   check("Identical", " A", "D", false);
  //   check("Related", "   A", "D", false);
  //   check("Broad", "     A", "D, E", false);
  //   check("Narrow", "    A", "D, E", false);
  //   check("Identical", " A", "D, E", false);
  //   check("Related", "   A", "D, E", false);
  //   check("Broad", "     A", "D, E, F", false);
  //   check("Narrow", "    A", "D, E, F", false);
  //   check("Identical", " A", "D, E, F", false);
  //   check("Related", "   A", "D, E, F", false);
  //   check("Broad", "     A, B", "", true);
  //   check("Narrow", "    A, B", "", true);
  //   check("Identical", " A, B", "", false);
  //   check("Related", "   A, B", "", true);
  //   check("Broad", "     A, B", "D", false);
  //   check("Narrow", "    A, B", "D", false);
  //   check("Identical", " A, B", "D", false);
  //   check("Related", "   A, B", "D", false);
  //   check("Broad", "     A, B", "D, E", false);
  //   check("Narrow", "    A, B", "D, E", false);
  //   check("Identical", " A, B", "D, E", false);
  //   check("Related", "   A, B", "D, E", false);
  //   check("Broad", "     A, B", "D, E, F", false);
  //   check("Narrow", "    A, B", "D, E, F", false);
  //   check("Identical", " A, B", "D, E, F", false);
  //   check("Related", "   A, B", "D, E, F", false);
  //   check("Broad", "     A, B, C", "", true);
  //   check("Narrow", "    A, B, C", "", true);
  //   check("Identical", " A, B, C", "", false);
  //   check("Related", "   A, B, C", "", true);
  //   check("Broad", "     A, B, C", "D", false);
  //   check("Narrow", "    A, B, C", "D", false);
  //   check("Identical", " A, B, C", "D", false);
  //   check("Related", "   A, B, C", "D", false);
  //   check("Broad", "     A, B, C", "D, E", false);
  //   check("Narrow", "    A, B, C", "D, E", false);
  //   check("Identical", " A, B, C", "D, E", false);
  //   check("Related", "   A, B, C", "D, E", false);
  //   check("Broad", "     A, B, C", "D, E, F", false);
  //   check("Narrow", "    A, B, C", "D, E, F", false);
  //   check("Identical", " A, B, C", "D, E, F", false);
  //   check("Related", "   A, B, C", "D, E, F", false);
  // }

  public void assertApplicableInContextDecider(String scope, String context, boolean res) {
    Predicate decider = new ApplicableInContextDecider(makeContext(context));
    Assert.assertTrue(decider + " decider got wrong result: " + scope + "/" + context,
           decider.test(makeScoped(scope)) == res);    
  }
  public void assertSuperOfContextDecider(String scope, String context, boolean res) {
    Predicate decider = new SupersetOfContextDecider(makeContext(context));
    Assert.assertTrue(decider + " decider got wrong result: " + scope + "/" + context,
           decider.test(makeScoped(scope)) == res);
  }
  public void assertSubOfContextDecider(String scope, String context, boolean res) {
    Predicate decider = new SubsetOfContextDecider(makeContext(context));
    Assert.assertTrue(decider + " decider got wrong result: " + scope + "/" + context,
           decider.test(makeScoped(scope)) == res);
  }
  public void assertIntersectionOfContextDecider(String scope, String context, boolean res) {
    Predicate decider = new IntersectionOfContextDecider(makeContext(context));
    Assert.assertTrue(decider + " decider got wrong result: " + scope + "/" + context,
           decider.test(makeScoped(scope)) == res);
  }

  @Test
  public void testApplicableInContextDecider() {
    assertApplicableInContextDecider("", "", true);
    assertApplicableInContextDecider("", "A", true);
    assertApplicableInContextDecider("", "A, B", true);
    assertApplicableInContextDecider("A", "", true);
    assertApplicableInContextDecider("A", "A", true);
    assertApplicableInContextDecider("A", "A, B", false);
    assertApplicableInContextDecider("A, B", "", true);
    assertApplicableInContextDecider("A, B", "A", true);
    assertApplicableInContextDecider("A, B", "A, B", true);
  }
  
  @Test
  public void testSuperOfContextDecider() {
    assertSuperOfContextDecider("", "", true);
    assertSuperOfContextDecider("", "A", false);
    assertSuperOfContextDecider("", "A, B", false);
    assertSuperOfContextDecider("A", "", true);
    assertSuperOfContextDecider("A", "A", true);
    assertSuperOfContextDecider("A", "A, B", false);
    assertSuperOfContextDecider("A, B", "", true);
    assertSuperOfContextDecider("A, B", "A", true);
    assertSuperOfContextDecider("A, B", "A, B", true);
  }
  
  @Test
  public void testSubOfContextDecider() {
    assertSubOfContextDecider("", "", true);
    assertSubOfContextDecider("", "A", true);
    assertSubOfContextDecider("", "A, B", true);
    assertSubOfContextDecider("A", "", false);
    assertSubOfContextDecider("A", "A", true);
    assertSubOfContextDecider("A", "A, B", true);
    assertSubOfContextDecider("A, B", "", false);
    assertSubOfContextDecider("A, B", "A", false);
    assertSubOfContextDecider("A, B", "A, B", true);
  }
  
  @Test
  public void testIntersectionOfContextDecider() {
    assertIntersectionOfContextDecider("", "", false);
    assertIntersectionOfContextDecider("", "A", false);
    assertIntersectionOfContextDecider("", "A, B", false);
    assertIntersectionOfContextDecider("A", "", false);
    assertIntersectionOfContextDecider("A", "A", true);
    assertIntersectionOfContextDecider("A", "A, B", true);
    assertIntersectionOfContextDecider("A, B", "", false);
    assertIntersectionOfContextDecider("A, B", "A", true);
    assertIntersectionOfContextDecider("A, B", "A, B", true);
  }
  
  // --- Helper classes

  public static class Scoped implements ScopedIF {
    private Collection scope;
    Scoped(Collection scope) {
      this.scope = scope;
    }
    @Override
    public Collection getScope() {
      return scope;
    }
    @Override
    public void addTheme(TopicIF theme) {
      scope.add(theme);
    }
    @Override
    public void removeTheme(TopicIF theme) {
      scope.remove(theme);
    }   
    @Override
    public String getObjectId() {return "";}
    @Override
    public boolean isReadOnly() {return false;}
    @Override
    public TopicMapIF getTopicMap() {return null;}
    @Override
    public Collection getItemIdentifiers() {return Collections.EMPTY_SET;}
    @Override
    public void addItemIdentifier(LocatorIF source_locator) { /* no-op */ }  
    @Override
    public void removeItemIdentifier(LocatorIF source_locator) { /* no-op */ }
    @Override
    public void remove() { /* no-op */ }
      
  }
  
}
