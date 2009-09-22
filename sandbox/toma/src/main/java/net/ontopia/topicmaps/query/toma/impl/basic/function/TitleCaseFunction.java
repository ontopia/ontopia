package net.ontopia.topicmaps.query.toma.impl.basic.function;

import net.ontopia.topicmaps.query.toma.impl.utils.Stringifier;
import net.ontopia.topicmaps.query.toma.parser.AntlrWrapException;

public class TitleCaseFunction extends AbstractSimpleFunction {
  
  public TitleCaseFunction() {
    super("TITLECASE", 0);
  }

  public String evaluate(Object obj) {
    String str = Stringifier.toString(obj);
    if (str != null) {
      return toTitleCase(str.toLowerCase());
    } else {
      return str;
    }
  }
  
  private String toTitleCase(String str) {
    if (str == null || str.length() == 0) {
        return str;
    }
    int strLen = str.length();
    StringBuilder buffer = new StringBuilder(strLen);
    boolean capitalizeNext = true;
    for (int i = 0; i < strLen; i++) {
        char ch = str.charAt(i);

        if (Character.isWhitespace(ch)) {
            buffer.append(ch);
            capitalizeNext = true;
        } else if (capitalizeNext) {
            buffer.append(Character.toTitleCase(ch));
            capitalizeNext = false;
        } else {
          buffer.append(ch);
        }
    }
    return buffer.toString();
  }
  
  public boolean validate() throws AntlrWrapException {
    return true;
  }
}
