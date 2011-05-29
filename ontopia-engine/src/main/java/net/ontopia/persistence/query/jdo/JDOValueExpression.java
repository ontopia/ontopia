
package net.ontopia.persistence.query.jdo;

/**
 * INTERNAL: JDOQL boolean expression value: true or false.
 */

public class JDOValueExpression implements JDOExpressionIF {
  
  protected JDOValueIF value;

  public JDOValueExpression(JDOValueIF value) {
    // NOTE: value must have boolean value type
    this.value = value;
  }
  
  public int getType() {
    return VALUE_EXPRESSION;
  }
  
  public JDOValueIF getValue() {
    return value;
  }

  public void setValue(JDOValueIF value) {
    this.value = value;
  }

  public int hashCode() {
    return value.hashCode();
  }
  
  public boolean equals(Object obj) {
    if (obj instanceof JDOValueExpression) {
      JDOValueExpression other = (JDOValueExpression)obj;
      return value.equals(other.value);
    }
    return false;
  }

  public String toString() {
    return "expr(" + value + ')';
  }

  public void visit(JDOVisitorIF visitor) {
    visitor.visitable(value);
  }
  
}
