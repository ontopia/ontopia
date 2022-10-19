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

import net.ontopia.topicmaps.utils.PSI;
import org.junit.Assert;
import org.junit.Test;

// PLEASE NOTE: this is a duplicate of AbstractTypedTest, but with
// AbstractScopedTest as a base.

public abstract class AbstractTypedScopedTest extends AbstractScopedTest {
  protected TypedIF typed;

  // --- Test cases

  @Test
  public void testType() {
    if (typed instanceof TopicNameIF) {
      TopicMapIF tm = typed.getTopicMap();
      TopicIF defaultNameType = tm.getTopicBySubjectIdentifier(PSI
          .getSAMNameType());
      Assert.assertTrue("type not equal to the default name type initially", typed
          .getType().equals(defaultNameType));
    } else {
      Assert.assertTrue("type null initially", typed.getType() != null);
    }

    TopicIF type = builder.makeTopic();
    typed.setType(type);
    Assert.assertTrue("type identity not retained", typed.getType().equals(type));

    if (typed instanceof TopicNameIF) {
      typed.setType(null);
      TopicMapIF tm = typed.getTopicMap();
      TopicIF defaultNameType = tm.getTopicBySubjectIdentifier(PSI
          .getSAMNameType());
      Assert.assertTrue("type is not equal to the default name type when set to null",
          typed.getType().equals(defaultNameType));
    } else {
      try {
        typed.setType(null);
        Assert.fail("type could be set to null");
      } catch (NullPointerException e) {
      }
      Assert.assertTrue("type identity not retained", typed.getType().equals(type));
    }
  }
}
