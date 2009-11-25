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

/**
 * INTERNAL: GreaterThanEquals ('>=') operator, checks whether the first
 * expression is greater than or equal to the second expression. This operator
 * is only defined, if both expression are integers. 
 * <p>
 * <b>Note</b>: No generic string comparison is performed in case one of the expressions
 * is not an integer as the result can be undefined.
 * </p> 
 */
public class GreaterThanEqualsExpression extends AbstractComparisonExpression {
  public GreaterThanEqualsExpression() {
    super(">=");
  }

  protected boolean satisfiesExpression(String s1, String s2) {
    if (s1 == null || s2 == null)
      return false;

    try {
      int i1 = Integer.valueOf(s1);
      int i2 = Integer.valueOf(s2);

      return (i1 >= i2);
    } catch (NumberFormatException e) {
      return false;
    }
  }
}
