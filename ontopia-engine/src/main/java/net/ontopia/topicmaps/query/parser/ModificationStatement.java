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

package net.ontopia.topicmaps.query.parser;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.basic.QueryMatches;

/**
 * INTERNAL: Common abstract superclass for all tolog updates
 * statements (INSERT, DELETE, MERGE, UPDATE).
 */
public abstract class ModificationStatement extends TologStatement {
  protected List litlist;     // always exactly two
  protected TologQuery query; // the FROM ... part, if any

  public ModificationStatement() {
    super();
    litlist = new ArrayList();
  }

  public void addLit(Object lit) {
    litlist.add(lit);
  }

  public List getLitList() {
    return litlist;
  }

  public void setClauseList(List clauses, TologOptions options)
    throws AntlrWrapException {
    // this is only called if there was a FROM clause, so we create a subquery
    query = new TologQuery();
    query.setClauseList(clauses);
    query.setOptions(options);

    // add vars in litlist to select list of subquery so that we get projection
    for (int ix = 0; ix < litlist.size(); ix++) {
      Object lit = litlist.get(ix);
      if (lit instanceof Variable) {
        query.addVariable((Variable) lit);
      }
    }
  }

  public TologQuery getEmbeddedQuery() {
    return query;
  }

  @Override
  public void close() throws InvalidQueryException {
    if (query != null) {
      query.close();
    }

    // verify that if we have variables in the litlist we also have a FROM
    // part
    if (query == null) {
      for (int ix = 0; ix < litlist.size(); ix++) {
        if (litlist.get(ix) instanceof Variable) {
          throw new InvalidQueryException("Cannot have variables in select " +
                                          "part if no from part");
        }
      }
    }
  }

  public abstract int doStaticUpdates(TopicMapIF topicmap, Map arguments)
    throws InvalidQueryException;

  public abstract int doUpdates(QueryMatches matches)
    throws InvalidQueryException;
  
  // --- Internal utilities

  protected String toStringLitlist() {
    StringBuilder buf = new StringBuilder();
    for (int ix = 0; ix < litlist.size(); ix++) {
      buf.append(litlist.get(ix));
      if (ix + 1 < litlist.size()) {
        buf.append(", ");
      }
    }
    return buf.toString();
  }

  protected static int getIndex(Object arg, QueryMatches matches) {
    if (arg instanceof Variable) {
      return matches.getIndex((Variable) arg);
    } else {
      return -1;
    }
  }

  // translates parameters to their values (and leaves values alone)
  protected static Object getValue(Object obj, Map arguments)
    throws InvalidQueryException {
    Object value;
    if (obj instanceof Parameter) {
      value = arguments.get(((Parameter) obj).getName());
      if (value == null) {
        throw new InvalidQueryException("Parameter not specified: " + obj);
      }
    } else {
      value = obj;
    }
    return value;
  }  
}