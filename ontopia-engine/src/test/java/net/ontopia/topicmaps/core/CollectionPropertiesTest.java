
package net.ontopia.topicmaps.core;

import java.lang.reflect.*;

/**
 * This class is the base class for all test cases which test the collection properties
 * handling of the different implementations of the core topic map interfaces.
 */
public abstract class CollectionPropertiesTest extends AbstractTopicMapTest
{

  public CollectionPropertiesTest(String name) {
    super(name);
  }

  protected void testMethod(Object obj, String methodName, String propType) {
    Class cls = obj.getClass();
    Class parmCls[] = new Class[1];
    Method method = null;

    try {
      parmCls[0] = Class.forName(propType);
      method = cls.getMethod(methodName, parmCls);
    } catch (ClassNotFoundException ex) {
      fail("Test setup failure. Cannot find class: " + propType);
    } catch (NoSuchMethodException ex) {
      fail("Test setup failure: Cannot find method: " + methodName);
    }
        

    Object parms[] = new Object[1];
    parms[0] = null;
        
    try {
      method.invoke(obj, (Object[])null);

      // If we get to here without an exception, the test failed.
      fail("Allowed to pass null to " + cls.getName() + "." + methodName);
    } catch(IllegalArgumentException ex) {
      // This is expected
    } catch(IllegalAccessException ex) {
      fail("Test setup failure: Cannot access method " + cls.getName() + "." + methodName);
    } catch(InvocationTargetException ex) {
      fail("Test setup failure: Cannot invoke method " + cls.getName() + "." + methodName);
    }
  }

  protected void testProperty(Object obj, String propName, String propClass) {
    testMethod(obj, "add" + propName, propClass);
    testMethod(obj, "remove" + propName, propClass);
  }

  public void testTopic() {
    TopicIF topic = builder.makeTopic();
    testTMObject(topic);
  }

  public void testAssociation() {
    AssociationIF assoc = builder.makeAssociation(builder.makeTopic());
    testScoped(assoc);
    testTMObject(assoc);
  }

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





