package net.ontopia.topicmaps.query.toma.impl.basic;

import java.util.Vector;

import net.ontopia.topicmaps.core.TMObjectIF;

/**
 * INTERNAL:  
 */
public class Row implements Cloneable {

  private Vector<Object> row;

  public Row(int cols) {
    row = new Vector<Object>(2 * cols);
    row.setSize(cols);
  }

  public int getColumnCount() {
    return row.size();
  }

  public Object getValue(int column) {
    return row.get(column);
  }

  public Object getFirstValue() {
    return row.get(0);
  }

  public Object getLastValue() {
    return row.get(row.size() - 1);
  }

  public void setValue(int column, Object val) {
    row.set(column, val);
  }

  public Object[] getValues() {
    return row.toArray();
  }

  public Object[] getValues(Object[] values) {
    return row.toArray(values);
  }

  public void addColumn() {
    row.setSize(row.size() + 1);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;

    if ((obj == null) || (obj.getClass() != this.getClass()))
      return false;

    // object must be Row at this point
    Row other = (Row) obj;

    if (row.size() != other.row.size())
      return false;

    for (int idx = 0; idx < row.size(); idx++) {
      Object a = row.get(idx);
      Object b = other.row.get(idx);

      if (a == null && b == null)
        continue;
      if (a == null)
        return false;

      if (a instanceof TMObjectIF && b instanceof TMObjectIF) {
        String idA = ((TMObjectIF) a).getObjectId();
        String idB = ((TMObjectIF) b).getObjectId();

        if (idA == null && idB == null)
          continue;
        if (idA == null)
          return false;
        if (!idA.equals(idB))
          return false;
      } else {
        if (!a.equals(b))
          return false;
      }
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;

    for (Object col : row) {
      int val = 0;

      if (col != null) {
        if (col instanceof TMObjectIF) {
          val = ((TMObjectIF) col).getObjectId().hashCode();
        } else {
          val = col.hashCode();
        }
      }

      hash = 31 * hash + val;
    }
    return hash;
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    Row r = (Row) super.clone();

    r.row = new Vector<Object>(this.row);
    return r;
  }
}
