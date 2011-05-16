
// $Id: CharacterSetTest.java,v 1.1 2005/04/13 14:46:09 larsga Exp $

package net.ontopia.utils;

import junit.framework.TestCase;

public class CharacterSetTest extends TestCase {

  public CharacterSetTest(String name) {
    super(name);
  }
  
  public void testEmpty() {
    CharacterSet set = new CharacterSet();
    set.close();
    assertTrue("empty set contains character", !set.contains((char) 0x20));
    assertTrue("empty set contains character", !set.contains((char) 0x51));
    assertTrue("empty set contains character", !set.contains((char) 0x7F2E));
  }
  
  public void testSingleCharacter() {
    CharacterSet set = new CharacterSet();
    set.addInterval(' ', ' ');
    set.close();
    
    assertTrue("set contains unknown character", !set.contains((char) 0x19));
    assertTrue("set contains unknown character", !set.contains((char) 0x21));
    assertTrue("set doesn't contain added character", set.contains(' '));
  }

  public void testSingleInterval() {
    CharacterSet set = new CharacterSet();
    set.addInterval('a', 'z');
    set.close();
    
    assertTrue("set contains unknown character", !set.contains((char) 0x19));
    assertTrue("set contains unknown character", !set.contains((char) 0x21));
    assertTrue("set contains unknown character", !set.contains(' '));
    assertTrue("set doesn't contain added character", set.contains('a'));
    assertTrue("set doesn't contain added character", set.contains('g'));
    assertTrue("set doesn't contain added character", set.contains('z'));
  }

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
    
    assertTrue("set contains unknown character", !set.contains((char) 0x19));
    assertTrue("set contains unknown character", !set.contains((char) 0x21));
    assertTrue("set contains unknown character", !set.contains(' '));
    assertTrue("set contains unknown character", !set.contains((char) 0x0321));
    assertTrue("set doesn't contain added character 'a'", set.contains('a'));
    assertTrue("set doesn't contain added character 'g'", set.contains('g'));
    assertTrue("set doesn't contain added character 'z'", set.contains('z'));
    assertTrue("set doesn't contain added character '1'", set.contains('1'));
    assertTrue("set doesn't contain added character ':'", set.contains(':'));
    assertTrue("set doesn't contain added character 'A'", set.contains('A'));
  }
}
