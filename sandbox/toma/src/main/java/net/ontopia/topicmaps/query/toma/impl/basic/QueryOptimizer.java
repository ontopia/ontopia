package net.ontopia.topicmaps.query.toma.impl.basic;

import java.util.LinkedList;
import java.util.List;

import net.ontopia.topicmaps.query.toma.impl.basic.expression.EqualsExpression;
import net.ontopia.topicmaps.query.toma.impl.basic.expression.LiteralExpression;
import net.ontopia.topicmaps.query.toma.impl.basic.expression.PathExpression;
import net.ontopia.topicmaps.query.toma.impl.basic.path.InstancePath;
import net.ontopia.topicmaps.query.toma.impl.basic.path.ItemIDPath;
import net.ontopia.topicmaps.query.toma.impl.basic.path.NamePath;
import net.ontopia.topicmaps.query.toma.impl.basic.path.SubTypePath;
import net.ontopia.topicmaps.query.toma.impl.basic.path.SubjectIDPath;
import net.ontopia.topicmaps.query.toma.impl.basic.path.SubjectLocatorPath;
import net.ontopia.topicmaps.query.toma.impl.basic.path.SuperTypePath;
import net.ontopia.topicmaps.query.toma.impl.basic.path.TopicPath;
import net.ontopia.topicmaps.query.toma.impl.basic.path.TypePath;
import net.ontopia.topicmaps.query.toma.impl.basic.path.VariablePath;
import net.ontopia.topicmaps.query.toma.impl.basic.path.VariantPath;
import net.ontopia.topicmaps.query.toma.impl.utils.QueryOptimizerIF;
import net.ontopia.topicmaps.query.toma.parser.AntlrWrapException;
import net.ontopia.topicmaps.query.toma.parser.ast.ExpressionIF;
import net.ontopia.topicmaps.query.toma.parser.ast.Level;
import net.ontopia.topicmaps.query.toma.parser.ast.PathElementIF;
import net.ontopia.topicmaps.query.toma.parser.ast.VariableDecl;
import net.ontopia.topicmaps.query.toma.parser.ast.AbstractTopic.IDTYPE;

public class QueryOptimizer implements QueryOptimizerIF {

  private List<QueryOptimizerIF> optimizers;

  public QueryOptimizer() {
    optimizers = new LinkedList<QueryOptimizerIF>();
    optimizers.add(new ReplaceExpressions());
  }

  public ExpressionIF optimize(ExpressionIF expr) {
    ExpressionIF e = expr;
    for (QueryOptimizerIF opt : optimizers) {
      e = opt.optimize(e);
    }
    return e;
  }

  // TODO: other ideas for query optimization

  // - reordering of expressions which are coupled together by an 'and'
  // - structure of association path expression should be reordered if the
  //   start of the path is a variable. Transform: 
  //     $t.(r1)<-$a()->(r2) => $a()-> $t(r1)
  //                                     (r2)
  //   this should improve association path expressions significantly

  @SuppressWarnings("unchecked")
  public static class ReplaceExpressions implements QueryOptimizerIF {

    private enum STATUS {
      FOUND, MIRROR, NOTFOUND
    };

    private static Class[] IDPATH = { VariablePath.class, ItemIDPath.class };
    private static Class[] NAMEPATH = { VariablePath.class, NamePath.class };
    private static Class[] VARIANTPATH = { VariablePath.class, NamePath.class,
        VariantPath.class };
    private static Class[] SLPATH = { VariablePath.class,
        SubjectLocatorPath.class };
    private static Class[] SIPATH = { VariablePath.class, SubjectIDPath.class };

    private static Class[] TYPEPATH = { VariablePath.class, TypePath.class };
    private static Class[] INSTANCEPATH = { VariablePath.class,
        InstancePath.class };
    private static Class[] SUBPATH = { VariablePath.class, SubTypePath.class };
    private static Class[] SUPERPATH = { VariablePath.class,
        SuperTypePath.class };
    private static Class[] TOPICPATH = { TopicPath.class };

    public ExpressionIF optimize(ExpressionIF expr) {
      if (expr instanceof EqualsExpression) {
        ExpressionIF left = expr.getChild(0);
        ExpressionIF right = expr.getChild(1);

        STATUS status;
        status = checkExpression(left, right, PathExpression.class,
            LiteralExpression.class);
        if (status != STATUS.NOTFOUND) {
          PathExpression path = (PathExpression) (status == STATUS.FOUND ? left
              : right);
          LiteralExpression literal = (LiteralExpression) (status == STATUS.FOUND ? right
              : left);
          
          VariableDecl varDecl = path.getVariableDeclaration();

          ExpressionIF result = null;
          if (checkSimplePathExpression(path, IDPATH)) {
            // $t.id = 'abc' -> $t = i'abc'
            result = getTopicEqualsIDExpression(varDecl, IDTYPE.IID, literal
                .getValue());
          } else if (checkSimplePathExpression(path, NAMEPATH)) {
            // $t.name = 'abc' -> $t = n'abc'
            result = getTopicEqualsIDExpression(varDecl, IDTYPE.NAME, literal
                .getValue());
          } else if (checkSimplePathExpression(path, VARIANTPATH)) {
            // $t.name.var = 'abc' -> $t = v'abc'
            result = getTopicEqualsIDExpression(varDecl, IDTYPE.VAR, literal
                .getValue());
          } else if (checkSimplePathExpression(path, SLPATH)) {
            // $t.sl = 'abc' -> $t = sl'abc'
            result = getTopicEqualsIDExpression(varDecl, IDTYPE.SL, literal
                .getValue());
          } else if (checkSimplePathExpression(path, SIPATH)) {
            // $t.si = 'abc' -> $t = si'abc'
            result = getTopicEqualsIDExpression(varDecl, IDTYPE.SI, literal
                .getValue());
          }

          // if something went wrong in the optimization process, return the
          // unchanged expression.
          return (result == null) ? expr : result;
        }

        status = checkExpression(left, right, PathExpression.class,
            PathExpression.class);
        if (status != STATUS.NOTFOUND) {
          PathExpression leftPath = (PathExpression) left;
          PathExpression rightPath = (PathExpression) right;
          PathExpression path = null, topic = null;

          // check if the TopicPath Expression is on the right or left side of
          // the equals expression.
          if (checkSimplePathExpression(rightPath, TOPICPATH)) {
            path = leftPath;
            topic = rightPath;
          } else if (checkSimplePathExpression(leftPath, TOPICPATH)) {
            path = rightPath;
            topic = leftPath;
          }

          if (path != null) {
            // get the level of the last path element
            Level level = path.isEmpty() ? null : path.getPathElement(
                path.getPathLength() - 1).getLevel();
            
            VariableDecl varDecl = path.getVariableDeclaration();
            ExpressionIF result = null;

            if (checkSimplePathExpression(path, TYPEPATH)) {
              // $t.type = i'topic' -> $t = i'topic'.instance
              result = getReversalTopicExpression(varDecl, topic,
                  new InstancePath(), level);
            } else if (checkSimplePathExpression(path, INSTANCEPATH)) {
              // $t.instance = i'topic' -> $t = i'topic'.type
              result = getReversalTopicExpression(varDecl, topic,
                  new TypePath(), level);
            } else if (checkSimplePathExpression(path, SUBPATH)) {
              // $t.sub = i'topic' -> $t = i'topic'.super
              result = getReversalTopicExpression(varDecl, topic,
                  new SuperTypePath(), level);
            } else if (checkSimplePathExpression(path, SUPERPATH)) {
              // $t.super = i'topic' -> $t = i'topic'.sub
              result = getReversalTopicExpression(varDecl, topic,
                  new SubTypePath(), level);
            }

            // if something went wrong in the optimization process, return the
            // unchanged expression.
            return (result == null) ? expr : result;
          }
        }
      }

      return expr;
    }

    private ExpressionIF getTopicEqualsIDExpression(VariableDecl decl,
        IDTYPE type, String val) {
      try {
        ExpressionIF expr = new EqualsExpression();
        PathExpression l = new PathExpression();
        l.addPath(new VariablePath(decl));
        expr.addChild(l);
        PathExpression r = new PathExpression();
        r.addPath(new TopicPath(type, val));
        expr.addChild(r);
        return expr;
      } catch (AntlrWrapException e) {
        return null;
      }
    }

    private ExpressionIF getReversalTopicExpression(VariableDecl decl,
        PathExpression topicExpr, PathElementIF reverseElement, Level level) {
      try {
        ExpressionIF expr = new EqualsExpression();
        PathExpression l = new PathExpression();
        l.addPath(new VariablePath(decl));
        expr.addChild(l);
        PathExpression r = topicExpr;
        reverseElement.setLevel(level);
        r.addPath(reverseElement);
        expr.addChild(r);
        return expr;
      } catch (AntlrWrapException e) {
        return null;
      }
    }

    private STATUS checkExpression(ExpressionIF left, ExpressionIF right,
        Class expectedLeft, Class expectedRight) {
      if (expectedLeft.isInstance(left) && expectedRight.isInstance(right)) {
        return STATUS.FOUND;
      } else if (expectedLeft.isInstance(right)
          && expectedRight.isInstance(left)) {
        return STATUS.MIRROR;
      } else {
        return STATUS.NOTFOUND;
      }
    }

    private boolean checkSimplePathExpression(PathExpression path,
        Class<? extends PathElementIF>... elements) {
      // check if the length of the same for both paths
      if (path.getPathLength() != elements.length) {
        return false;
      }

      // now check all elements for equality
      for (int idx = 0; idx < path.getPathLength(); idx++) {
        PathElementIF pe = path.getPathElement(idx);

        // if they are not of the same class -> false
        if (!elements[idx].isInstance(pe)) {
          return false;
        }

        if (pe.getScope() != null || pe.getType() != null) {
          return false;
        }
      }
      return true;
    }
  }
}
