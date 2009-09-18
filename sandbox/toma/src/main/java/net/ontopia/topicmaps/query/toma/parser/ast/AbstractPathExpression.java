package net.ontopia.topicmaps.query.toma.parser.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.toma.parser.AntlrWrapException;
import net.ontopia.topicmaps.query.toma.util.IndentedStringBuilder;

/**
 * INTERNAL: Abstract base class for path expressions in the AST.
 */
public abstract class AbstractPathExpression extends AbstractExpression
    implements PathExpressionIF {

  private PathRootIF root;
  private ArrayList<PathElementIF> path;

  public AbstractPathExpression() {
    this(null);
  }

  public AbstractPathExpression(PathRootIF root) {
    super("PATHEXPR");
    this.root = root;
    this.path = new ArrayList<PathElementIF>();
  }

  public void setRoot(PathRootIF root) {
    this.root = root;
  }

  public PathRootIF getRoot() {
    return root;
  }

  public List<AbstractVariable> getBoundVariables() {
    List<AbstractVariable> vars = new ArrayList<AbstractVariable>();
    return vars;
  }

  public void addPath(PathElementIF next) throws AntlrWrapException {
    path.add(next);
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

  public boolean validate() throws AntlrWrapException {
    root.validate();

    // TODO: check input from root
    PathElementIF.TYPE output = PathElementIF.TYPE.TOPIC;
    PathElementIF last = null;
    for (PathElementIF element : path) {
      Set<PathElementIF.TYPE> validInput = element.validInput();
      
      if (validInput != null && !validInput.contains(output)) {
        throw new AntlrWrapException(
            new InvalidQueryException("path element '" + element.toString()
                + "' not allowed after '" + last.toString() + "'"));
      }
      
      output = element.output();
      last = element;
    }
 
    return true;
  }

  @Override
  public void fillParseTree(IndentedStringBuilder buf, int level) {
    if (root != null)
      root.fillParseTree(buf, level);
    for (PathElementIF pe : path) {
      pe.fillParseTree(buf, ++level);
    }
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();

    if (root != null) {
      sb.append(root.toString());
      if (path != null) {
        for (PathElementIF p : path) {
          sb.append(".");
          sb.append(p.toString());
        }
      }
    }
    return sb.toString();
  }
}
