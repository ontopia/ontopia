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
import net.ontopia.topicmaps.webed.core.ActionResponseIF;

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
