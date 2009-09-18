package net.ontopia.topicmaps.query.toma.impl.basic.function;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.toma.impl.utils.Stringifier;
import net.ontopia.topicmaps.query.toma.parser.AntlrWrapException;

/**
 * INTERNAL: Return a substring of the given input string, depending on specific
 * parameters.
 */
public class SubstrFunction extends AbstractSimpleFunction {

  private int from;
  private int to;
  
  public SubstrFunction() {
    super("SUBSTR", 2);
    from = to = -1;
  }

  public String evaluate(Object obj) throws InvalidQueryException {
    String str = Stringifier.toString(obj);
    if (str != null) {
      return substr(str);
    } else {
      return str;
    }
  }

  public boolean validate() throws AntlrWrapException {
    if (parameters.size() < 1 || parameters.size() > 2) {
      throw new AntlrWrapException(new InvalidQueryException(
          "Only 1 or 2 parameters are allowed for 'substr' function."));
    }
    
    String param1 = parameters.get(0);
    
    try {
      from = Integer.valueOf(param1);
      if (from < 0) {
        throw new AntlrWrapException(new InvalidQueryException(
            "negative values are not allowed as parameter for function 'substr': "
                + param1));
      }
    } catch (NumberFormatException e) {
      throw new AntlrWrapException(new InvalidQueryException(
          "invalid parameter for function 'substr': " + param1));
    }

    if (parameters.size() == 2) {
      String param2 = parameters.get(1);
      
      try {
        int length = Integer.valueOf(param2);
        if (length < 0) {
          throw new AntlrWrapException(new InvalidQueryException(
              "negative values are not allowed as parameter for function 'substr': "
                  + param2));
        }
        to = from + length;
      } catch (NumberFormatException e) {
        throw new AntlrWrapException(new InvalidQueryException(
            "invalid parameter for function 'substr': " + param2));
      }
    }
    
    return true;
  }
  
  private String substr(String str) throws InvalidQueryException {
    int localTo = to;

    // if the start index is bigger than the string itself, return an empty
    // string.
    if (from >= str.length()) {
      return "";
    }
    
    // if the end index is larger than the string itself, just go to the end
    if (localTo >= str.length()) {
      localTo = -1;
    }
    
    if (to == -1) {
      return str.substring(from);
    } else {
      return str.substring(from, localTo);
    }
  }
}
