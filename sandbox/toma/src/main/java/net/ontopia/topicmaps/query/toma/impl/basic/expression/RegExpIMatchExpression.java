package net.ontopia.topicmaps.query.toma.impl.basic.expression;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * INTERNAL: Case insensitive regular expression match ('~*') operator, 
 * checks whether an expression is matched by the given regular expression.
 * <p>
 * <b>Note</b>: For convenience reasons, the specified regular expression is
 * automatically extended to match any following string afterwards: "expr.*".
 * </p>
 */
public class RegExpIMatchExpression extends AbstractComparisonExpression {
  public RegExpIMatchExpression() {
    super("~*");
  }

  protected boolean satisfiesExpression(String s1, String s2) {
    if (s1 != null && s2 != null) {
      Pattern p = Pattern.compile(s2 + ".*", Pattern.CASE_INSENSITIVE);
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
