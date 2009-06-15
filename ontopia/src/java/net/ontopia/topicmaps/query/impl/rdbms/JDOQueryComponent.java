
// $Id: JDOQueryComponent.java,v 1.19 2006/05/02 07:26:22 grove Exp $

package net.ontopia.topicmaps.query.impl.rdbms;

import java.util.Arrays;
import java.util.Map;

import net.ontopia.persistence.proxy.QueryIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.basic.QueryMatches;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: Query component that executes JDO queries and wraps the
 * resulting QueryResultIF in a QueryMatches instance..
 */

public class JDOQueryComponent implements QueryComponentIF {

  //! // Define a logging category.
  //! static Logger log = Logger.getLogger(JDOQueryComponent.class.getName());

  protected QueryIF jdoquery;
  protected String[] colnames;
  
  public JDOQueryComponent(QueryIF jdoquery, String[] colnames) {
    this.jdoquery = jdoquery;
    this.colnames = colnames;
  }

  public QueryMatches satisfy(QueryMatches matches, Map arguments)
    throws InvalidQueryException {
   
    try {

      QueryMatches result = matches;
      
      //! if (log.isDebugEnabled()) log.debug("BEFORE: " + result.dump());

      net.ontopia.persistence.proxy.QueryResultIF jdoresult =
        (net.ontopia.persistence.proxy.QueryResultIF)
        (arguments == null ? jdoquery.executeQuery() : jdoquery.executeQuery(arguments));

      result.add(new QueryResult(jdoresult, colnames));

      //! if (log.isDebugEnabled()) log.debug("AFTER: " + result.dump());
      
      return result;
    } catch (Exception e) {
      throw new OntopiaRuntimeException(e);
    }
    
  }

  public String toString() {
    return "JQC: " + jdoquery + " " + Arrays.asList(colnames);
  }
  
}
