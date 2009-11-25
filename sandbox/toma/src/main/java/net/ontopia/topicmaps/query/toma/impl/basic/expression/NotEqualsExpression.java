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
 * INTERNAL: Inequality operator, checks whether two objects are not equal.
 */
public class NotEqualsExpression extends AbstractComparisonExpression {
  public NotEqualsExpression() {
    super("!=");
  }

  protected boolean satisfiesExpression(String s1, String s2) {
    if (s1 == null && s2 != null || s1 != null && s2 == null || !s1.equals(s2))
      return true;
    else
      return false;
  }
}
