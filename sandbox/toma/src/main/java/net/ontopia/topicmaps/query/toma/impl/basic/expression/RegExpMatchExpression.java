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
package net.ontopia.topicmaps.query.toma.impl.basic.expression;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * INTERNAL: Regular expression match ('~') operator, checks whether an
 * expression is matched by the given regular expression.
 * <p>
 * <b>Note</b>: For convenience reasons, the specified regular expression is
 * automatically extended to match any following string afterwards: "expr.*".
 * </p>
 */
public class RegExpMatchExpression extends AbstractComparisonExpression {
  public RegExpMatchExpression() {
    super("~");
  }

  protected boolean satisfiesExpression(String s1, String s2) {
    if (s1 != null && s2 != null) {
      Pattern p = Pattern.compile(s2 + ".*");
      Matcher m = p.matcher(s1);
      if (m.matches()) {
        return true;
      } else {
        return false;
      }
    } else {
      return false;
    }
  }
}
