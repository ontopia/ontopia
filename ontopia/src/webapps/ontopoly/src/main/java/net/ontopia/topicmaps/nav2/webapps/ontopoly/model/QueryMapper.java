package net.ontopia.topicmaps.nav2.webapps.ontopoly.model;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.query.core.DeclarationContextIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.utils.RowMapperIF;
import net.ontopia.utils.OntopiaRuntimeException;


public class QueryMapper<T> {
  
  private Class<T> type;
  private DeclarationContextIF context;
  private QueryProcessorIF processor;
  private TopicMap topicMap;
  
  public QueryMapper(TopicMap topicMap, QueryProcessorIF processor, DeclarationContextIF context) {
    this(topicMap, processor, context, null);   
  }
  
  public QueryMapper(TopicMap topicMap, QueryProcessorIF processor, DeclarationContextIF context, Class<T> type) {
    this.topicMap = topicMap;
    this.type = type;
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
   * returned. If the query produces more than one row or more than
   * one column, an exception is thrown.
   */
  @SuppressWarnings("unchecked")
  public T queryForObject(String query, Map<String,?> params) {
    QueryResultIF result = null;
    try {
      result = processor.execute(query, params, context);
      if (result.next())
        return (T)result.getValue(0);
      else
        return null;
      // if (result.next()) throw new OntopolyModelRuntimeException("Got more than one result row.");
    } catch (InvalidQueryException e) {
      throw new OntopiaRuntimeException(e);
    } finally {
      if (result != null)
        result.close();
    }
  }
  
  /**
   * EXPERIMENTAL: Returns the mapping of the value in the first
   * column in the first row of the query result. If the query
   * produces no results null is returned. If the query produces more
   * than one row or more than one column, an exception is thrown.
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
      // if (result.next()) throw new OntopolyModelRuntimeException("Got more than one result row.");
    } catch (InvalidQueryException e) {
      throw new OntopiaRuntimeException(e);
    } finally {
      if (result != null)
        result.close();
    }
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
   * rows the method returns null; if it produces more than one an
   * exception is thrown.
   */
  public Map queryForMap(String query, Map<String,?> params) {
    QueryResultIF result = null;
    try {
      result = processor.execute(query, params, context);
      if (result.next()) {
        Map<String,Object> row = new HashMap<String,Object>(result.getWidth());
        for (int ix = 0; ix < result.getWidth(); ix++)
          row.put(result.getColumnName(ix), result.getValue(ix));
        return row;

      } else {
        return null;
      }
      // if (result.next()) throw new OntopolyModelRuntimeException("Got more than one result row.");
    } catch (InvalidQueryException e) {
      throw new OntopiaRuntimeException(e);
    } finally {
      if (result != null)
        result.close();
    }

  }
  
  public RowMapperIF<T> newRowMapperOneColumn() {
    // hardcoded to return the value in the first column
    return new RowMapperIF<T>() {
      public T mapRow(QueryResultIF queryResult, int rowno) {
        return wrapValue(queryResult.getValue(0));
      }
    };
  }

  @SuppressWarnings("unchecked")
  private T wrapValue(Object value) {
    // don't wrap if type is null
    if (type == null) return (T)value;
    
    // if (value == null) return null;
    try {      
      Constructor<T> constructor = getConstructor();
      if (constructor == null) {
        throw new OntopolyModelRuntimeException("Couldn't find constructor for the class: " + type);
      }
      return constructor.newInstance(new Object[] { value, topicMap });
    } catch (Exception e) {
      throw new OntopolyModelRuntimeException(e);
    }
  }
  
  private Constructor<T> getConstructor() throws SecurityException, NoSuchMethodException {
    return type.getConstructor(TopicIF.class, TopicMap.class); 
  }
  
}
