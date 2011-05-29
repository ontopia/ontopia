
package net.ontopia.persistence.query.jdo;

/**
 * INTERNAL: JDOQL logical expression: not (!). Syntax: '!( ... )'.
 */

public class JDONot implements JDOExpressionIF {

  protected JDOExpressionIF expression;

  public JDONot(JDOExpressionIF expression) {
    if (expression == null)
      throw new NullPointerException("Not expression must have nested expression.");
    this.expression = expression;
  }
  
  public int getType() {
    return NOT;
  }
  
  public JDOExpressionIF getExpression() {
    return expression;
  }

  public int hashCode() {
    return expression.hashCode();
  }
  
  public boolean equals(Object obj) {
    if (obj instanceof JDONot) {
      JDONot other = (JDONot)obj;
      return expression.equals(other.getExpression());
    }
    return false;
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("!(");
    sb.append(expression);
    sb.append(")");
    return sb.toString();
  }

  public void visit(JDOVisitorIF visitor) {
    visitor.visitable(expression);
  }
  
}






