
// $Id: SQLFalse.java,v 1.1 2003/08/06 07:57:34 grove Exp $

package net.ontopia.persistence.query.sql;

/**
 * INTERNAL: SQL condition: expression that always evaluates to false
 */

public class SQLFalse implements SQLExpressionIF {

  private static SQLFalse singleton;

  public static synchronized SQLFalse getInstance() {
    if (singleton == null) singleton = new SQLFalse();
    return singleton; 
  }

  public int getType() {
    return FALSE;
  }

  public String toString() {
    return "false";
  }
  
}





