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
package net.ontopia.topicmaps.query.toma.parser.ast;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.toma.parser.AntlrWrapException;
import net.ontopia.topicmaps.query.toma.util.IndentedStringBuilder;

/**
 * INTERNAL: Abstract base class for literals in the AST.
 */
public abstract class AbstractLiteral extends AbstractExpression implements
    ExpressionIF {
  private String value;

  public AbstractLiteral(String value) {
    super("LITERAL", 0);
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Override
  public void addChild(ExpressionIF child) throws AntlrWrapException {
    throw new AntlrWrapException(new InvalidQueryException(
        "Literals can not have children"));
  }

  @Override
  public void fillParseTree(IndentedStringBuilder buf, int level) {
    buf.append("(   LITERAL) [" + getValue() + "]", level);
  }

  public String toString() {
    return "'" + value + "'";
  }
}
