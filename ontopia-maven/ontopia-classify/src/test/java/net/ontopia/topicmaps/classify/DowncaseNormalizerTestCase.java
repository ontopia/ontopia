
package net.ontopia.topicmaps.classify;

import junit.framework.TestCase;

public class DowncaseNormalizerTestCase extends TestCase {
  
  public DowncaseNormalizerTestCase(String name) {
    super(name);
  }
  
  public void testNormalizer() {
    DowncaseNormalizer n = new DowncaseNormalizer();

    assertEquals(n.normalize("AbC"), "abc");
  }
  
}
