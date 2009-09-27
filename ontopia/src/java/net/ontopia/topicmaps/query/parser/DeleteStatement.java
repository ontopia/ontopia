
// $Id$

package net.ontopia.topicmaps.query.parser;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.net.MalformedURLException;

import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.basic.QueryMatches;

/**
 * INTERNAL: Represents a parsed DELETE statement.
 */
public class DeleteStatement extends TologStatement {
  private String funcname; // name of delete function to be called, if any
  private List litlist;    // doubles as param list to function, when there is one
  private TologQuery query; // the FROM ... part, if any

  public DeleteStatement() {
    super();
    litlist = new ArrayList();
  }

  public void addLit(Object lit) {
    litlist.add(lit);
  }

  public List getLitList() {
    return litlist;
  }

  public void setFunction(String name) {
    this.funcname = name;
  }

  public String getFunction() {
    return funcname;
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
      if (lit instanceof Variable)
        query.addVariable((Variable) lit);
    }
  }

  public TologQuery getEmbeddedQuery() {
    return query;
  }

  public void close() throws InvalidQueryException {
    if (query != null)
      query.close();

    // verify that if we have variables in the litlist we also have a FROM
    // part
    if (query == null)
      for (int ix = 0; ix < litlist.size(); ix++)
        if (litlist.get(ix) instanceof Variable)
          throw new InvalidQueryException("Cannot have variables in select " +
                                          "part if no from part");

  }

  public int doStaticDeletes() throws InvalidQueryException {
    if (funcname == null)
      return doLitListDeletes(true);
    else {
      // in order to avoid duplicating code we produce a "fake" matches
      // object here, so that in effect we're simulating a one-row zero-column
      // result set
      QueryMatches matches = new QueryMatches(Collections.EMPTY_SET, null);
      matches.last++; // make an empty row
      return doFunctionDeletes(matches);
    }
  }

  public int doDeletes(QueryMatches matches) throws InvalidQueryException {
    if (funcname == null)
      return doNormalDeletes(matches);
    else
      return doFunctionDeletes(matches);
  }

  // --- Internal methods

  private int doLitListDeletes(boolean strict) throws InvalidQueryException {
    int deletes = 0;
    for (int ix = 0; ix < litlist.size(); ix++) {
      Object lit = litlist.get(ix);
      if (lit instanceof TMObjectIF) {
        ((TMObjectIF) lit).remove();
        deletes++;
      } else if (strict)
        throw new InvalidQueryException("Invalid reference in litlist: " +
                                        lit);
    }
    return deletes;
  }

  private int doNormalDeletes(QueryMatches matches) throws InvalidQueryException{
    int deletes = doLitListDeletes(false);

    // INV: the final QueryMatches object contains only variables actually
    // used in the litlist, so we can go through and just delete everything

    for (int row = 0; row <= matches.last; row++) {
      for (int col = 0; col < matches.colcount; col++) {
        Object o = matches.data[row][col];
        if (o instanceof TMObjectIF) {
          ((TMObjectIF) o).remove();
          deletes++;
        } else
          throw new InvalidQueryException("Deleting non-topic map object: " +
                                          o);
      }
    }
    
    return deletes;
  }

  private int doFunctionDeletes(QueryMatches matches)
    throws InvalidQueryException {
    int deletes = 0;

    DeleteFunctionIF function = makeFunction(funcname);
    Object arg1 = litlist.get(0);
    int varix1 = getIndex(arg1, matches);
    Object arg2 = litlist.get(1);
    int varix2 = getIndex(arg2, matches);
    
    for (int row = 0; row <= matches.last; row++) {
      TMObjectIF obj;
      if (varix1 == -1)
        obj = (TMObjectIF) arg1;
      else
        obj = (TMObjectIF) matches.data[row][varix1];

      String str;
      if (varix2 == -1)
        str = (String) arg2;
      else
        str = (String) matches.data[row][varix2];

      function.delete(obj, str);
    }

    return deletes;
  }

  private int getIndex(Object arg, QueryMatches matches) {
    if (arg instanceof Variable)
      return matches.getIndex((Variable) arg);
    else
      return -1;
  }
    
  // ----- DELETE FUNCTIONS

  private DeleteFunctionIF makeFunction(String name)
    throws InvalidQueryException {
    if (name.equals("item-identifier"))
      return new ItemIdentifierFunction();
    else
      throw new InvalidQueryException("No such delete function: '" + name + "'");
  }

  interface DeleteFunctionIF {
    public void delete(TMObjectIF object, String value);
  }

  class ItemIdentifierFunction implements DeleteFunctionIF {
    public void delete(TMObjectIF object, String value) {
      try {
        object.removeItemIdentifier(new URILocator(value));
      } catch (MalformedURLException e) {
        throw new OntopiaRuntimeException("Invalid URI: " + value);
      }
    }
  }
}