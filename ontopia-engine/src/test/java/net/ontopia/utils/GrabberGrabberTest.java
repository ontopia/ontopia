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

public class GrabberGrabberTest extends AbstractGrabberTest {

  public GrabberGrabberTest(String name) {
    super(name);
  }

  public static Test suite() {
    return new TestSuite(GrabberGrabberTest.class);
  }
  
  public void testGrabberGrabber() {
    LowerCaseGrabber grbLow = new LowerCaseGrabber();
    UpperCaseGrabber grbUp = new UpperCaseGrabber();
    SubstringGrabber grbSub1 = new SubstringGrabber(4, 15);
    SubstringGrabber grbSub2 = new SubstringGrabber(2,7);

    GrabberGrabber grbGrb1 = new GrabberGrabber(grbLow, grbUp);
    GrabberGrabber grbGrb2 = new GrabberGrabber(grbLow, grbUp);
    GrabberGrabber grbGrb3 = new GrabberGrabber(grbUp, grbLow);
    testGrabber(grbGrb1.getGrabbers(), grbGrb2.getGrabbers(), grbGrb3.getGrabbers()); 
    GrabberGrabber grbGrb4 = new GrabberGrabber(grbSub1, grbSub2, grbUp); 
    GrabberGrabber grbGrb5 = new GrabberGrabber(grbSub1, grbSub2, grbUp); 
    GrabberGrabber grbGrb6 = new GrabberGrabber(grbSub1, grbSub2, grbLow); 
    testGrabber(grbGrb4.getGrabbers(), grbGrb5.getGrabbers(), grbGrb6.getGrabbers()); 
    GrabberGrabber grbGrb7 = new GrabberGrabber(grbSub1, grbSub2, grbUp, grbLow); 
    GrabberGrabber grbGrb8 = new GrabberGrabber(grbSub1, grbSub2, grbUp, grbLow); 
    GrabberGrabber grbGrb9 = new GrabberGrabber(grbSub1, grbSub2, grbLow, grbUp);
    testGrabber(grbGrb7.getGrabbers(), grbGrb8.getGrabbers(), grbGrb9.getGrabbers()); 

    grbGrb1.addGrabber(grbSub1);
    grbGrb2.addGrabber(grbSub1);
    grbGrb3.addGrabber(grbSub1);
    
    grbGrb4.setGrabbers(grbGrb7.getGrabbers());
    grbGrb6.setGrabbers(grbGrb9.getGrabbers());
    testGrabber(grbGrb4.getGrabbers(), grbGrb7.getGrabbers(), grbGrb6.getGrabbers()); 

    String str = "JaZz Is NoT dEaD, iT jUsT sMeLlS fUnNy!";
    testGrabber(grbGrb4.grab(str), grbGrb7.grab(str), grbGrb6.grab(str));
  }

}




