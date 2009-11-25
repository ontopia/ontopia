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

import java.util.Collection;
import java.util.Set;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.toma.util.IndentedStringBuilder;

/**
 * INTERNAL: Abstract base class for variable in the AST.
 */
public abstract class AbstractVariable extends AbstractPathElement implements
    VariableIF {
  private VariableDecl decl;

  /**
   * Create a new variable with the given name.
   * 
   * @param name the name of the variable.
   */
  public AbstractVariable(VariableDecl decl) {
    super("VARIABLE");
    this.decl = decl;
  }

  public String getVarName() {
    return decl.getVariableName();
  }

  public VariableDecl getDeclaration() {
    return decl;
  }
  
  public Set<TYPE> getValidTypes() {
    return decl.getValidTypes();
  }

  /**
   * Constrain the types for this variable.
   * @see VariableDecl
   */
  public void constrainTypes(TYPE... types) throws InvalidQueryException {
    decl.constrainTypes(types);
  }

  /**
   * Constrain the types for this variable.
   * @see VariableDecl
   */
  public void constrainTypes(Collection<TYPE> types) throws InvalidQueryException {
    decl.constrainTypes(types);
  }
  
  @Override
  public void fillParseTree(IndentedStringBuilder buf, int level) {
    Set<TYPE> validTypes = getValidTypes();
    String type;

    if (validTypes.size() > 1) {
      type = "UNKNOWN";
    } else {
      type = validTypes.iterator().next().toString();
    }
    
    buf.append("(  VARIABLE) [" + getVarName() + "] [" + type + "]", level);
  }

  @Override
  public String toString() {
    return "$" + decl.getVariableName();
  }
}
