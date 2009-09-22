package net.ontopia.topicmaps.query.toma.impl.basic.expression;

/**
 * INTERNAL: GreaterThanEquals ('>=') operator, checks whether the first
 * expression is greater than or equal to the second expression. This operator
 * is only defined, if both expression are integers. 
 * <p>
 * <b>Note</b>: No generic string comparison is performed in case one of the expressions
 * is not an integer as the result can be undefined.
 * </p> 
 */
public class GreaterThanEqualsExpression extends AbstractComparisonExpression {
  public GreaterThanEqualsExpression() {
    super(">=");
  }

  @Override
  protected boolean satisfiesExpression(String s1, String s2) {
    if (s1 == null || s2 == null)
      return false;

    try {
      int i1 = Integer.valueOf(s1);
      int i2 = Integer.valueOf(s2);

      return (i1 >= i2);
    } catch (NumberFormatException e) {
      return false;
    }
  }
}
