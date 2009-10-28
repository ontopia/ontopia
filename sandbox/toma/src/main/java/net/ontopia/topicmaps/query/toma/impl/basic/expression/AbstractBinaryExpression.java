package net.ontopia.topicmaps.query.toma.impl.basic.expression;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.toma.impl.basic.BasicExpressionIF;
import net.ontopia.topicmaps.query.toma.parser.AntlrWrapException;
import net.ontopia.topicmaps.query.toma.parser.ast.AbstractExpression;
import net.ontopia.topicmaps.query.toma.parser.ast.AbstractPathExpression;
import net.ontopia.topicmaps.query.toma.parser.ast.ExpressionIF;
import net.ontopia.topicmaps.query.toma.parser.ast.VariableDecl;

/**
 * INTERNAL: abstract base class for all binary expressions.
 */
public abstract class AbstractBinaryExpression extends AbstractExpression
    implements BasicExpressionIF {

  protected AbstractBinaryExpression(String name) {
    super(name, 2);
  }

  @Override
  public boolean validate() throws AntlrWrapException {
    if (!super.validate()) {
      return false;
    }

    // check the types of variables for cases like:
    // $var = $t.name
    ExpressionIF left = getChild(0);
    ExpressionIF right = getChild(1);
    if (left instanceof AbstractPathExpression
        && right instanceof AbstractPathExpression) {
      AbstractPathExpression leftPath = (AbstractPathExpression) left;
      AbstractPathExpression rightPath = (AbstractPathExpression) right;

      try {
        if (leftPath.isVariable()) {
          VariableDecl leftVar = leftPath.getVariableDeclaration();
          leftVar.constrainTypes(rightPath.output());
        } else if (rightPath.isVariable()) {
          VariableDecl rightVar = rightPath.getVariableDeclaration();
          rightVar.constrainTypes(leftPath.output());
        }
      } catch (InvalidQueryException e) {
        throw new AntlrWrapException(e);
      }
    }

    return true;
  }
}
