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

import net.ontopia.topicmaps.query.toma.impl.utils.Stringifier;

/**
 * INTERNAL: Transforms a string into titlecase style. 
 */
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
}
