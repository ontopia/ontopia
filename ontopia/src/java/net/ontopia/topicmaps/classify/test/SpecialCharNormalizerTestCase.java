
package net.ontopia.topicmaps.classify.test;

import net.ontopia.test.AbstractOntopiaTestCase;
import net.ontopia.topicmaps.classify.SpecialCharNormalizer;


public class SpecialCharNormalizerTestCase extends AbstractOntopiaTestCase {
  
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
