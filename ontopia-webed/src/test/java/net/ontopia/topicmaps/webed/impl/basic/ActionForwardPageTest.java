
package net.ontopia.topicmaps.webed.impl.basic;

import java.util.*;

import net.ontopia.topicmaps.webed.core.*;
import net.ontopia.topicmaps.webed.impl.basic.*;
import junit.framework.TestCase;

public class ActionForwardPageTest extends TestCase {

  public ActionForwardPageTest(String name) {
    super(name);
  }

  public void testEqual() {
    ParamRuleIF paramRuleA = new IdentityParamRule();
    ActionForwardPageIF forwardPageA =
      new ActionForwardPage("/ontopia/about.jsp", "navigation",
                            "aboutNext.jsp", paramRuleA);
    ParamRuleIF paramRuleB = new IdentityParamRule();
    ActionForwardPageIF forwardPageB =
      new ActionForwardPage("/ontopia/about.jsp", "navigation",
                            "aboutNext.jsp", paramRuleB);

    assertTrue("The objects are not equal",
               forwardPageA.equals(forwardPageB));
  }

  public void testUnequal() {
    ParamRuleIF paramRuleA = new IdentityParamRule();
    ActionForwardPageIF forwardPageA =
      new ActionForwardPage("/ontopia/about.jsp", "foo",
                            "aboutNext.jsp", paramRuleA);
    ParamRuleIF paramRuleB = new IdentityParamRule();
    ActionForwardPageIF forwardPageB =
      new ActionForwardPage("/ontopia/about.jsp", "navigation",
                            "aboutNext.jsp", paramRuleB);
    
    assertTrue("The objects are equal",
               !forwardPageA.equals(forwardPageB));
  }
  
}
