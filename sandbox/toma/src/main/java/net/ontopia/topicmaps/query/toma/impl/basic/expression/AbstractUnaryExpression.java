package net.ontopia.topicmaps.query.toma.impl.basic.expression;

import net.ontopia.topicmaps.query.toma.impl.basic.BasicExpressionIF;
import net.ontopia.topicmaps.query.toma.parser.ast.AbstractExpression;

/**
 * INTERNAL: abstract base class for all unary expressions. 
 */
public abstract class AbstractUnaryExpression extends AbstractExpression 
  implements BasicExpressionIF {

  protected AbstractUnaryExpression(String name) {
    super(name, 1);
  }  
}
