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

import org.junit.Test;

public class SubstringGrabberTest extends AbstractGrabberTest {

  @Test
  public void testSubstringGrabber() {
    String str = "Jazz is NOT dead, it JUST SMELLS funny";
      
    assertGrabberResult(new SubstringGrabber(2, 5).grab(str), str.substring(2, 5), str.substring(1,4));
    assertGrabberResult(new SubstringGrabber(0, 10).grab(str), str.substring(0, 10), str.substring(1, 10));
    assertGrabberResult(new SubstringGrabber(2, str.length()).grab(str), str.substring(2, str.length()), str.substring(2, str.length() - 1));
  }

}
