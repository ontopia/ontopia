
package net.ontopia.topicmaps.classify.test;

import net.ontopia.test.AbstractOntopiaTestCase;
import net.ontopia.topicmaps.classify.DefaultTokenizer;


public class DefaultTokenizerTestCase extends AbstractOntopiaTestCase {
  
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
