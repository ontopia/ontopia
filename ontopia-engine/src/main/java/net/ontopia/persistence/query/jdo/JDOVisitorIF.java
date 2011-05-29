
package net.ontopia.persistence.query.jdo;


/**
 * INTERNAL: Visitor interface
 */

public interface JDOVisitorIF {

  public void visitable(JDOExpressionIF expr);
  public void visitable(JDOExpressionIF[] exprs);

  public void visitable(JDOValueIF value);
  public void visitable(JDOValueIF[] values);

}






