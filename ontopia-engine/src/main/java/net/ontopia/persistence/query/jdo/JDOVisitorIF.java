// $Id: JDOVisitorIF.java,v 1.2 2005/07/12 09:37:40 grove Exp $

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






