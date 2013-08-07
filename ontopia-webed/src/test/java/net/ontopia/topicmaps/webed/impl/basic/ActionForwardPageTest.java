/*
 * #!
 * Ontopia Webed
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

package net.ontopia.topicmaps.webed.impl.basic;

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
