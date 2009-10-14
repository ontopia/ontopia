package net.ontopia.topicmaps.query.toma.impl.basic.function;

import net.ontopia.topicmaps.query.toma.impl.utils.Stringifier;

/**
 * INTERNAL: Transforms an input string into lowercase letters.
 */
public class LowerCaseFunction extends AbstractSimpleFunction {
  
  public LowerCaseFunction() {
    super("LOWERCASE", 0);
  }

  public String evaluate(Object obj) {
    String str = Stringifier.toString(obj);
    if (str != null) {
      return str.toLowerCase();
    } else {
      return str;
    }
  }
}
