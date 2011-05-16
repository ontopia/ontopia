
package net.ontopia.topicmaps.classify;

import junit.framework.TestCase;

public class SnowballStemmerTestCase extends TestCase {
  
  public SnowballStemmerTestCase(String name) {
    super(name);
  }
  
  public void testNormalizer() {
    SnowballStemmer s = new SnowballStemmer("no");

    assertEquals(s.stem("hopper"), "hopp");
  }
  
}
