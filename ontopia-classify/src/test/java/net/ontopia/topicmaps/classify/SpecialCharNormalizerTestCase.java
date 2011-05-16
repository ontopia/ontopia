
package net.ontopia.topicmaps.classify;

import junit.framework.TestCase;

public class SpecialCharNormalizerTestCase extends TestCase {
  
  public SpecialCharNormalizerTestCase(String name) {
    super(name);
  }
  
  public void testNormalizer() {
    String prechars = "<')(\"[ {\u00B7-%\u201c\u2018/$.,";
    String poschars = ">')(.,\"':;!]? |}*\u00B7-%\u201d\u2019";

    SpecialCharNormalizer n = new SpecialCharNormalizer(prechars, poschars);

    assertEquals(n.normalize(prechars+"foo"+poschars), "foo");
  }
  
}
