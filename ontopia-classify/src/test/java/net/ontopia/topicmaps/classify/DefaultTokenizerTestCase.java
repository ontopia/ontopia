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

import org.junit.Assert;
import org.junit.Test;

public class DefaultTokenizerTestCase {
  
  @Test
  public void testTokenizer() {
    DefaultTokenizer t = new DefaultTokenizer();
    t.setText("  one two\nthree\t\tfour five six seven eight nine ten\n");

    t.next();
    Assert.assertEquals(t.getToken(), "one");
    t.next();
    Assert.assertEquals(t.getToken(), "two");
    t.next();
    Assert.assertEquals(t.getToken(), "three");
    t.next();
    Assert.assertEquals(t.getToken(), "four");
    t.next();
    Assert.assertEquals(t.getToken(), "five");
    t.next();
    Assert.assertEquals(t.getToken(), "six");
    t.next();
    Assert.assertEquals(t.getToken(), "seven");
    t.next();
    Assert.assertEquals(t.getToken(), "eight");
    t.next();
    Assert.assertEquals(t.getToken(), "nine");

    Assert.assertTrue(t.next());
    Assert.assertEquals(t.getToken(), "ten");

    Assert.assertFalse(t.next());
  }
  
}
