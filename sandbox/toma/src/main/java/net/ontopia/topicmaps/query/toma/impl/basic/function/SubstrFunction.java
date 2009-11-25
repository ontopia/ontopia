/**
 * Copyright (C) 2009 Space Applications Services
 *   <thomas.neidhart@spaceapplications.com>
 *
 * This file is part of the Ontopia project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

  @Override
  public boolean validate() throws AntlrWrapException {
    super.validate();
    
    if (parameters.size() < 1 || parameters.size() > 2) {
      throw new AntlrWrapException(new InvalidQueryException(
          "Only 1 or 2 parameters are allowed for 'substr' function."));
    }
    
    String param1 = parameters.get(0);
    
    try {
      from = Integer.valueOf(param1) - 1;
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
