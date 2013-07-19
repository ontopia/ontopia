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

import junit.framework.*;
import java.util.*;
import net.ontopia.utils.*;

public class CachedGrabberTest extends AbstractGrabberTest {

  public CachedGrabberTest(String name) {
    super(name);
  }

  public static Test suite() {
    return new TestSuite(CachedGrabberTest.class);
  }
  
  public void testCachedGrabber() {
    CachedGrabber grb = new CachedGrabber(new SubstringGrabber(5, 15));
    
    String str = "Jazz is not dead, it just smells funny!";
    testGrabber(grb.grab(str), grb.getGrabber().grab(str), grb.getGrabber().grab(str.substring(2)));
    str.replace(' ', '-');
    testGrabber(grb.grab(str), grb.getGrabber().grab(str), grb.getGrabber().grab(str.substring(2)));
    testGrabber(grb.grab(str), grb.getGrabber().grab(str), grb.getGrabber().grab(str.substring(2)));
    grb.refresh();
    testGrabber(grb.grab(str), grb.getGrabber().grab(str), grb.getGrabber().grab(str.substring(2)));
    str.replace('-', '_');
    testGrabber(grb.grab(str), grb.getGrabber().grab(str), grb.getGrabber().grab(str.substring(2)));
    testGrabber(grb.grab(str), grb.getGrabber().grab(str), grb.getGrabber().grab(str.substring(2)));

    grb.setGrabber(new UpperCaseGrabber());
    testGrabber(grb.grab(str), grb.grab(str), grb.getGrabber().grab(str));
    grb.refresh();
    testGrabber(grb.grab(str), grb.getGrabber().grab(str), grb.getGrabber().grab(str.substring(2)));
  }

}




