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

package net.ontopia.topicmaps.query.impl.basic;

import java.io.IOException;
import java.io.Reader;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.core.index.IndexIF;
import net.ontopia.topicmaps.impl.rdbms.RDBMSTopicMapStore;
import net.ontopia.topicmaps.query.core.DeclarationContextIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.core.ParsedModificationStatementIF;
import net.ontopia.topicmaps.query.core.ParsedQueryIF;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.impl.utils.Prefetcher;
import net.ontopia.topicmaps.query.impl.utils.QueryAnalyzer;
import net.ontopia.topicmaps.query.impl.utils.QueryMatchesUtils;
import net.ontopia.topicmaps.query.impl.utils.QueryOptimizer;
import net.ontopia.topicmaps.query.parser.GlobalParseContext;
import net.ontopia.topicmaps.query.parser.LocalParseContext;
import net.ontopia.topicmaps.query.parser.ModificationStatement;
import net.ontopia.topicmaps.query.parser.ParseContextIF;
import net.ontopia.topicmaps.query.parser.TologOptions;
import net.ontopia.topicmaps.query.parser.TologParser;
import net.ontopia.topicmaps.query.parser.TologQuery;
import net.ontopia.topicmaps.query.parser.Variable;
import net.ontopia.topicmaps.query.utils.TologSpy;
import net.ontopia.topicmaps.utils.TopicStringifiers;
import net.ontopia.utils.CompactHashSet;
import net.ontopia.utils.OntopiaRuntimeException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: This query processor implementation can be used to query any topic
 * map implementation; it makes no assumptions about the stored form of the
 * topic map.
 */
public class QueryProcessor extends AbstractQueryProcessor implements
    QueryProcessorIF, IndexIF {
  protected TopicMapIF topicmap; // the topic map to query
  protected Collator collator;
  protected TologOptions options;
  protected TologParser parser; // the default parser (may have state)

  // --- initialize logging facility.
  private static Logger logger = LoggerFactory.getLogger(QueryProcessor.class.getName());

  // -- Prefetcher constants

  private final static int[] Prefetcher_OB_fields = new int[] {
      Prefetcher.TopicIF_names, Prefetcher.TopicNameIF_variants };

  private final static boolean[] Prefetcher_OB_traverse = new boolean[] {
      false, false };

  public QueryProcessor(TopicMapIF topicmap) {
    this(topicmap, topicmap.getStore().getBaseAddress());
  }

  public QueryProcessor(TopicMapIF topicmap, LocatorIF base) {
    this.topicmap = topicmap;
    this.collator = getCollator(topicmap);

    this.options = new TologOptions(TologOptions.defaults);
    options.setOption("optimizer.role-player-type",
                      "" + !(topicmap instanceof RDBMSTopicMapStore));
    options.setOption("optimizer.next-previous",
                      "" + !(topicmap instanceof RDBMSTopicMapStore));
    options.loadProperties(); // loads tolog.properties from classpath
    
    ParseContextIF context = new GlobalParseContext(new PredicateFactory(
        topicmap, base), topicmap, base);
    context = new LocalParseContext(context);
    parser = new TologParser(context, options);
  }

  public TologOptions getOptions() {
    return options;
  }
  
  // / query processor implementation

  @Override
  public QueryResultIF execute(String query) throws InvalidQueryException {
    return execute(parseQuery(query, null));
  }

  @Override
  public QueryResultIF execute(String query, DeclarationContextIF context)
      throws InvalidQueryException {
    return execute(parseQuery(query, context));
  }

  @Override
  public QueryResultIF execute(String query, Map arguments)
      throws InvalidQueryException {
    return execute(parseQuery(query, null), arguments);
  }

  @Override
  public QueryResultIF execute(String query, Map arguments,
      DeclarationContextIF context) throws InvalidQueryException {
    return execute(parseQuery(query, context), arguments);
  }

  @Override
  public ParsedQueryIF parse(String query) throws InvalidQueryException {
    return new ParsedQuery(this, parseQuery(query, null));
  }

  @Override
  public ParsedQueryIF parse(String query, DeclarationContextIF context)
      throws InvalidQueryException {
    return new ParsedQuery(this, parseQuery(query, context));
  }

  protected TologQuery parseQuery(String query, DeclarationContextIF context)
      throws InvalidQueryException {

    if (context == null)
      // there is no context, so we just use the default parser
      return optimize(parser.parseQuery(query));

    // there is a context, so we have to use a new parser for this
    TologParser localparser = new TologParser((ParseContextIF) context, options);
    return optimize(localparser.parseQuery(query));
  }

  @Override
  public void load(String ruleset) throws InvalidQueryException {
    parser.load(ruleset);
  }

  @Override
  public void load(Reader ruleset) throws InvalidQueryException, IOException {
    parser.load(ruleset);
  }

  // / query execution code

  public QueryResultIF execute(TologQuery query) throws InvalidQueryException {
    return execute(query, null);
  }

  public QueryResultIF execute(TologQuery query, Map arguments)
      throws InvalidQueryException {

    long start = System.currentTimeMillis();
    QueryAnalyzer.verifyParameters(query, arguments);

    if (logger.isDebugEnabled())
      logger.debug("Parsed query: " + query);

    QueryMatches matches;

    QueryTracer.startQuery();
    try {
      matches = createInitialMatches(query, arguments);
      List clauses = query.getClauses();
      
      matches = satisfy(clauses, matches);
      
      matches = reduce(query, matches);
      
      matches = count(query, matches);
      
      sort(query, matches);
    } finally {
      QueryTracer.endQuery();
    }
    TologSpy.recordExecute(query, start, System.currentTimeMillis());
    
    return new QueryResult(matches, query.getLimit(), query.getOffset());
  }

  @Override
  public int update(String query) throws InvalidQueryException {
    return update(query, null, null);
  }

  @Override
  public int update(String query, DeclarationContextIF context)
    throws InvalidQueryException {
    return update(query, null, context);
  }
  
  @Override
  public int update(String query, Map<String, ?> params)
    throws InvalidQueryException {
    return update(query, params, null);
  }

  @Override
  public int update(String query, Map<String, ?> params,
                    DeclarationContextIF context)
    throws InvalidQueryException {
    return runUpdate(parseUpdateStatement(query, context), params);
  }

  @Override
  public ParsedModificationStatementIF parseUpdate(String statement)
    throws InvalidQueryException {
    return parseUpdate(statement, null);
  }

  @Override
  public ParsedModificationStatementIF parseUpdate(String statement,
                                                   DeclarationContextIF context)
    throws InvalidQueryException {
    return new ParsedModificationStatement(parseUpdateStatement(statement,
                                                                context));
  }

  protected ModificationStatement parseUpdateStatement(String statement,
                                                       DeclarationContextIF ctx)
    throws InvalidQueryException {
    if (ctx == null)
      return (ModificationStatement) parser.parseStatement(statement);
    else {
      // there is a context, so we have to use a new parser for this
      TologParser localparser = new TologParser((ParseContextIF) ctx, options);
      return (ModificationStatement) localparser.parseStatement(statement);
    }
  }

  protected int runUpdate(ModificationStatement statement, Map<String, ?> params)
    throws InvalidQueryException {
    if (statement.getEmbeddedQuery() != null) {
      TologQuery subquery = optimize(statement.getEmbeddedQuery());
      QueryTracer.startQuery();
      try {
        QueryMatches matches = createInitialMatches(subquery, params);
        matches = satisfy(subquery.getClauses(), matches);
        matches = reduce(subquery, matches);
        return statement.doUpdates(matches);
      } finally {
        QueryTracer.endQuery();
      }
    } else
      return statement.doStaticUpdates(topicmap, params);
  }
  
  // / actual query processor implementation

  // satisfy lives in AbstractQueryProcessor

  // takes the query and sets up the matches table with a single row
  // ready for use
  public QueryMatches createInitialMatches(TologQuery query, Map arguments) {
    Collection items = findClauseItems(query.getClauses(), arguments);
    return createInitialMatches(query, items, arguments);
  }

  public QueryMatches createInitialMatches(TologQuery query, Collection items,
                                           Map arguments) {
    QueryContext context = new QueryContext(topicmap, query, arguments, query.getOptions());
    return QueryMatchesUtils.createInitialMatches(context, items);
  }

  /**
   * INTERNAL: Projects the query results onto the set of variables specified in
   * the 'select' clause. If there is no 'select' clause nothing is done.
   * 
   * @param query The parsed query.
   * @param matches The query result.
   */
  public QueryMatches reduce(TologQuery query, QueryMatches matches) {
    // WARNING: method used by rdbms tolog
    if (!query.hasSelectClause() && !matches.hasLiteralColumns())
      return matches; // only run if no select clause

    QueryTracer.enterSelect(matches);

    List projection = new ArrayList();
    int[] varix = new int[query.getSelectedVariables().size()];
    for (int ix = 0; ix < varix.length; ix++) {
      Variable var = (Variable) query.getSelectedVariables().get(ix);
      projection.add(var);
      varix[ix] = matches.getIndex(var);
    }

    QueryMatches result = new QueryMatches(projection, matches
        .getQueryContext());
    Set alreadyAdded = new CompactHashSet();
    Object[][] mdata = matches.data;
    Object[][] rdata = result.data;
    ArrayWrapper wrapper = new ArrayWrapper(); // for instance reuse...

    result.last = 0; // we use one row too many all the way through
    for (int row = 0; row <= matches.last; row++) {
      for (int col = 0; col < varix.length; col++)
        rdata[result.last][col] = mdata[row][varix[col]];

      wrapper.setArray(rdata[result.last]); // reuse previous wrapper
      if (!alreadyAdded.contains(wrapper)) {
        alreadyAdded.add(wrapper);
        wrapper = new ArrayWrapper(); // can't reuse, so make new wrapper
        if (result.last + 1 == result.size) {
          result.increaseCapacity();
          rdata = result.data;
        }
        result.last++;
      }
    }
    result.last--; // reclaim the temporary last row

    QueryTracer.leaveSelect(result);

    return result;
  }

  /**
   * INTERNAL: Replaces count($A) variables by their relevant counts.
   * 
   * @param query The parsed query.
   * @param matches The query result.
   */
  public QueryMatches count(TologQuery query, QueryMatches matches) {
    // WARNING: method used by rdbms tolog
    if (query.getCountedVariables().isEmpty())
      return matches;

    Collection countVars = query.getCountedVariables();
    int[] countcols = new int[countVars.size()];
    int ix = 0;
    for (Iterator it = countVars.iterator(); it.hasNext();)
      countcols[ix++] = matches.getIndex(it.next());

    // fixes issue 80: return 0 if the query did not match anything, and
    //                 the select clauses contain only counted variables
    if (countVars.size() == matches.colcount && 
        matches.last == -1 && matches.size == 1) {
      Object[] row = matches.data[matches.size - 1];
      for (int i = 0; i < matches.colcount; i++)
        row[i] = new Integer(0);
      matches.last = 0;
      return matches;
    }

    // do the actual counting
    ArrayWrapper wrapper = new ArrayWrapper(); // for instance reuse...
    Map counters = new HashMap();

    for (ix = 0; ix <= matches.last; ix++) {
      Object[] row = matches.data[ix];
      boolean nonnull = false; // is at least one counted value non-null?
      for (int i = 0; i < countcols.length; i++) {
        if (row[countcols[i]] != null)
          nonnull = true;
        row[countcols[i]] = null;
      }

      wrapper.setArray(row);
      Counter counter;
      if (counters.containsKey(wrapper))
        counter = (Counter) counters.get(wrapper);
      else {
        counter = new Counter();
        counters.put(wrapper, counter);
        wrapper = new ArrayWrapper();
      }
      if (nonnull)
        counter.counter++; // only count if we have non-null counted values
    }

    // replace variable values in result set with counts
    int next = 0; // next row to use
    Iterator it = counters.keySet().iterator();
    while (it.hasNext()) {
      wrapper = (ArrayWrapper) it.next();
      Object[] row = wrapper.row;
      Counter count = (Counter) counters.get(wrapper);

      for (int i = 0; i < countcols.length; i++)
        row[countcols[i]] = new Integer(count.counter);

      matches.data[next++] = row; // no need to expand...
    }
    
    matches.last = next - 1;
    return matches;
  }

  /**
   * INTERNAL: Sorts the query result as requested.
   * 
   * @param query The parsed query.
   * @param matches The query result.
   */
  public void sort(TologQuery query, QueryMatches matches) {
    // WARNING: method used by rdbms tolog
    if (query.getOrderBy().isEmpty())
      return;
    if (matches.isEmpty()) // no use sorting an empty table
      return;

    QueryTracer.enterOrderBy();
    java.util.Arrays.sort(matches.data, 0, matches.last + 1, new RowComparator(
        query, matches));
    QueryTracer.leaveOrderBy();
  }

  /**
   * Optimizes the query before executing it.
   */
  private TologQuery optimize(TologQuery query) throws InvalidQueryException {
    return QueryOptimizer.getOptimizer(query).optimize(query);
  }

  // --- Internal classes

  class Counter {
    public int counter = 0;
  }

  class RowComparator implements java.util.Comparator {    
    private int[] orderColumns;
    private int[] orderType;
    private boolean[] isAscending;
    private Function<TopicIF, String> sort;

    private final static int ORDER_UNKNOWN = -1;
    private final static int ORDER_TOPIC = 0;
    private final static int ORDER_STRING = 1;
    private final static int ORDER_OBJECT = 2;
    private final static int ORDER_INT = 3;
    private final static int ORDER_FLOAT = 4;
    private final static int ORDER_NUMBERS = 5;

    public RowComparator(TologQuery query, QueryMatches result) {
      Collection counted = query.getCountedVariables();
      int orderVars = query.getOrderBy().size();
      orderColumns = new int[orderVars];
      orderType = new int[orderVars];
      isAscending = new boolean[orderVars];

      for (int ix = 0; ix < orderVars; ix++) {
        Variable orderBy = (Variable) query.getOrderBy().get(ix);
        orderColumns[ix] = result.getIndex(orderBy);
        Object[] types = (Object[]) query.getVariableTypes().get(
            orderBy.getName());
        if (counted.contains(orderBy))
          orderType[ix] = ORDER_INT;
        else if (types == null) // we don't know the type of the variable
          orderType[ix] = ORDER_UNKNOWN;
        else if (types.length > 1) { // multiple types (possibly TMObjectIFs)
          // types might hold only Number classes, treat those separately
          boolean onlyNumbers = true;
          for (Object type : types) {
            onlyNumbers &= Number.class.isAssignableFrom((Class) type);
          }
          orderType[ix] = onlyNumbers ? ORDER_NUMBERS : ORDER_OBJECT;
        } else if (types[0].equals(String.class))
          orderType[ix] = ORDER_STRING;
        else if (types[0].equals(TopicIF.class)) {
          orderType[ix] = ORDER_TOPIC;

          Prefetcher.prefetch(topicmap, result, orderColumns[ix],
              Prefetcher.TopicIF, Prefetcher.TopicIF_topicmap, false);

          Prefetcher.prefetch(topicmap, result, orderColumns[ix],
              Prefetcher.TopicIF, Prefetcher_OB_fields, Prefetcher_OB_traverse);

        } 
        else if (types[0].equals(Integer.class))
          orderType[ix] = ORDER_INT;
        else if (types[0].equals(Float.class))
          orderType[ix] = ORDER_FLOAT;
        else
          orderType[ix] = ORDER_OBJECT; // single type (possibly TMObjectIF)

        isAscending[ix] = query.isOrderedAscending(orderBy.getName());
      }

      TopicMapIF tm = result.getQueryContext().getTopicMap();
      sort = TopicStringifiers.getFastSortNameStringifier(tm);
    }

    @Override
    public int compare(Object o1, Object o2) {
      Object[] row1 = (Object[]) o1;
      Object[] row2 = (Object[]) o2;

      int comp = 0;
      int ix;
      for (ix = 0; comp == 0 && ix < orderColumns.length; ix++) {
        // null checks first
        if (row1[orderColumns[ix]] == null) {
          if (row2[orderColumns[ix]] == null)
            comp = 0;
          else
            comp = -1;
          continue;
        } else if (row2[orderColumns[ix]] == null) {
          comp = 1;
          continue;
        }

        // no nulls, we can compare
        switch (orderType[ix]) {
        case ORDER_TOPIC:
          if (row1[orderColumns[ix]] == row2[orderColumns[ix]])
            comp = 0;
          else {
            String name1 = sort.apply((TopicIF) row1[orderColumns[ix]]);
            String name2 = sort.apply((TopicIF) row2[orderColumns[ix]]);
            
            if (name1 == null)
              comp = name2 == null ? 0 : -1;
            else if (name2 == null)
              comp = 1;
            else
              comp = (collator != null ?
                      collator.compare(name1, name2) :
                      name1.compareTo(name2));
          }
          break;
        case ORDER_INT:
          comp = ((Integer) row1[orderColumns[ix]]).intValue()
              - ((Integer) row2[orderColumns[ix]]).intValue();
          break;
        case ORDER_FLOAT:
          Float f1 = (Float) row1[orderColumns[ix]];
          Float f2 = (Float) row2[orderColumns[ix]];
          comp = f1.compareTo(f2);        
          break;
        case ORDER_STRING:          
          comp = (collator != null ?
                  collator.compare((String)row1[orderColumns[ix]], (String)row2[orderColumns[ix]]) :
                  ((Comparable) row1[orderColumns[ix]]).compareTo(row2[orderColumns[ix]]));
          break;
        case ORDER_OBJECT:
          // if both objects are topic then sort them as topics
          if (row1[orderColumns[ix]] instanceof TopicIF &&
              row2[orderColumns[ix]] instanceof TopicIF) {
            if (row1[orderColumns[ix]] == row2[orderColumns[ix]])
              comp = 0;
            else {
              String name1 = sort.apply((TopicIF) row1[orderColumns[ix]]);
              String name2 = sort.apply((TopicIF) row2[orderColumns[ix]]);
              
              if (name1 == null)
                comp = name2 == null ? 0 : -1;
              else if (name2 == null)
                comp = 1;
              else
                comp = (collator != null ?
                        collator.compare(name1, name2) :
                        name1.compareTo(name2));
            }
          } else {
            Object x1 = row1[orderColumns[ix]];
            Object x2 = row2[orderColumns[ix]];
            String id1 = (x1 instanceof TMObjectIF ? ((TMObjectIF) x1).getObjectId() : String.valueOf(x1));
            String id2 = (x2 instanceof TMObjectIF ? ((TMObjectIF) x2).getObjectId() : String.valueOf(x2));
            comp = id1.compareTo(id2);
          }
          break;
        case ORDER_NUMBERS:
          // cast to highest precision to sort
          Double n1 = ((Number) row1[orderColumns[ix]]).doubleValue();
          Double n2 = ((Number) row2[orderColumns[ix]]).doubleValue();
          comp = n1.compareTo(n2);
          break;
        case ORDER_UNKNOWN:
          throw new OntopiaRuntimeException(
              "INTERNAL ERROR: Could not infer type " + "of column "
                  + orderColumns[ix] + ". "
                  + "Please report to <support@ontopia.net>.");
        default:
          // unknown kind of ordering. complain!
          throw new OntopiaRuntimeException("INTERNAL ERROR: Unknown ordering"
              + " type " + orderType[ix] + " in position " + ix);
        }
      }

      ix--; // get back to previous value
      if (comp != 0 && !isAscending[ix])
        comp *= -1;
      return comp;
    }
  }

  // We have to use this to get meaningful implementations of
  // hashCode() and equals() for arrays. Arrays have these methods,
  // but they are, stupidly, the same as for Object.

  final class ArrayWrapper {
    public Object[] row;

    private int hashCode;

    public void setArray(Object[] row) {
      this.row = row;

      hashCode = 0;
      for (int ix = 0; ix < row.length; ix++)
        if (row[ix] != null)
          hashCode = (hashCode + row[ix].hashCode()) & 0x7FFFFFFF;
    }

    @Override
    public int hashCode() {
      return hashCode;
    }

    @Override
    public boolean equals(Object o) {
      // this class is only used here, so we are making some simplifying
      // assumptions:
      // - o is not null
      // - o is an ArrayWrapper
      // - o contains an Object[] array of the same length as row
      Object[] orow = ((ArrayWrapper) o).row;
      for (int ix = 0; ix < orow.length; ix++)
        if (orow[ix] != null && !orow[ix].equals(row[ix]))
          return false;
      return true;
    }
  }

  // -- Collation handling

  private Collator getCollator(TopicMapIF tm) {
    if (tm.getStore().getImplementation() == TopicMapStoreIF.RDBMS_IMPLEMENTATION) {
      // look up locale settings in properties file
      RDBMSTopicMapStore store = (RDBMSTopicMapStore) tm.getStore();
      String locale = store.getProperty("net.ontopia.topicmaps.query.core.QueryProcessorIF.locale");
      Collator c = getCollator(locale);
      if (c != null) return c;
    }
    // fallback to using system property
    try {
      return getCollator(System.getProperty("net.ontopia.topicmaps.query.core.QueryProcessorIF.locale"));
    } catch (SecurityException e) {
      return null;
    }
  }

  private Locale getLocale(String _locale) {
    if (_locale == null) return null;

    String language = null;
    String country = null;
    String variant = null;
    
    String[] locale = StringUtils.split(_locale, "_");
    if (locale.length >= 1)
      language = locale[0];
    if (locale.length >= 2)
      country = locale[1];
    if (locale.length >= 3)
      variant = locale[2];
    
    if (country == null) country = "";
    if (variant == null) variant = "";

    return new Locale(language, country, variant);    
  }
  
  private Collator getCollator(String _locale) {
    Locale locale = getLocale(_locale);
    if (locale == null) return null;
    return Collator.getInstance(locale);
  }

  // -- ParsedModificationStatement

  class ParsedModificationStatement implements ParsedModificationStatementIF {
    private ModificationStatement stmt;

    private ParsedModificationStatement(ModificationStatement stmt) {
      this.stmt = stmt;
    }

    @Override
    public int update() throws InvalidQueryException {
      return runUpdate(stmt, null);
    }

    @Override
    public int update(Map<String, ?> params) throws InvalidQueryException {
      return runUpdate(stmt, params);
    }

    @Override
    public String toString() {
      return stmt.toString();
    }
  }
}
