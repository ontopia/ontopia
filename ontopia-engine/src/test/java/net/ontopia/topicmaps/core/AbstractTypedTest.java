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

public abstract class AbstractTypedTest extends AbstractTMObjectTest {
  protected TypedIF typed;
  
  public AbstractTypedTest(String name) {
    super(name);
  }

    // --- Test cases

    public void testType() {
        assertTrue("type null initially", typed.getType() != null);

        TopicIF type = builder.makeTopic();
        typed.setType(type);
        assertTrue("type identity not retained", typed.getType().equals(type));

				try {
					typed.setType(null);
					fail("type could be set to null");
				} catch (NullPointerException e) {
				}
    }

}





