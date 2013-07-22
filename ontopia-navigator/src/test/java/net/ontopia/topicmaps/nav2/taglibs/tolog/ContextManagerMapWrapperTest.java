/*
 * #!
 * Ontopia Navigator
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
package net.ontopia.topicmaps.nav2.taglibs.tolog;

import java.util.Collection;
import net.ontopia.topicmaps.nav2.impl.basic.ContextManager;
import net.ontopia.topicmaps.nav2.taglibs.tolog.ContextManagerMapWrapper;
import net.ontopia.topicmaps.nav2.core.VariableNotSetException;

import org.junit.Assert;
import org.junit.Test;

public class ContextManagerMapWrapperTest {
  
/*  @Test
  public void testRemove() {
    ContextManagerMapWrapper contextManagerMapWrapper = new 
    ContextManagerMapWrapper(new ContextManager());
    contextManagerMapWrapper.put("the key", "the value");
    contextManagerMapWrapper.remove("the key");
    Collection value = (Collection)contextManagerMapWrapper.get("the key");
    Assert.assertTrue("Expected empty Collection, but found Collection of size" +
        value.size(), value.isEmpty());
    
    if (contextManagerMapWrapper.remove("nonExistent") != null)
      Assert.fail("Removing a non-existent variable should return null");
    if (contextManagerMapWrapper.get("nonExistent") != null)
      Assert.fail("Removing a non-existent variable bound the variable to null,"
          + " but should have done nothing.");
  }
  
  @Test
  public void testGet() {
    ContextManagerMapWrapper contextManagerMapWrapper = new 
        ContextManagerMapWrapper(new ContextManager());
    Assert.assertNull(contextManagerMapWrapper.get("the key"));
  }
*/
}
