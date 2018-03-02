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

public class JunkNormalizerTestCase extends TestCase {
  
  public JunkNormalizerTestCase(String name) {
    super(name);
  }
  
  public void testNormalizer() {
    JunkNormalizer n = new JunkNormalizer();

    assertEquals(n.normalize("abc"), "abc");
    assertEquals(n.normalize("john's"), "john");
    assertEquals(n.normalize(" abc "), "abc");
  }
  
}
