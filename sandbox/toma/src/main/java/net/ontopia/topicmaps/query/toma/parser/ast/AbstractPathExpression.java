package net.ontopia.topicmaps.query.toma.parser.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.toma.parser.AntlrWrapException;
import net.ontopia.topicmaps.query.toma.parser.ast.PathElementIF.TYPE;
import net.ontopia.topicmaps.query.toma.util.IndentedStringBuilder;

/**
 * INTERNAL: Abstract base class for path expressions in the AST.
 */
public abstract class AbstractPathExpression extends AbstractExpression
    implements PathExpressionIF {

  private ArrayList<PathElementIF> path;

  public AbstractPathExpression() {
    super("PATHEXPR", 0);
    this.path = new ArrayList<PathElementIF>();
  }

  public List<AbstractVariable> getBoundVariables() {
    List<AbstractVariable> vars = new ArrayList<AbstractVariable>();
    return vars;
  }

  public void addPath(PathElementIF next) throws AntlrWrapException {
    path.add(next);
  }

  public boolean isEmpty() {
    return path.isEmpty();
  }
  
  public boolean isVariable() {
    if (getPathLength() == 1) {
      PathElementIF element = path.get(0);
      if (element instanceof VariableIF) {
        return true;
      }
    }
    return false;
  }
  
  public String getVariableName() {
    if (getPathLength() > 0) {
      PathElementIF element = path.get(0);
      if (element instanceof VariableIF) {
        return ((VariableIF) element).toString();
      }
    }
    return null;
  }
  
  public VariableDecl getVariableDeclaration() {
    if (getPathLength() > 0) {
      PathElementIF element = path.get(0);
      if (element instanceof VariableIF) {
        return ((VariableIF) element).getDeclaration();
      }
    }
    return null;
  }
  
  public int getPathLength() {
    return path.size();
  }

  public PathElementIF getPathElement(int idx) {
    return path.get(idx);
  }

  @Override
  public void addChild(ExpressionIF child) throws AntlrWrapException {
    throw new AntlrWrapException(new InvalidQueryException(
        "PathExpressions can not have children"));
  }

  public TYPE output() {
    if (!isEmpty()) {
      PathElementIF lastElement = path.get(path.size() - 1);
      return lastElement.output();
    } else {
      return PathElementIF.TYPE.NONE;
    }
  }

  @Override
  public boolean validate() throws AntlrWrapException {
    super.validate();

    PathElementIF.TYPE output = PathElementIF.TYPE.NONE;
    PathElementIF last = null;
    for (PathElementIF element : path) {
      // validate the element itself
      element.validate();
      // get the valid input for this element
      Set<PathElementIF.TYPE> validInput = element.validInput();

      // if the first element was a VariablePath, constrain the types
      if (last instanceof AbstractVariable) {
        AbstractVariable var = (AbstractVariable) last;
        try {
          var.constrainTypes(validInput);
        } catch (InvalidQueryException e) {
          throw new AntlrWrapException(e);
        }
      } else {
        // check if the types match
        if (validInput != null && !validInput.contains(output)) {
          throw new AntlrWrapException(new InvalidQueryException(
              "path element '" + element.toString() + "' not allowed after '"
              + last.toString() + "'"));
        }
      }
      
      output = element.output();
      last = element;
    }
 
    return true;
  }

  @Override
  public void fillParseTree(IndentedStringBuilder buf, int level) {
    for (PathElementIF pe : path) {
      pe.fillParseTree(buf, level++);
    }
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();

    if (path != null) {
      for (int i=0; i<path.size(); i++) {
        sb.append(path.get(i).toString());
        if (i < (path.size() - 1)) {
          sb.append(".");
        }
      }
    }
    return sb.toString();
  }
}
