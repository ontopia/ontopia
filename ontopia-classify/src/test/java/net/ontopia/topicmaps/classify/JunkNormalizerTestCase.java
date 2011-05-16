
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
    assertEquals(n.normalize(" abc "), " abc ");
  }
  
}
