package net.ontopia.topicmaps.query.toma.impl.basic.expression;

public class GreaterThanEqualsExpression extends AbstractComparisonExpression 
{
  public GreaterThanEqualsExpression()
  {
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
