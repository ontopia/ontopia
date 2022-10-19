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

import org.junit.Assert;
import org.junit.Test;

public class CharacterSetTest {

  @Test
  public void testEmpty() {
    CharacterSet set = new CharacterSet();
    set.close();
    Assert.assertTrue("empty set contains character", !set.contains((char) 0x20));
    Assert.assertTrue("empty set contains character", !set.contains((char) 0x51));
    Assert.assertTrue("empty set contains character", !set.contains((char) 0x7F2E));
  }
  
  @Test
  public void testSingleCharacter() {
    CharacterSet set = new CharacterSet();
    set.addInterval(' ', ' ');
    set.close();
    
    Assert.assertTrue("set contains unknown character", !set.contains((char) 0x19));
    Assert.assertTrue("set contains unknown character", !set.contains((char) 0x21));
    Assert.assertTrue("set doesn't contain added character", set.contains(' '));
  }

  @Test
  public void testSingleInterval() {
    CharacterSet set = new CharacterSet();
    set.addInterval('a', 'z');
    set.close();
    
    Assert.assertTrue("set contains unknown character", !set.contains((char) 0x19));
    Assert.assertTrue("set contains unknown character", !set.contains((char) 0x21));
    Assert.assertTrue("set contains unknown character", !set.contains(' '));
    Assert.assertTrue("set doesn't contain added character", set.contains('a'));
    Assert.assertTrue("set doesn't contain added character", set.contains('g'));
    Assert.assertTrue("set doesn't contain added character", set.contains('z'));
  }

  @Test
  public void testMultipleIntervals() {
    CharacterSet set = new CharacterSet();
    // set contains A-Za-z0-9-:_
    set.addInterval('A', 'Z');
    set.addInterval('a', 'z');
    set.addInterval('_', '_');
    set.addInterval(':', ':');
    set.addInterval('-', '-');
    set.addInterval('0', '9');
    set.close();
    
    Assert.assertTrue("set contains unknown character", !set.contains((char) 0x19));
    Assert.assertTrue("set contains unknown character", !set.contains((char) 0x21));
    Assert.assertTrue("set contains unknown character", !set.contains(' '));
    Assert.assertTrue("set contains unknown character", !set.contains((char) 0x0321));
    Assert.assertTrue("set doesn't contain added character 'a'", set.contains('a'));
    Assert.assertTrue("set doesn't contain added character 'g'", set.contains('g'));
    Assert.assertTrue("set doesn't contain added character 'z'", set.contains('z'));
    Assert.assertTrue("set doesn't contain added character '1'", set.contains('1'));
    Assert.assertTrue("set doesn't contain added character ':'", set.contains(':'));
    Assert.assertTrue("set doesn't contain added character 'A'", set.contains('A'));
  }
}
