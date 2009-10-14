package net.ontopia.topicmaps.query.toma.impl.basic.expression;

import net.ontopia.topicmaps.query.toma.impl.basic.BasicExpressionIF;
import net.ontopia.topicmaps.query.toma.parser.ast.AbstractExpression;

/**
 * INTERNAL: abstract base class for all binary expressions. 
 */
public abstract class AbstractBinaryExpression extends AbstractExpression 
  implements BasicExpressionIF {

  protected AbstractBinaryExpression(String name) {
    super(name, 2);
  }
}
