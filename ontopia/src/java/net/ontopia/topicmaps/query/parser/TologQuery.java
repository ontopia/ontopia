
// $Id: TologQuery.java,v 1.27 2008/06/13 08:17:54 geir.gronmo Exp $

package net.ontopia.topicmaps.query.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.xml.AbstractTopicMapExporter;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.utils.BindingContext;
import net.ontopia.topicmaps.query.impl.utils.QueryAnalyzer;

/**
 * INTERNAL: Used to represent parsed queries.
 */
public class TologQuery {
  protected List variables; // select * from variables
  protected Set countVariables;
  protected Set allVariables;
  protected List clauses;
  protected List orderBy;
  protected Set orderDescending;
  protected Map arguments; // external args used to resolve param refs
  protected int limit;
  protected int offset;
  protected Map vartypemap;   // maps variable to Object[] containing possible types
  protected Map ptypemap;     // maps parameter to Object[] containing possible types

  protected List selected_variables;
  protected TologOptions options;
  
  public TologQuery() {
    clauses = new ArrayList();
    orderBy = new ArrayList();
    countVariables = new HashSet();
    variables = new ArrayList();
    orderDescending = new HashSet();
    limit = -1;
    offset = -1;
  }

  /// ParsedQueryIF implementation [the class does not implement the interface]
  
  public List getSelectedVariables() {
    if (selected_variables == null) {
      // Need to resolve the list of selected variables once, because
      // of the fact that allVariables is unordered.
      if (variables.size() > 0)
        selected_variables = Collections.unmodifiableList(variables);
      else
        selected_variables = Collections.unmodifiableList(new ArrayList(allVariables));
    }
    return selected_variables;
  }

  public boolean hasSelectClause() {
    return !variables.isEmpty() || !countVariables.isEmpty();
  }

  public String[] getSelectedVariableNames() {
    List selected = getSelectedVariables();
    int width = selected.size();
    String[] colnames = new String[width];
    for (int i=0; i < width; i++) {
      colnames[i] = ((Variable)selected.get(i)).getName();
    }
    return colnames;
  }

  public Collection getAllVariables() {
    return allVariables;
  }
  
  public Collection getCountedVariables() {
    return countVariables;
  }

  public List getOrderBy() {
    return orderBy;
  }

  public boolean isOrderedAscending(String name) {
    return !orderDescending.contains(name);
  }

  public TologOptions getOptions() {
    return options;
  }
  
  public void setOptions(TologOptions options) {
    this.options = options;
  }

  /// Object implementation
  
  public String toString() {
    StringBuffer buf = new StringBuffer();
    
    // select clause
    buf.append("select ");
    List selected = getSelectedVariables();
    if (!selected.isEmpty()) {
      for (int ix = 0; ix < selected.size(); ix++) {
        if (ix > 0) buf.append(", ");

        if (countVariables.contains(selected.get(ix)))
          buf.append("count(" + selected.get(ix) + ")");
        else
          buf.append(selected.get(ix));

      }
      
      buf.append(" from \n");
    }

    // predicates
    buf.append(toString(clauses));

    // order by
    if (!orderBy.isEmpty()) {
      buf.append(" order by ");
      for (int ix = 0; ix < orderBy.size(); ix++) {
        if (ix > 0) buf.append(", ");
        buf.append(orderBy.get(ix));
      }
      buf.append("\n");
    }

    // limit/offset
    if (limit != -1)
      buf.append(" limit " + limit);
    if (offset != -1)
      buf.append(" offset " + offset);
    
    buf.append("?");

    // variable types
//     buf.append("\n\n");
//     Iterator it = vartypemap.keySet().iterator();
//     while (it.hasNext()) {
//       Object name = it.next();
//       buf.append("$" + name + " -> " + net.ontopia.utils.DebugUtils.toString((Object[]) vartypemap.get(name)) + "\n");
//     }
    
    return buf.toString();
  }

  public static String toString(List clauses) {
    StringBuffer buf = new StringBuffer();

    Set rules = new HashSet();
    for (int ix = 0; ix < clauses.size(); ix++) {
      if (ix > 0) buf.append(", ");

      AbstractClause theClause = (AbstractClause) clauses.get(ix);
      if (theClause instanceof PredicateClause) {
        PredicateClause clause = (PredicateClause) theClause;
        PredicateIF predicate = clause.getPredicate();
        buf.append(predicate.getName() + "(" +
                   argumentsToString(clause.getArguments()) + ")");

        if (predicate instanceof net.ontopia.topicmaps.query.impl.basic.RulePredicate)
          rules.add(predicate);

      } else if (theClause instanceof OrClause) {
        OrClause clause = (OrClause) theClause;

        buf.append("{ ");
        List alts = clause.getAlternatives();
        for (int i = 0; i < alts.size(); i++) {
          if (i > 0) buf.append(" | ");
          buf.append(toString((List) alts.get(i)));
        }
        buf.append(" }");
        
      } else if (theClause instanceof NotClause) {
        NotClause clause = (NotClause) theClause;
        buf.append("not(" + clause.getClauses() + ")");
        
      } else
        throw new OntopiaRuntimeException("Unknown clause type:" + theClause);

      buf.append("\n");
    }

//     Iterator it = rules.iterator();
//     while (it.hasNext()) {
//       PredicateIF predicate = (PredicateIF) it.next();
//       buf.append("\n\n\n---" + predicate.getName() + "\n");
//       buf.append(toString(((net.ontopia.topicmaps.query.impl.basic.RulePredicate) predicate).getClauses()));
//     }

    return buf.toString();
  }

  private static String argumentsToString(List arguments) {
    StringBuffer buf = new StringBuffer();

    for (int ix = 0; ix < arguments.size(); ix++) {
      if (ix > 0) buf.append(", ");
      valueToString(arguments.get(ix), buf);
    }
    
    return buf.toString();
  }

  private static void valueToString(Object arg, StringBuffer buf) {
    if (arg instanceof String)
      buf.append('"').append(arg).append('"');
    else if (arg instanceof TopicIF)
      buf.append(topicToString((TopicIF) arg));
    else if (arg instanceof Pair) {
      Pair pair = (Pair) arg;
      valueToString(pair.getFirst(), buf);
      buf.append(" : ");
      valueToString(pair.getSecond(), buf);
    } else
      buf.append(arg);
  }

  private static String topicToString(TopicIF topic) {
    String fallbackid = null; // bad IDs, only used in worst case
    
    Iterator it = topic.getItemIdentifiers().iterator();
    while (it.hasNext()) {
      LocatorIF loc = (LocatorIF) it.next();
      String addr = loc.getAddress();
      int pos = addr.lastIndexOf('#');
      if (pos != -1) {
        String id = addr.substring(pos + 1);
        if (AbstractTopicMapExporter.mayCollide(id))
          fallbackid = id;
        else
          return id;
      }
    }

    it = topic.getSubjectIdentifiers().iterator();
    if (it.hasNext())
      return "i\"" + ((LocatorIF) it.next()).getAddress() + "\"";

    it = topic.getSubjectLocators().iterator();
    if (it.hasNext())
      return "a\"" + ((LocatorIF) it.next()).getAddress() + "\"";

    if (fallbackid != null)
      return fallbackid;
    return "@" + topic.getObjectId();
  }
    
  /// Modifiers

  public List getClauses() {
    return clauses;
  }

  public void setClauseList(List clauses) {
    this.clauses = clauses;
  }

  public void addVariable(Variable variable) throws AntlrWrapException {
    if (variables.contains(variable))
      throw new AntlrWrapException(
              new InvalidQueryException("Variable " + variable +
                                        " appears twice in select clause"));
    variables.add(variable);
  }

  public void addCountVariable(Variable variable) throws AntlrWrapException {
    if (variables.contains(variable))
      throw new AntlrWrapException(
              new InvalidQueryException("Variable " + variable +
                                        " appears twice in select clause"));
    variables.add(variable);
    countVariables.add(variable);
  }
  
  public void addOrderBy(Variable variable, boolean ascending) {
    orderBy.add(variable);
    if (!ascending)
      orderDescending.add(variable.getName());
  }
  
  public void close() throws InvalidQueryException {
   
    // compute the variables we calculate
    allVariables = new HashSet();
    for (int ix = 0; ix < clauses.size(); ix++) {
      AbstractClause clause = (AbstractClause) clauses.get(ix);
      Iterator it = clause.getAllVariables().iterator();
      while (it.hasNext())
        allVariables.add(it.next());
    }

    // verify that SELECT variables actually exist
    for (int ix = 0; ix < variables.size(); ix++)
      if (!allVariables.contains(variables.get(ix)))
        throw new InvalidQueryException("Variable " + variables.get(ix) + " in select clause not used in query");

    // verify that ORDER BY variables actually exist (and are selected)
    for (int ix = 0; ix < orderBy.size(); ix++) {
      if (!allVariables.contains(orderBy.get(ix)))
        throw new InvalidQueryException("Variable " + orderBy.get(ix) + " in order by clause not used in query");

      if (!(variables.isEmpty() && countVariables.isEmpty()) &&
          !variables.contains(orderBy.get(ix)) &&
          !countVariables.contains(orderBy.get(ix)))
        throw new InvalidQueryException("Variable " + orderBy.get(ix) + " in order by clause not in select list");
    }

    // type analysis
    BindingContext bc = QueryAnalyzer.analyzeTypes(this);
    vartypemap = bc.getVariableTypes();
    Iterator it = vartypemap.keySet().iterator();
    while (it.hasNext()) {
      String key = (String) it.next();
      Object[] value = (Object[]) vartypemap.get(key);

      // verify that types are compatible
      if (value.length > 1) {
        int seedType = getTypeIdentifier((Class)value[0]);
        for (int ix = 1; ix < value.length; ix++) {
          if (!isCompatibleTypes(seedType, getTypeIdentifier((Class)value[ix])))
            throw new InvalidQueryException("Variable " + key + " can be bound to incompatible types: '" + 
                                            value[0] + "' and '" + value[ix] + "'");
        }
      }
    }

    ptypemap = bc.getParameterTypes();
  }

  // Note: class types mapped to type identifiers in order to make it
  // easier and faster to check for incompatibilities.
  protected int TYPE_TMObjectIF = 1;
  protected int TYPE_String = 2;
  protected int TYPE_Number = 4;

  private int getTypeIdentifier(Class type) throws InvalidQueryException {
    if (TMObjectIF.class.isAssignableFrom(type))
      return TYPE_TMObjectIF;
    else if (String.class.equals(type))
      return TYPE_String;
    else if (Integer.class.equals(type))
      return TYPE_Number;
    else if (Float.class.equals(type))
      return TYPE_Number;
    else
      throw new InvalidQueryException("Unsupported variable type: " + type);
  }

  private boolean isCompatibleTypes(int type1, int type2) {
    if (type1 == type2)
      return true;
    else
      return false;
  }

  private boolean isTMObject(Class aclass) {
    Class[] ifs = aclass.getInterfaces();
    for (int ix = 0; ix < ifs.length; ix++)
      if (ifs[ix].equals(TMObjectIF.class) || isTMObject(ifs[ix]))
        return true;
    
    return false;
  }

  public Map getArguments() {
    // NOTE: needed by the RDBMS implementation.
    return arguments;
  }
  
  public void setArguments(Map arguments) {
    this.arguments = arguments;
  }

  public Object getArgument(String name) throws InvalidQueryException {
    if (arguments == null)
      throw new InvalidQueryException("Tried to get value for query parameter '" +
                                      name + "', but no arguments provided");

    Object value = arguments.get(name);
    
    if (value == null)
      throw new InvalidQueryException("No value supplied for query parameter '" +
                                      name + "'");

    return value;
  }

  public void setLimit(int limit) {
    this.limit = limit;
  }

  public int getLimit() {
    return limit;
  }

  public void setOffset(int offset) throws InvalidQueryException {
    this.offset = offset;
  }

  public int getOffset() {
    return offset;
  }

  /// query introspection

  public Map getVariableTypes() {
    return vartypemap;
  }

  public Map getParameterTypes() {
    return ptypemap;
  }
}
