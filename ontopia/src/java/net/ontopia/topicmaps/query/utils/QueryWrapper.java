
// $Id: QueryWrapper.java,v 1.10 2008/10/24 11:40:38 geir.gronmo Exp $

package net.ontopia.topicmaps.query.utils;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.utils.QueryUtils;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.core.DeclarationContextIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;

/**
 * EXPERIMENTAL: Class a la Spring's JDBC templates to simplify use of
 * the tolog query engine API.
 * @since 3.4.4
 */
public class QueryWrapper {
  private TopicMapIF topicmap;
  private DeclarationContextIF context;
  private QueryProcessorIF processor;

  /**
   * EXPERIMENTAL: Creates a wrapper for this particular topic map.
   */
  public QueryWrapper(TopicMapIF topicmap) {
    this.topicmap = topicmap;
    this.processor = QueryUtils.getQueryProcessor(topicmap);
  }
  
  public QueryProcessorIF getQueryProcessor() {
    return processor;
  }

  public DeclarationContextIF getDeclarationContext() {
    return context;
  }

  /**
   * EXPERIMENTAL: Sets the parsing context for the query processor.
   * Each call to this method overwrites the results from previous
   * calls.
   * @param declarations a tolog fragment containing prefix declarations
   */
  public void setDeclarations(String declarations) {
    try {
      if (context == null)
        context = QueryUtils.parseDeclarations(topicmap, declarations);
      else
        context = QueryUtils.parseDeclarations(topicmap, declarations, context);
    } catch (InvalidQueryException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  /**
   * EXPERIMENTAL: Utility method for creating parameter maps.
   */
  public Map makeParams(String name, Object value) {
    Map params = new HashMap(1);
    params.put(name, value);
    return params;
  }

  /**
   * EXPERIMENTAL: Runs the query, and returns a the single value in
   * each row.
   */
  public List queryForList(String query) {
    return queryForList(query, new ObjectMapper(false), null);
  }
  
  /**
   * EXPERIMENTAL: Runs the query, and returns a the single value in
   * each row.
   */
  public List queryForList(String query, Map params) {
    return queryForList(query, new ObjectMapper(false), params);
  }
  
  /**
   * EXPERIMENTAL: Runs the query, and calls the mapper for each row
   * in the query result. Returns a list of all the objects produced
   * by the mapper in query result order.
   */
  public List queryForList(String query, RowMapperIF mapper) {
    return queryForList(query, mapper, null);
  }
  
  /**
   * EXPERIMENTAL: Runs the query with the given parameters, and calls
   * the mapper for each row in the query result. Returns a list of
   * all the objects produced by the mapper in query result order.
   */
  public List queryForList(String query, RowMapperIF mapper, Map params) {
    List list = new ArrayList();
    QueryResultIF result = null;
    try {
      result = processor.execute(query, params, context);
      int ix = 0;
      while (result.next())
        list.add(mapper.mapRow(result, ix++));
    } catch (InvalidQueryException e) {
      throw new OntopiaRuntimeException(e);
    } finally {
      if (result != null)
        result.close();
    }
    return list;
  }

  /**
   * EXPERIMENTAL: Returns a map of the first row of the query
   * results, with each variable name (without $) as a key and each
   * variable value as the value of the key. If the query produces no
   * rows the method returns null; if it produces more than one an
   * exception is thrown.
   */
  public Map queryForMap(String query) {
    return queryForMap(query, null);
  }
  
  /**
   * EXPERIMENTAL: Returns a map of the first row of the query
   * results, with each variable name (without $) as a key and each
   * variable value as the value of the key. If the query produces no
   * rows the method returns null; if it produces more than one an
   * exception is thrown.
   */
  public Map queryForMap(String query, Map params) {
    MapMapper mapper = new MapMapper();
    List list = queryForList(query, mapper, params);
    int size = list.size();
    if (size == 0)
      return null;
    else if (size == 1)
      return (Map) list.get(0);
    else
      throw new OntopiaRuntimeException("Query produced more than one row");
  }
 
  /**
   * EXPERIMENTAL: Write!
   */
  public List queryForMaps(String query, Map params) {
    return queryForList(query, new MapMapper(false), params);
  }
 
  private class MapMapper implements RowMapperIF {
    private boolean maxone;

    public MapMapper() {
    }
    
    public MapMapper(boolean maxone) {
      this.maxone = maxone;
    }
    
    public Object mapRow(QueryResultIF result, int rowno) {
      if (maxone && rowno == 1)
        throw new OntopiaRuntimeException("Query produced more than one row");
      Map row = new HashMap(result.getWidth());
      for (int ix = 0; ix < result.getWidth(); ix++)
        row.put(result.getColumnName(ix), result.getValue(ix));
      return row;
    }
  }

  /**
    * EXPERIMENTAL: Returns true if the query produces a row and
    * false if the query produces no rows. If the query produces 
    * more than one row an exception is thrown.  
    */
  public boolean isTrue(String query) {
    return isTrue(query, null);
  }
  
  /**
    * EXPERIMENTAL: Returns true if the query produces a row and
    * false if the query produces no rows. If the query produces 
    * more than one row an exception is thrown.  
    */
  public boolean isTrue(String query, Map params) {
    List list = queryForList(query, new RowMapperIF(){
      public Object mapRow(QueryResultIF result, int rowno) {
        if (rowno == 1)
          throw new OntopiaRuntimeException("Query produced more than one row");
        return new Object();
      }
    }, params);

    return !list.isEmpty();
  }

  /**
   * EXPERIMENTAL: Returns a String from the first column in the first
   * row of the query result. If the query produces no results null is
   * returned. If the query produces more than one row or more than
   * one column, an exception is thrown.
   */
  public String queryForString(String query) {
    return queryForString(query, null);
  }  
  
  /**
   * EXPERIMENTAL: Returns a String from the first column in the first
   * row of the query result. If the query produces no results null is
   * returned. If the query produces more than one row or more than
   * one column, an exception is thrown.
   */
  public String queryForString(String query, Map params) {
    return (String) queryForObject(query, params);
  }  

  /**
   * EXPERIMENTAL: Returns a topic from the first column in the first
   * row of the query result. If the query produces no results null is
   * returned. If the query produces more than one row or more than
   * one column, an exception is thrown.
   */
  public TopicIF queryForTopic(String query) {
    return queryForTopic(query, null);
  }  
  
  /**
   * EXPERIMENTAL: Returns a topic from the first column in the first
   * row of the query result. If the query produces no results null is
   * returned. If the query produces more than one row or more than
   * one column, an exception is thrown.
   */
  public TopicIF queryForTopic(String query, Map params) {
    return (TopicIF) queryForObject(query, params);
  }
  
  /**
   * EXPERIMENTAL: Returns the value in the first column in the first
   * row of the query result. If the query produces no results null is
   * returned. If the query produces more than one row or more than
   * one column, an exception is thrown.
   */
  public Object queryForObject(String query) {
    return queryForObject(query, null, null);
  }  
  
  /**
   * EXPERIMENTAL: Returns the mapping of the value in the first
   * column in the first row of the query result. If the query
   * produces no results null is returned. If the query produces more
   * than one row or more than one column, an exception is thrown.
   */
  public Object queryForObject(String query, RowMapperIF mapper) {
    return queryForObject(query, mapper, null);
  }  
  
  /**
   * EXPERIMENTAL: Returns the value in the first column in the first
   * row of the query result. If the query produces no results null is
   * returned. If the query produces more than one row or more than
   * one column, an exception is thrown.
   */
  public Object queryForObject(String query, Map params) {
    return queryForObject(query, new ObjectMapper(), params);
  }

  /**
   * EXPERIMENTAL: Returns the mapping of the value in the first
   * column in the first row of the query result. If the query
   * produces no results null is returned. If the query produces more
   * than one row or more than one column, an exception is thrown.
   */
  public Object queryForObject(String query, RowMapperIF mapper, Map params) {
    List list = queryForList(query, mapper, params);
    if (list.isEmpty())
      return null;
    else
      return list.iterator().next();
  }
  
  class ObjectMapper implements RowMapperIF {
    private boolean maxone;
    
    public ObjectMapper() {
      this.maxone = true;
    }

    public ObjectMapper(boolean maxone) {
      this.maxone = maxone;
    }
    
    public Object mapRow(QueryResultIF result, int rowno) {
      if (maxone && rowno == 1)
        throw new OntopiaRuntimeException("Query produced more than one row");
      return result.getValue(0);
    }
  }
}
