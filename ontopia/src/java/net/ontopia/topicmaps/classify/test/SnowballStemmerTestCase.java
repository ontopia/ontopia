
package net.ontopia.topicmaps.classify.test;

import net.ontopia.test.AbstractOntopiaTestCase;
import net.ontopia.topicmaps.classify.SnowballStemmer;


public class SnowballStemmerTestCase extends AbstractOntopiaTestCase {
  
  public SnowballStemmerTestCase(String name) {
    super(name);
  }
  
  public void testNormalizer() {
    SnowballStemmer s = new SnowballStemmer("no");

    assertEquals(s.stem("hopper"), "hopp");
  }
  
}
