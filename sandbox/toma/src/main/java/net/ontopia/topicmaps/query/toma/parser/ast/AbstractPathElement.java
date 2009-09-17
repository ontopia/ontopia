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
  private PathRootIF boundVariable;
  private ArrayList<PathExpressionIF> childs;

  /**
   * Create a new path element with the given name.
   * @param name the name of the path element.
   */
  public AbstractPathElement(String name) {
    this.name = name;
    this.scope = null;
    this.level = null;
    this.boundVariable = null;
    this.childs = null;
  }

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

  public void bindVariable(PathRootIF var) throws AntlrWrapException {
    this.boundVariable = var;
  }

  public PathRootIF getBoundVariable() {
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

  public void fillParseTree(IndentedStringBuilder buf, int level) {
    StringBuilder sb = new StringBuilder();
    sb.append(String.format("(%1$10s)", getName()));

    if (type != null) {
      sb.append(String.format(" [%1$s]", type.toString()));
    }

    if (scope != null) {
      sb.append(String.format(" @%1$s", scope.toString()));
    }

    buf.append(sb.toString(), level);
    
    for (int i=0; i<getChildCount(); i++) {
      PathExpressionIF child = getChild(i);
      child.fillParseTree(buf, level+1);
    }
  }

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
