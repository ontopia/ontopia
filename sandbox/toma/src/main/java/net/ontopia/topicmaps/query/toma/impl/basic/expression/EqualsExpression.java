package net.ontopia.topicmaps.query.toma.impl.basic.expression;

/**
 * INTERNAL: Equality operator, checks whether two objects are equal.
 */
public class EqualsExpression extends AbstractComparisonExpression {
  public EqualsExpression() {
    super("EQUALS");
  }

  protected boolean satisfiesExpression(String s1, String s2) {
    if (s1 != null && s2 != null && s1.equals(s2))
      return true;
    else
      return false;
  }
}
