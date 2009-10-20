package net.ontopia.topicmaps.query.toma.impl.basic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.collections.Bag;
import org.apache.commons.collections.bag.HashBag;

/**
 * INTERNAL: This is a simple implementation of a table.
 */
public class ResultSet implements Iterable<Row> {

  private Vector<String> columns;
  private Bag rows;
  private boolean unique;

  public ResultSet() {
    this(0, false);
  }

  public ResultSet(int cols, boolean unique) {
    columns = new Vector<String>(cols);
    columns.setSize(cols);
    rows = new HashBag();
    this.unique = unique;
  }

  /**
   * Create a new ResultSet based on the definition from one or more other
   * ResultSets.
   * 
   * @param others
   */
  public ResultSet(ResultSet... others) {
    int cols = 0;
    for (ResultSet rs : others) {
      cols += rs.getColumnCount();
    }
    columns = new Vector<String>(cols);
    columns.setSize(cols);

    int i = 0;
    for (ResultSet rs : others) {
      for (int j = 0; j < rs.getColumnCount(); j++, i++) {
        setColumnName(i, rs.getColumnName(j));
      }
    }

    rows = new HashBag();
  }

  /**
   * Get an unmodifiable list of the columns for this ResultSet.
   *  
   * @return
   */
  public List<String> getColumnDefinitions() {
    return Collections.unmodifiableList(columns);
  }
  
  public List<String> getSharedColumns(ResultSet rs) {
    List<String> sharedCols = new ArrayList<String>();
    
    List<String> vars = getBoundVariables();
    for (String var : vars) {
      if (rs.containsColumn(var)) {
        sharedCols.add(var);
      }
    }
    
    return sharedCols;
  }
  
  public List<String> getBoundVariables() {
    List<String> variables = new ArrayList<String>();
    for (String col : columns) {
      if (col.startsWith("$") && !col.contains(".")) {
        variables.add(col);
      }
    }
    return variables;
  }

  public int getRowCount() {
    return rows.size();
  }

  public int getColumnCount() {
    return columns.size();
  }
  
  /**
   * Convenience method the 
   * @return
   */
  public int getLastIndex() {
    return columns.size() - 1;
  }

  public void addColumn(String name) {
    columns.add(name);
    for (Object r : rows) {
      ((Row) r).addColumn();
    }
  }

  @SuppressWarnings("unchecked")
  public Iterator<Row> iterator() {
    if (unique) {
      return rows.uniqueSet().iterator();
    } else {
      return rows.iterator();
    }
  }

  public Row createRow() {
    Row r = new Row(getColumnCount());
    return r;
  }

  public Row mergeRow(Row... rows) {
    Row newRow = createRow();

    int i = 0;
    for (Row r : rows) {
      for (int j = 0; j < r.getColumnCount(); j++, i++) {
        newRow.setValue(i, r.getValue(j));
      }
    }

    return newRow;
  }

  public void addRow(Row row) {
    rows.add(row);
  }

  /**
   * Adds all rows from the other ResultSet to this one. Note: the layout of the
   * two ResultSets has to be the same, otherwise this operation will fail.
   * 
   * @param other the ResultSet to be added. 
   */
  @SuppressWarnings("unchecked")
  public void addAll(ResultSet other) {
    rows.addAll(other.rows);
  }

  public void removeRow(Row row) {
    rows.remove(row);
  }

  public boolean containsRow(Row row) {
    return rows.contains(row);
  }
  
  public void clear() {
    rows.clear();
  }
  
  /**
   * 
   * @param index
   * @return
   * @throws IndexOutOfBoundsException
   */
  public String getColumnName(int index) throws IndexOutOfBoundsException {
    if (index < 0 || index >= columns.size()) {
      throw new IndexOutOfBoundsException("No column available for index '"
          + index + "'");
    }
    return columns.get(index);
  }

  public void setColumnName(int index, String name) {
    columns.set(index, name);
  }

  public boolean containsColumn(String name) {
    return (getColumnIndex(name) > -1);
  }

  public int getColumnIndex(String name) {
    int idx = 0;
    for (String column : columns) {
      if (column.equals(name)) {
        return idx;
      }
      idx++;
    }
    return -1;
  }

  public Collection<?> getValues(int idx) {
    Collection<Object> col = new HashSet<Object>(rows.size());

    if (idx == -1)
      return col;

    for (Object r : rows.uniqueSet()) {
      Row row = (Row) r;
      Object val = row.getValue(idx);
      col.add(val);
    }

    return col;
  }

  /**
   * Get all valid values from a specified column of the ResultSet.
   * A valid value is a non-null value.
   * 
   * @param idx the column of the ResultSet.
   * @return a Collection containing all valid values.
   */
  public Collection<?> getValidValues(int idx) {
    Collection<Object> col = new HashSet<Object>(rows.size());

    if (idx == -1)
      return col;

    for (Object r : rows.uniqueSet()) {
      Row row = (Row) r;
      Object val = row.getValue(idx);
      if (val != null) {
        col.add(val);
      }
    }

    return col;
  }
  
  public Collection<?> getValues(String column) {
    int idx = getColumnIndex(column);
    return getValues(idx);
  }
  
  @SuppressWarnings("unchecked")
  public ResultSet merge(ResultSet rs) {
    List<String> sharedCols = getSharedColumns(rs);
    if (sharedCols.isEmpty()) {
      return null;
    } else {
      String col = sharedCols.get(0);
      Collection vals1 = getValues(col);
      Collection vals2 = rs.getValues(col);
      
      vals1.addAll(vals2);
      ResultSet result = new ResultSet(1, true);
      result.setColumnName(0, col);
      
      for (Object o : vals1) {
        Row r = result.createRow();
        r.setLastValue(o);
        result.addRow(r);
      }
      
      return result;
    }
  }
  
  public void union(ResultSet rs, boolean distinct) {
    for (Row r : rs) {
      if (!distinct || !containsRow(r)) {
        addRow(r);
      }
    }
  }
  
  public void intersect(ResultSet rs) {
    List<Row> toDelete = new LinkedList<Row>();
    for (Row r : this) {
      if (!rs.containsRow(r)) {
        toDelete.add(r);
      }
    }
    
    rows.removeAll(toDelete);
  }
  
  public void except(ResultSet rs) {
    for (Row r : rs) {
      removeRow(r);
    }
  }
}
