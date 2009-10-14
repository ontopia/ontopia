package net.ontopia.topicmaps.query.toma.impl.basic.function;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.toma.impl.utils.Stringifier;
import net.ontopia.topicmaps.query.toma.parser.AntlrWrapException;

/**
 * INTERNAL: trims characters from a string.
 */
public class TrimFunction extends AbstractSimpleFunction {
  
  private enum TRIM_TYPE {
    LEADING,
    TRAILING,
    BOTH
  };
  
  private String replaceExpr[];
  
  public TrimFunction() {
    super("TRIM", 2);
  }

  public String evaluate(Object obj) throws InvalidQueryException {
    String str = Stringifier.toString(obj);
    if (str != null) {
      return trim(str);
    } else {
      return str;
    }
  }
  
  @Override
  public boolean validate() throws AntlrWrapException {
    super.validate();
    
    if (parameters.size() > 2) {
      throw new AntlrWrapException(new InvalidQueryException(
          "Only up to 2 parameters are allowed for the 'trim' function."));
    }

    TRIM_TYPE trimming = TRIM_TYPE.BOTH;
    
    if (parameters.size() > 0) {
      String param1 = parameters.get(0);
      if (param1 != null) {
        param1 = param1.toUpperCase();
      }

      if ("BOTH".equals(param1)) {
        trimming = TRIM_TYPE.BOTH;
      } else if ("LEADING".equals(param1)) {
        trimming = TRIM_TYPE.LEADING;
      } else if ("TRAILING".equals(param1)) {
        trimming = TRIM_TYPE.TRAILING;
      } else {
        throw new AntlrWrapException(new InvalidQueryException(
            "unknown parameter for function 'trim': "
            + param1));
      }
    }
    
    String characters = " ";
    if (parameters.size() == 2) {
      characters = parameters.get(1);
    }
    
    characters.replaceAll(" ", "\\w");
    characters.replaceAll(".", "\\.");
    
    switch (trimming) {
    case BOTH:
      replaceExpr = new String[] { "^([" + characters + "])*",
          "([" + characters + "])*$" };
      break;
    case LEADING:
      replaceExpr = new String[] { "^([" + characters + "])*" };
      break;
    case TRAILING:
      replaceExpr = new String[] { "([" + characters + "])*$" };
      break;
    }
    return true;
  }
  
  private String trim(String str) throws InvalidQueryException {
    String s = str;
    for (String regex : replaceExpr) {
      s = s.replaceAll(regex, "");
    }
    return s;
  }
}
