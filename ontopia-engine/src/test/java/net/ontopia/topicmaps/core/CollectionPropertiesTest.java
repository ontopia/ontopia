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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.junit.Assert;
import org.junit.Test;

/**
 * This class is the base class for all test cases which test the collection properties
 * handling of the different implementations of the core topic map interfaces.
 */
public abstract class CollectionPropertiesTest extends AbstractTopicMapTest {

  protected void testMethod(Object obj, String methodName, String propType) {
    Class cls = obj.getClass();
    Class parmCls[] = new Class[1];
    Method method = null;

    try {
      parmCls[0] = Class.forName(propType);
      method = cls.getMethod(methodName, parmCls);
    } catch (ClassNotFoundException ex) {
      Assert.fail("Test setup failure. Cannot find class: " + propType);
    } catch (NoSuchMethodException ex) {
      Assert.fail("Test setup failure: Cannot find method: " + methodName);
    }
        

    Object parms[] = new Object[1];
    parms[0] = null;
        
    try {
      method.invoke(obj, (Object[])null);

      // If we get to here without an exception, the test failed.
      Assert.fail("Allowed to pass null to " + cls.getName() + "." + methodName);
    } catch(IllegalArgumentException ex) {
      // This is expected
    } catch(IllegalAccessException ex) {
      Assert.fail("Test setup failure: Cannot access method " + cls.getName() + "." + methodName);
    } catch(InvocationTargetException ex) {
      Assert.fail("Test setup failure: Cannot invoke method " + cls.getName() + "." + methodName);
    }
  }

  protected void testProperty(Object obj, String propName, String propClass) {
    testMethod(obj, "add" + propName, propClass);
    testMethod(obj, "remove" + propName, propClass);
  }

  @Test
  public void testTopic() {
    TopicIF topic = builder.makeTopic();
    testTMObject(topic);
  }

  @Test
  public void testAssociation() {
    AssociationIF assoc = builder.makeAssociation(builder.makeTopic());
    testScoped(assoc);
    testTMObject(assoc);
  }

  @Test
  public void testTopicName() {
    TopicIF topic = builder.makeTopic();
    TopicNameIF bn = builder.makeTopicName(topic, "");
    testScoped(bn);
    testTMObject(bn);
  }

  protected void testScoped(ScopedIF obj) {
    testProperty(obj, "Theme", "net.ontopia.topicmaps.core.TopicIF");
  }
        
  protected void testTMObject(TMObjectIF obj) {
    testProperty(obj, "ItemIdentifier", "net.ontopia.infoset.core.LocatorIF");
  }
        
}





