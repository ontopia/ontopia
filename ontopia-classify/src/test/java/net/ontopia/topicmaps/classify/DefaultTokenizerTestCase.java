/*
 * #!
 * Ontopia Classify
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

package net.ontopia.topicmaps.classify;

import junit.framework.TestCase;

public class DefaultTokenizerTestCase extends TestCase {
  
  public DefaultTokenizerTestCase(String name) {
    super(name);
  }
  
  public void testTokenizer() {
    DefaultTokenizer t = new DefaultTokenizer();
    t.setText("  one two\nthree\t\tfour five six seven eight nine ten\n");

    t.next();
    assertEquals(t.getToken(), "one");
    t.next();
    assertEquals(t.getToken(), "two");
    t.next();
    assertEquals(t.getToken(), "three");
    t.next();
    assertEquals(t.getToken(), "four");
    t.next();
    assertEquals(t.getToken(), "five");
    t.next();
    assertEquals(t.getToken(), "six");
    t.next();
    assertEquals(t.getToken(), "seven");
    t.next();
    assertEquals(t.getToken(), "eight");
    t.next();
    assertEquals(t.getToken(), "nine");

    assertTrue(t.next());
    assertEquals(t.getToken(), "ten");

    assertFalse(t.next());
  }
  
}
