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

package net.ontopia.net.data;

import junit.framework.TestCase;
import net.ontopia.utils.OntopiaRuntimeException;

public class DataURLTest extends TestCase {
  
  public DataURLTest(String name) {
    super(name);
  }

  protected DataURL makeURI(String address) {
    try {
      return new DataURL(address);
    } catch (java.io.IOException e) {
      throw new OntopiaRuntimeException(e);
    }
  }
    
  // --- tests

  public void testTrivial() {
    DataURL url = makeURI("data:,42");
    assertEquals("Contents of data URL not decoded correctly",
               "42", url.getContents());
  }

  // motivated by bug #1418
  public void testLatin1Chars() { 
    DataURL url = makeURI("data:,hei+p\u00E5+deg");
    String contents = url.getContents();
    assertEquals("Latin1 chars in data URL not decoded correctly: '" + contents + "'",
               "hei p\u00E5 deg", contents);
  }
}
