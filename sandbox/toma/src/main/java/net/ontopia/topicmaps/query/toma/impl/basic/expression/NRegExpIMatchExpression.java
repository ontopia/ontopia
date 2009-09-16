package net.ontopia.topicmaps.query.toma.impl.basic.expression;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NRegExpIMatchExpression extends AbstractComparisonExpression 
{
  public NRegExpIMatchExpression()
  {
    super("!~*");
  }

  @Override
  protected boolean satisfiesExpression(String s1, String s2) {
    if (s1 != null && s2 != null) {
      Pattern p = Pattern.compile(s2+".*", Pattern.CASE_INSENSITIVE);
      Matcher m = p.matcher(s1);
      if (!m.matches()) {
        return true;
      } else {
        return false;
      }
    } else { 
      return false;
    }
  }
}
