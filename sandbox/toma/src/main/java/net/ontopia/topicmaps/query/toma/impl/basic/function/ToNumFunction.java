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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.toma.impl.utils.Stringifier;

/**
 * INTERNAL: Converts a string into a number.
 */
public class ToNumFunction extends AbstractSimpleFunction {

  private static Pattern pattern = Pattern
      .compile("^\\s*([+\\-]?[0-9]+[\\.]?[0-9]*(?:[eE][+\\-]?[0-9]+)?).*");

  public ToNumFunction() {
    super("TO_NUM", 0);
  }

  public static String convertToNumber(Object obj) throws InvalidQueryException {
    String str = Stringifier.toString(obj);
    if (str != null) {
      Matcher m = pattern.matcher(str);
      if (m.matches()) {
        return m.group(1);
      } else {
        return "0";
      }
    } else {
      return "0";
    }
  }
  
  public String evaluate(Object obj) throws InvalidQueryException {
    return convertToNumber(obj);
  }
}
