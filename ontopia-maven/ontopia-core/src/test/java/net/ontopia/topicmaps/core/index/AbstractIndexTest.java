
// $Id: AbstractIndexTest.java,v 1.13 2008/06/11 16:55:57 geir.gronmo Exp $

package net.ontopia.topicmaps.core.index;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.ontopia.topicmaps.core.AbstractTopicMapTest;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;

public abstract class AbstractIndexTest extends AbstractTopicMapTest {
  
  protected Object _ix;
  
  // The expected exception message if NULL is passed to a function
  // which does not accept it.
  protected static String NULLPOINTERMESSAGE = "null is not a valid argument.";
  
  public AbstractIndexTest(String name) {
    super(name);
  }

  protected Object setUp(String indexInterface) throws Exception {
    super.setUp();
    _ix = topicmap.getIndex("net.ontopia.topicmaps.core.index." + indexInterface);
    assertTrue("Null " + indexInterface, _ix != null);
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
        fail(methodName + " accepts null parameter.");
      } catch (NullPointerException ex) {
        assertTrue("NullPointerException does not have expected message.",
               ex.getMessage().equals(NULLPOINTERMESSAGE));
      }
    } catch(ClassNotFoundException ex) {
      fail("Test setup error: " + ex.getMessage());
    } catch(NoSuchMethodException ex) {
      fail("Test setup error: " + ex.getMessage());
    } catch(IllegalAccessException ex) {
      fail("Test setup error: " + ex.getMessage());
    } catch(InvocationTargetException ex) {
      fail("Test setup error: " + ex.getMessage());
    }
  }
}
