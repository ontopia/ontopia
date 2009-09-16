package net.ontopia.topicmaps.query.toma.impl.basic.function;

import net.ontopia.topicmaps.query.toma.impl.utils.Stringifier;

public class LengthFunction extends AbstractSimpleFunction {
  
  public LengthFunction() {
    super("LENGTH", 0);
  }

  public String evaluate(Object obj) {
    String str = Stringifier.toString(obj);
    if (str != null) {
      return String.valueOf(str.length());
    } else {
      return str;
    }
  }
}
