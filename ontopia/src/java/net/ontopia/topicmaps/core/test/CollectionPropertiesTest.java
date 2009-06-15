// $Id: CollectionPropertiesTest.java,v 1.15 2008/06/13 08:17:51 geir.gronmo Exp $

package net.ontopia.topicmaps.core.test;

import java.lang.*;
import java.lang.reflect.*;

import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * This class is the base class for all test cases which test the collection properties
 * handling of the different implementations of the core topic map interfaces.
 */
public class CollectionPropertiesTest extends AbstractTopicMapTest
{

  protected TopicMapReferenceIF topicmapRef;
  protected TopicMapIF topicmap;
  protected TopicMapBuilderIF builder;

  public CollectionPropertiesTest(String name) {
    super(name);
  }

  protected void setUp() /*throws Exception*/ {
    // Get a new topic map object from the factory.
    topicmapRef = factory.makeTopicMapReference();
    try {
      topicmap = topicmapRef.createStore(false).getTopicMap();
      assertTrue("Null topic map!" , topicmap != null);
      
      // Get the topic map factory of that topic map.
      builder = topicmap.getBuilder();     
      assertTrue("Null builder!", builder != null);
    } catch (java.io.IOException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  public void tearDown() {
    // Inform the factory that the topic map is not needed anymore.
    topicmap.getStore().close();
    factory.releaseTopicMapReference(topicmapRef);
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
      method.invoke(obj, null);

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





