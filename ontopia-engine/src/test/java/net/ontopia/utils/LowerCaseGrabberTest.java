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

package net.ontopia.utils;

import junit.framework.Test;
import junit.framework.TestSuite;

public class LowerCaseGrabberTest extends AbstractGrabberTest {

  public LowerCaseGrabberTest(String name) {
    super(name);
  }

  public static Test suite() {
    return new TestSuite(LowerCaseGrabberTest.class);
  }
  
  public void testLowerCaseGrabber() {
    String str = "Jazz is NOT dead, it JUST SMELLS funny";
      
    testGrabber(new LowerCaseGrabber().grab(str), str.toLowerCase(), str);
  }

}




