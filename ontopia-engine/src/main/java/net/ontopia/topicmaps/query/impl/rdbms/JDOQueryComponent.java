/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

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
  //! static Logger log = LoggerFactory.getLogger(JDOQueryComponent.class.getName());

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
