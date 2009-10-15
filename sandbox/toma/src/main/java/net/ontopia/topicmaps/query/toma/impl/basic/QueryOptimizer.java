package net.ontopia.topicmaps.query.toma.impl.basic;

import java.util.LinkedList;
import java.util.List;

import net.ontopia.topicmaps.query.toma.impl.basic.expression.EqualsExpression;
import net.ontopia.topicmaps.query.toma.impl.basic.expression.LiteralExpression;
import net.ontopia.topicmaps.query.toma.impl.basic.expression.PathExpression;
import net.ontopia.topicmaps.query.toma.impl.basic.path.NamePath;
import net.ontopia.topicmaps.query.toma.impl.basic.path.TopicPath;
import net.ontopia.topicmaps.query.toma.impl.basic.path.VariablePath;
import net.ontopia.topicmaps.query.toma.impl.utils.QueryOptimizerIF;
import net.ontopia.topicmaps.query.toma.parser.AntlrWrapException;
import net.ontopia.topicmaps.query.toma.parser.ast.ExpressionIF;
import net.ontopia.topicmaps.query.toma.parser.ast.AbstractTopic.IDTYPE;

public class QueryOptimizer implements QueryOptimizerIF {

  private List<QueryOptimizerIF> optimizers;
  
  public QueryOptimizer() {
    optimizers = new LinkedList<QueryOptimizerIF>();
    optimizers.add(new SimplifyEqualExpressions());
  }
  
  public ExpressionIF optimize(ExpressionIF expr) {
    ExpressionIF e = expr;
    for (QueryOptimizerIF opt : optimizers) {
      e = opt.optimize(e);
    }
    return e;
  }
  
  // TODO: this is just an example for a good optimization
  // need to improve the way optimizations (in this case replacements) are coded
  
  // useful replacements:
  // $t.name = 'something' -> $t = n'something'
  // $t.name.var = 'something' -> $t = v'something'
  // $t.sl = 'something' -> $t = sl'something'
  // $t.si = 'something' -> $t = si'something'
  // $t.type = topicExpr -> $t = topicExpr.instance
  // $t.instance = topicExpr -> $t = topicExpr.type
  // $t.super = topicExpr -> $t = topicExpr.sub
  // $t.sub = topicExpr -> $t = topicExpr.super

  // other ideas for query optimization
  // reordering of expressions which are coupled together by an 'and'
  
  public static class SimplifyEqualExpressions implements QueryOptimizerIF {
    public ExpressionIF optimize(ExpressionIF expr) {
      if (expr instanceof EqualsExpression) {
        BasicExpressionIF left = (BasicExpressionIF) expr.getChild(0);
        BasicExpressionIF right = (BasicExpressionIF) expr.getChild(1);

        if (left instanceof PathExpression && right instanceof LiteralExpression) {
          PathExpression path = (PathExpression) left;
          if (path.getChildCount() == 2 && path.getChild(0) instanceof VariablePath &&
              path.getChild(1) instanceof NamePath) {
            try {
              ExpressionIF result = new EqualsExpression();
              PathExpression l = new PathExpression();
              l.addPath(new VariablePath(path.getVariableName()));
              result.addChild(expr);
              PathExpression r = new PathExpression();
              r.addPath(new TopicPath(IDTYPE.NAME, ((LiteralExpression) right).getValue()));
              result.addChild(r);
              return result;
            } catch (AntlrWrapException e) {}
          }
        }
      }
      
      return expr;
    }
  }
}
