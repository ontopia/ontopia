package net.ontopia.topicmaps.nav2.webapps.ontopoly.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ontopia.topicmaps.query.core.DeclarationContextIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.utils.RowMapperIF;
import net.ontopia.utils.OntopiaRuntimeException;


public class QueryMapper<T> {
  
  private DeclarationContextIF context;
  private QueryProcessorIF processor;
  
  private final RowMapperIF<T> FIRST_COLUMN_MAPPER = new RowMapperIF<T>() {
    public T mapRow(QueryResultIF queryResult, int rowno) {
      return wrapValue(queryResult.getValue(0));
    }
  };
  
  public QueryMapper(QueryProcessorIF processor, DeclarationContextIF context) {
    this.processor = processor;
    this.context = context;
  } 
  /**
   * EXPERIMENTAL: Returns true if the query produces a row and
   * false if the query produces no rows. If the query produces 
   * more than one row an exception is thrown.  
   */
 public boolean isTrue(String query, Map<String,?> params) {
   QueryResultIF result = null;
   try {
     result = processor.execute(query, params, context);
     return result.next();
   } catch (InvalidQueryException e) {
     throw new OntopiaRuntimeException(e);
   } finally {
     if (result != null)
       result.close();
   }
}
 
 /**
  * EXPERIMENTAL: Returns the value in the first column in the first
  * row of the query result. If the query produces no results null is
  * returned.
  */
 public T queryForObject(String query) {
   return queryForObject(query, FIRST_COLUMN_MAPPER, null);
 }
  
  /**
   * EXPERIMENTAL: Returns the value in the first column in the first
   * row of the query result. If the query produces no results null is
   * returned.
   */
  public T queryForObject(String query, Map<String,?> params) {
    return queryForObject(query, FIRST_COLUMN_MAPPER, params);
  }
  
  /**
   * EXPERIMENTAL: Returns the value in the first column in the first
   * row of the query result. If the query produces no results null is
   * returned.
   */
  public T queryForObject(String query, RowMapperIF<T> mapper) {
    return queryForObject(query, mapper, null);
  }
  
  /**
   * EXPERIMENTAL: Returns the mapping of the value in the first
   * column in the first row of the query result. If the query
   * produces no results null is returned.
   */
  public T queryForObject(String query, RowMapperIF<T> mapper, Map<String,?> params) {
    QueryResultIF result = null;
    try {
      result = processor.execute(query, params, context);
      int ix=0;
      if (result.next())
        return mapper.mapRow(result, ix++);
      else
        return null;
    } catch (InvalidQueryException e) {
      throw new OntopiaRuntimeException(e);
    } finally {
      if (result != null)
        result.close();
    }
  }

  /**
   * EXPERIMENTAL: Runs the query, and returns a the single value in
   * each row.
   */
  public List<T> queryForList(String query) {
    return queryForList(query, FIRST_COLUMN_MAPPER, null);
  }
  
  /**
   * EXPERIMENTAL: Runs the query, and calls the mapper for each row
   * in the query result. Returns a list of all the objects produced
   * by the mapper in query result order.
   */
  public List<T> queryForList(String query, RowMapperIF<T> mapper) {
    return queryForList(query, mapper, null);
  }

  /**
   * EXPERIMENTAL: Runs the query, and returns a the single value in
   * each row.
   */
  public List<T> queryForList(String query, Map<String,?> params) {
    return queryForList(query, FIRST_COLUMN_MAPPER, params);
  }
 
  /**
   * EXPERIMENTAL: Runs the query with the given parameters, and calls
   * the mapper for each row in the query result. Returns a list of
   * all the objects produced by the mapper in query result order.
   */
  public List<T> queryForList(String query, RowMapperIF<T> mapper, Map<String,?> params) {
    List<T> list = new ArrayList<T>();
    QueryResultIF result = null;
    try {
      result = (params == null ? processor.execute(query, context) : processor.execute(query, params, context));
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
   * rows the method returns null.
   */
  public Map queryForMap(String query, Map<String,?> params) {
    QueryResultIF result = null;
    try {
      result = processor.execute(query, params, context);
      if (result.next()) {
        Map<String,Object> row = new HashMap<String,Object>(result.getWidth());
        for (int ix = 0; ix < result.getWidth(); ix++)
          row.put(result.getColumnName(ix), wrapValue(result.getValue(ix)));
        return row;

      } else {
        return null;
      }
    } catch (InvalidQueryException e) {
      throw new OntopiaRuntimeException(e);
    } finally {
      if (result != null)
        result.close();
    }

  }

  @SuppressWarnings("unchecked")
  protected T wrapValue(Object value) {
    return (T)value;
  }
  
}
