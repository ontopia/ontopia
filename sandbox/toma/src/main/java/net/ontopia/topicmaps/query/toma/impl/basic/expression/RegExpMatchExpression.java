package net.ontopia.topicmaps.query.toma.impl.basic.expression;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExpMatchExpression extends AbstractComparisonExpression 
{
  public RegExpMatchExpression()
  {
    super("~");
  }

  @Override
  protected boolean satisfiesExpression(String s1, String s2) {
    if (s1 != null && s2 != null) {
      Pattern p = Pattern.compile(s2+".*");
      Matcher m = p.matcher(s1);
      if (m.matches()) {
        return true;
      } else {
        return false;
      }
    } else { 
      return false;
    }
  }
}
