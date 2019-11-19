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

package net.ontopia.topicmaps.core;

import org.junit.Assert;
import org.junit.Test;

public abstract class AbstractScopedTest extends AbstractTMObjectTest {
  protected ScopedIF scoped;
  
  // --- Test cases

  @Test
  public void testScope() {
    // STATE 1: empty scope
    Assert.assertTrue("scope initially not empty", scoped.getScope().size() == 0);
    
    TopicIF theme = builder.makeTopic();
    scoped.removeTheme(theme); // just checking that this works, is all
        
    // STATE 2: one topic in scope
    TopicIF topic = builder.makeTopic();
    scoped.addTheme(topic);
    Assert.assertTrue("theme not added", scoped.getScope().size() == 1);

    scoped.addTheme(topic);
    Assert.assertTrue("duplicate not rejected", scoped.getScope().size() == 1);

    // STATE 3: empty scope again
    scoped.removeTheme(topic);
    Assert.assertTrue("theme not removed", scoped.getScope().size() == 0);

    scoped.removeTheme(topic); // removing theme that is not present
  }

  /**
   * Verify that methods handle null arguments the way they should.
   */
  @Test
  public void testNullScopedArguments() {
    try {
      scoped.addTheme(null);
      Assert.fail("was allowed to add null theme");
    } catch (NullPointerException e) {
      // expected
    }

    try {
      scoped.removeTheme(null);
      Assert.fail("was allowed to remove null theme");
    } catch (NullPointerException e) {
      // expected
    }
  }

}
