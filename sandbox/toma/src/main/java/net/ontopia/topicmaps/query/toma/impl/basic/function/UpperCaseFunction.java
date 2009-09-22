package net.ontopia.topicmaps.query.toma.impl.basic.function;

import net.ontopia.topicmaps.query.toma.impl.utils.Stringifier;
import net.ontopia.topicmaps.query.toma.parser.AntlrWrapException;

public class UpperCaseFunction extends AbstractSimpleFunction {
  
  public UpperCaseFunction() {
    super("UPPERCASE", 0);
  }

  public String evaluate(Object obj) {
    String str = Stringifier.toString(obj);
    if (str != null) {
      return str.toUpperCase();
    } else {
      return str;
    }
  }
  
  public boolean validate() throws AntlrWrapException {
    return true;
  }
}
