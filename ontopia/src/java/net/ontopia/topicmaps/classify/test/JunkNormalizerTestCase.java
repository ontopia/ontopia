
package net.ontopia.topicmaps.classify.test;

import net.ontopia.test.AbstractOntopiaTestCase;
import net.ontopia.topicmaps.classify.JunkNormalizer;


public class JunkNormalizerTestCase extends AbstractOntopiaTestCase {
  
  public JunkNormalizerTestCase(String name) {
    super(name);
  }
  
  public void testNormalizer() {
    JunkNormalizer n = new JunkNormalizer();

    assertEquals(n.normalize("abc"), "abc");
    assertEquals(n.normalize("john's"), "john");
    assertEquals(n.normalize(" abc "), " abc ");
  }
  
}
