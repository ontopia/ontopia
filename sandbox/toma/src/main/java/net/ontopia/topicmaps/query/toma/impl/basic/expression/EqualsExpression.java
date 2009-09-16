package net.ontopia.topicmaps.query.toma.impl.basic.expression;

public class EqualsExpression extends AbstractComparisonExpression
{
  public EqualsExpression()
  {
    super("EQUALS");
  }

  @Override
  protected boolean satisfiesExpression(String s1, String s2) {
    if (s1 != null && s2 != null && s1.equals(s2))
      return true;
    else 
      return false;
  }
}
