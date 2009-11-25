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

import java.util.ArrayList;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.toma.parser.AntlrWrapException;
import net.ontopia.topicmaps.query.toma.util.IndentedStringBuilder;

/**
 * INTERNAL: Abstract base class for path elements in the AST.
 */
public abstract class AbstractPathElement implements PathElementIF {
  private String name;
  private PathExpressionIF scope;
  private PathExpressionIF type;
  private Level level;
  private VariableIF boundInputVariable;
  private VariableIF boundVariable;
  private ArrayList<PathExpressionIF> childs;

  /**
   * Create a new path element with the given name.
   * @param name the name of the path element.
   */
  public AbstractPathElement(String name) {
    this.name = name;
    this.scope = null;
    this.level = null;
    this.boundInputVariable = null;
    this.boundVariable = null;
    this.childs = null;
  }

  /**
   * Get the name of this path element.
   * 
   * @return the name of the path element
   */
  public String getName() {
    return this.name;
  }

  public void setType(PathExpressionIF expr) throws AntlrWrapException {
    if (isTypeAllowed()) {
      this.type = expr;
    } else {
      throw new AntlrWrapException(new InvalidQueryException(
          "Type not valid at this path element"));
    }
  }

  public PathExpressionIF getType() {
    return type;
  }

  public void setLevel(Level level) throws AntlrWrapException {
    if (isLevelAllowed()) {
      this.level = level;
    } else {
      throw new AntlrWrapException(new InvalidQueryException(
          "Level not valid at this path element"));
    }
  }

  public Level getLevel() {
    return level;
  }

  public void setScope(PathExpressionIF expr) throws AntlrWrapException {
    if (isScopeAllowed()) {
      this.scope = expr;
    } else {
      throw new AntlrWrapException(new InvalidQueryException(
          "Scope not valid at this path element"));
    }
  }

  public PathExpressionIF getScope() {
    return scope;
  }

  public void bindInputVariable(VariableIF var) throws AntlrWrapException {
    this.boundInputVariable = var;
  }

  public VariableIF getBoundInputVariable() {
    return boundInputVariable;
  }
  
  public void bindVariable(VariableIF var) throws AntlrWrapException {
    this.boundVariable = var;
  }

  public VariableIF getBoundVariable() {
    return boundVariable;
  }

  public void addChild(PathExpressionIF child) throws AntlrWrapException {
    if (isChildAllowed()) {
      if (childs == null) {
        childs = new ArrayList<PathExpressionIF>();
      }

      childs.add(child);
    } else {
      throw new AntlrWrapException(new InvalidQueryException(
          "Scope not valid at this path element"));
    }
  }

  public int getChildCount() {
    if (isChildAllowed()) {
      if (childs != null) {
        return childs.size();
      } else {
        return 0;
      }
    } else {
      return 0;
    }
  }

  public PathExpressionIF getChild(int idx) {
    if (isChildAllowed()) {
      if (childs != null) {
        return childs.get(idx);
      } else {
        return null;
      }
    } else {
      return null;
    }
  }

  /**
   * Indicates if a level setting is allowed for this element.
   * @return true if allowed, false otherwise.
   */
  protected abstract boolean isLevelAllowed();

  /**
   * Indicates if a scope expression is allowed for this element.
   * @return true if allowed, false otherwise.
   */
  protected abstract boolean isScopeAllowed();

  /**
   * Indicates if a type expression is allowed for this element.
   * @return true if allowed, false otherwise.
   */
  protected abstract boolean isTypeAllowed();

  /**
   * Indicates if a child is allowed for this element.
   * @return true if allowed, false otherwise.
   */
  protected abstract boolean isChildAllowed();

  public boolean validate() throws AntlrWrapException {
    // Check if no level has been set for type/instance/sub/super
    // elements. If this is the case, set it to the default: Level(1, 1)
    if (isLevelAllowed() && getLevel() == null) {
      setLevel(new Level(1));
    }
    
    VariableIF var = getBoundVariable();
    if (var != null) {
      try {
        ((AbstractVariable) var).constrainTypes(output());
      } catch (InvalidQueryException e) {
        throw new AntlrWrapException(e);
      }
    }

    // special case for Association Paths
    var = getBoundInputVariable();
    if (var != null) {
      try {
        ((AbstractVariable) var).constrainTypes(TYPE.ASSOCIATION);
      } catch (InvalidQueryException e) {
        throw new AntlrWrapException(e);
      }
    }
    
    return true;
  }

  public void fillParseTree(IndentedStringBuilder buf, int depth) {
    StringBuilder sb = new StringBuilder();
    sb.append(String.format("(%1$10s)", getName()));

    if (type != null) {
      sb.append(String.format(" [%1$s]", type.toString()));
    }

    if (scope != null) {
      sb.append(String.format(" @%1$s", scope.toString()));
    }

    if (this.level != null) {
      sb.append(String.format(" (%1$s)", level.toString()));
    }
    
    buf.append(sb.toString(), depth);
    
    for (int i=0; i<getChildCount(); i++) {
      PathExpressionIF child = getChild(i);
      child.fillParseTree(buf, depth+1);
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(getName());

    if (type != null) {
      sb.append("(");
      sb.append(type.toString());
      sb.append(")");
    }

    if (scope != null) {
      sb.append("@");
      sb.append(scope.toString());
    }

    return sb.toString();
  }
}
