// $Id: RDBMSQuery.java,v 1.3 2006/07/06 09:50:53 grove Exp $

package net.ontopia.persistence.query.sql;

import java.util.Map;

import net.ontopia.persistence.proxy.QueryIF;
import net.ontopia.persistence.proxy.RDBMSAccess;

/**
 * INTERNAL: 
 */

public class RDBMSQuery implements QueryIF {

  protected RDBMSAccess access;
  protected DetachedQueryIF query;
  
  public RDBMSQuery(RDBMSAccess access, DetachedQueryIF query) {
    this.access = access;
    this.query = query;
  }

  public Object executeQuery() throws Exception {
    return query.executeQuery(access.getConnection());
  }

  public Object executeQuery(Object[] params) throws Exception {
    return query.executeQuery(access.getConnection(), params);
  }

  public Object executeQuery(Map params) throws Exception {
    return query.executeQuery(access.getConnection(), params);
  }
  
}






