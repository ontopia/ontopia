package net.ontopia.topicmaps.query.toma.impl.basic.function;

import net.ontopia.topicmaps.query.toma.impl.utils.Stringifier;

public class SubstrFunction extends AbstractSimpleFunction {
  
  public SubstrFunction() {
    super("SUBSTR", 2);
  }

  public String evaluate(Object obj) {
    String str = Stringifier.toString(obj);
    if (str != null) {
      return substr(str);
    } else {
      return str;
    }
  }
  
  private String substr(String str) {
    try {
      int from = Integer.valueOf(parameters.get(0));
      
      if (parameters.size() == 2) {
        int length = Integer.valueOf(parameters.get(1));
        return str.substring(from, from+length);
      } else {
        return str.substring(from);
      }
    } catch (NumberFormatException e) {
      // TODO: error handling -> throw InvalidQueryException
      return null;
    } catch (IndexOutOfBoundsException e) {
      // TODO: error handling -> throw InvalidQueryException
      return null;
    }
  }
}
