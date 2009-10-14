package net.ontopia.topicmaps.query.toma.impl.basic.expression;

/**
 * INTERNAL: Inequality operator, checks whether two objects are not equal.
 */
public class NotEqualsExpression extends AbstractComparisonExpression {
  public NotEqualsExpression() {
    super("!=");
  }

  protected boolean satisfiesExpression(String s1, String s2) {
    if (s1 == null && s2 != null || s1 != null && s2 == null || !s1.equals(s2))
      return true;
    else
      return false;
  }
}
