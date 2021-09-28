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

public class StringifierGrabberTest extends AbstractGrabberTest {

  public StringifierGrabberTest(String name) {
    super(name);
  }

  public static Test suite() {
    return new TestSuite(StringifierGrabberTest.class);
  }
  
  public void testStringifierGrabber() {
    testGrabber(new StringifierGrabber().grab(new Float(1234.56)), new Float(1234.56).toString(), new Float(1234).toString()); 
    testGrabber(new StringifierGrabber().grab(new Double(4321.56)), new Double(4321.56).toString(), new Double(4321.57).toString()); 
    testGrabber(new StringifierGrabber().grab(new Integer(2987)), new Integer(2987).toString(), new Integer(2986).toString()); 
    testGrabber(new StringifierGrabber().grab(new Integer(2987)), new Integer(2987).toString(), new Float(2987).toString()); 
    testGrabber(new StringifierGrabber().grab(Boolean.TRUE), Boolean.TRUE.toString(), Boolean.FALSE.toString()); 
  }

}




