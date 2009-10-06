
// $Id$

package net.ontopia.topicmaps.query.parser;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.net.MalformedURLException;

import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.basic.QueryMatches;
import net.ontopia.topicmaps.impl.utils.Argument;
import net.ontopia.topicmaps.impl.utils.ArgumentValidator;

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
    FunctionSignature signature = FunctionSignature.getSignature(function);
    Object arg1 = litlist.get(0);
    int varix1 = getIndex(arg1, matches);
    Object arg2 = litlist.get(1);
    int varix2 = getIndex(arg2, matches);
    
    for (int row = 0; row <= matches.last; row++) {
      if (varix1 != -1)
        arg1 = matches.data[row][varix1];

      if (varix2 != -1)
        arg2 = matches.data[row][varix2];

      signature.validateArguments(arg1, arg2, funcname);
      function.delete((TMObjectIF) arg1, arg2);
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
    else if (name.equals("subject-identifier"))
      return new SubjectIdentifierFunction();
    else if (name.equals("subject-locator"))
      return new SubjectLocatorFunction();
    else if (name.equals("direct-instance-of"))
      return new DirectInstanceOfFunction();
    else
      throw new InvalidQueryException("No such delete function: '" + name + "'");
  }

  interface DeleteFunctionIF {
    public void delete(TMObjectIF object, Object value);
    public String getSignature();
  }

  class ItemIdentifierFunction implements DeleteFunctionIF {
    public String getSignature() {
      return "x s";
    }
    public void delete(TMObjectIF object, Object v) {
      String value = (String) v;
      try {
        object.removeItemIdentifier(new URILocator(value));
      } catch (MalformedURLException e) {
        throw new OntopiaRuntimeException("Invalid URI: " + value);
      }
    }
  }

  class SubjectIdentifierFunction implements DeleteFunctionIF {
    public String getSignature() {
      return "t s";
    }
    public void delete(TMObjectIF object, Object v) {
      if (!(object instanceof TopicIF))
        return;

      TopicIF topic = (TopicIF) object;
      String value = (String) v;
      try {
        topic.removeSubjectIdentifier(new URILocator(value));
      } catch (MalformedURLException e) {
        throw new OntopiaRuntimeException("Invalid URI: " + value);
      }
    }
  }

  class SubjectLocatorFunction implements DeleteFunctionIF {
    public String getSignature() {
      return "t s";
    }
    public void delete(TMObjectIF object, Object v) {
      if (!(object instanceof TopicIF))
        return;

      TopicIF topic = (TopicIF) object;
      String value = (String) v;
      try {
        topic.removeSubjectLocator(new URILocator(value));
      } catch (MalformedURLException e) {
        throw new OntopiaRuntimeException("Invalid URI: " + value);
      }
    }
  }

  class DirectInstanceOfFunction implements DeleteFunctionIF {
    public String getSignature() {
      return "t t";
    }
    public void delete(TMObjectIF object, Object v) {
      if (!(object instanceof TopicIF))
        return;

      TopicIF topic = (TopicIF) object;
      TopicIF type = (TopicIF) v;
      topic.removeType(type);
    }
  }

  static class FunctionSignature extends ArgumentValidator {
    private static Map cache = new HashMap(); // used to avoid having to reparse

    public static FunctionSignature getSignature(DeleteFunctionIF function)
    throws InvalidQueryException {

      String sign = function.getSignature();
      FunctionSignature signature = (FunctionSignature) cache.get(sign);

      if (signature == null) {
        signature = new FunctionSignature(sign);
        cache.put(sign, signature);
      }
    
      return signature;
    }
  
    private FunctionSignature(String signature) {
      super(signature);
    }

    public void validateArguments(Object arg1, Object arg2, String function)
      throws InvalidQueryException {
      check(arg1, getArgument(0), function, 1);
      check(arg2, getArgument(1), function, 2);
    }

    public void check(Object arg, Argument reqarg, String function, int no)
      throws InvalidQueryException {
      if (!reqarg.allows(arg.getClass()))
        throw new InvalidQueryException("Delete function " + function +
                                        " does not accept " +
                                        arg +
                                        "as parameter no " + no +
                                        ", but requires a " +
                                        getClassList(reqarg.getTypes()));
    }
  }
}