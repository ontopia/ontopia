
// $Id: ActionForwardPageTest.java,v 1.6 2003/08/28 13:24:17 larsga Exp $

package net.ontopia.topicmaps.webed.impl.basic.test;

import java.util.*;

import net.ontopia.test.AbstractOntopiaTestCase;
import net.ontopia.topicmaps.webed.core.*;
import net.ontopia.topicmaps.webed.impl.basic.*;

public class ActionForwardPageTest extends AbstractOntopiaTestCase {

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
