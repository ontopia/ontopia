
package net.ontopia.topicmaps.webed.impl.basic;

import java.util.*;

import net.ontopia.topicmaps.webed.core.*;
import net.ontopia.topicmaps.webed.impl.basic.*;
import net.ontopia.topicmaps.webed.impl.utils.*;
import junit.framework.TestCase;

public class ActionResponseTest extends TestCase {
  
  public ActionResponseTest(String name) {
    super(name);
  }
  
  public void testNullDefaultForward() {
    ActionResponseIF response = new ActionResponse(null, null);
    assertTrue("forward is not null by default",
               response.getForward() == null);
  }
  
  public void testSetRelative() {
    ActionResponseIF response = new ActionResponse(null, null);
    response.setForward("bogus.jsp");

    assertTrue("forward is not 'bogus.jsp', but '" + response.getForward() + "'",
               response.getForward().equals("bogus.jsp"));
  }
  
}
