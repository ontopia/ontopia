
// $Id: EqualsSQLOptimizer.java,v 1.3 2005/07/12 09:37:40 grove Exp $

package net.ontopia.persistence.query.sql;


/**
 * INTERNAL: SQL optimizer that removes A = A and A != A expressions.
 */

public class EqualsSQLOptimizer extends BooleanSQLOptimizer {

  // + RULE 1: 'A == A'  =>  true
  // + RULE 2: 'A != A'  =>  false
  
  public SQLQuery optimize(SQLQuery query) {
    optimizeQuery(query);
    return query;    
  }

  protected int optimizeEquals(SQLEquals expr) {
    // RULE 1: 'A == A'  =>  true
    if (expr.getLeft().equals(expr.getRight())) {
      //! System.out.println("Optimizing out: " + expr);
      //! optimizeValue(expr.getLeft());
      //! optimizeValue(expr.getRight());
      return 1;

    } else {
      return 0;
    }
  }

  protected int optimizeNotEquals(SQLNotEquals expr) {
    // RULE 2: 'A != A'  =>  false
    if (expr.getLeft().equals(expr.getRight())) {
      //! System.out.println("Optimizing out: " + expr);
      return -1;

    } else {
      //! optimizeValue(expr.getLeft());
      //! optimizeValue(expr.getRight());
      return 0;
    }
  }

}
