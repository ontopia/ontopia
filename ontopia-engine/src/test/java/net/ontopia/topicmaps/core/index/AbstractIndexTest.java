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

package net.ontopia.topicmaps.core.index;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.ontopia.topicmaps.core.AbstractTopicMapTest;
import org.junit.Assert;

public abstract class AbstractIndexTest extends AbstractTopicMapTest {
  
  protected Object _ix;
  
  // The expected exception message if NULL is passed to a function
  // which does not accept it.
  protected static String NULLPOINTERMESSAGE = "null is not a valid argument.";
  
  protected Object setUp(String indexInterface) throws Exception {
    super.setUp();
    _ix = topicmap.getIndex("net.ontopia.topicmaps.core.index." + indexInterface);
    Assert.assertTrue("Null " + indexInterface, _ix != null);
    return _ix;
  }

  protected void testNull(String methodName, String paramType) {
    try {
      Class paramTypes[] = new Class[1];
      paramTypes[0] = Class.forName(paramType);
      
      Method m = _ix.getClass().getMethod(methodName, paramTypes);
      Object params[] = new Object[1];
      params[0] = null;
      try {
        m.invoke(_ix, params);
        Assert.fail(methodName + " accepts null parameter.");
      } catch (NullPointerException ex) {
        Assert.assertTrue("NullPointerException does not have expected message.",
               ex.getMessage().equals(NULLPOINTERMESSAGE));
      }
    } catch(ClassNotFoundException ex) {
      Assert.fail("Test setup error: " + ex.getMessage());
    } catch(NoSuchMethodException ex) {
      Assert.fail("Test setup error: " + ex.getMessage());
    } catch(IllegalAccessException ex) {
      Assert.fail("Test setup error: " + ex.getMessage());
    } catch(InvocationTargetException ex) {
      Assert.fail("Test setup error: " + ex.getMessage());
    }
  }
}
