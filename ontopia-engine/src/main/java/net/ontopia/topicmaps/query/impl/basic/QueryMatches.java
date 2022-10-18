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

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.parser.Parameter;
import net.ontopia.topicmaps.query.parser.Variable;
import net.ontopia.utils.CompactHashSet;
import net.ontopia.utils.OntopiaRuntimeException;
import org.apache.commons.lang3.StringUtils;

/**
 * INTERNAL: Object used to hold query results during computation.
 */
public class QueryMatches {
  public static int initialSize = 100;
  public int last;
  public int size;
  public Object[][] data;

  // either a Variable or a TMObjectIF/String/Integer/Float constant, defining the
  // contents of the column  
  public Object[] columnDefinitions;
  public int colcount;

  private QueryContext context;

  /**
   * INTERNAL: Creates a new matches object with the given column
   * definitions.
   */
  public QueryMatches(Collection columnDefs, QueryContext context) {
    colcount = columnDefs.size();
    data = new Object[initialSize][colcount];
    size = initialSize;
    last = -1;
    columnDefinitions = columnDefs.toArray();
    this.context = context;
  }

  /**
   * INTERNAL: Creates a new (empty) matches object with the same
   * column definitions as the QueryMatches object passed in the
   * parameter.
   */
  public QueryMatches(QueryMatches matches) {
    colcount = matches.colcount;
    data = new Object[initialSize][colcount];
    size = initialSize;
    last = -1;
    columnDefinitions = matches.columnDefinitions;
    context = matches.getQueryContext();
  }

  /**
   * INTERNAL: Returns the index of the given variable in the table.
   */
  public int getVariableIndex(String varname) {
    for (int ix = 0; ix < colcount; ix++) {
      if (columnDefinitions[ix] instanceof Variable &&
          varname.equals(((Variable)columnDefinitions[ix]).getName()))
            return ix;
    }
    return -1;
  }
  
  /**
   * INTERNAL: Returns the index of the given constant in the table.
   */
  public int getIndex(TMObjectIF constant) {
    for (int ix = 0; ix < colcount; ix++)
      if (constant.equals(columnDefinitions[ix]))
        return ix;
    return -1;
  }

  /**
   * INTERNAL: Returns the index of the given variable in the table.
   */
  public int getIndex(Variable var) {
    for (int ix = 0; ix < colcount; ix++)
      if (var.equals(columnDefinitions[ix]))
        return ix;
    return -1;
  }

  /**
   * INTERNAL: Returns the index of the given string constant in the table.
   */
  public int getIndex(String str) {
    for (int ix = 0; ix < colcount; ix++)
      if (str.equals(columnDefinitions[ix]))
        return ix;
    return -1;
  }

  /**
   * INTERNAL: Returns the index of the given integer constant in the table.
   */
  public int getIndex(Integer num) {
    for (int ix = 0; ix < colcount; ix++)
      if (num.equals(columnDefinitions[ix]))
        return ix;
    return -1;
  }

  /**
   * INTERNAL: Returns the index of the given float constant in the table.
   */
  public int getIndex(Float num) {
    for (int ix = 0; ix < colcount; ix++)
      if (num.equals(columnDefinitions[ix]))
        return ix;
    return -1;
  }

  /**
   * INTERNAL: Returns the column index of the given object in the
   * table.
   */
  public int getIndex(Object argument) {
    if (argument instanceof Variable)
      return getIndex((Variable) argument);
    else if (argument instanceof TMObjectIF)
      return getIndex((TMObjectIF) argument);
    else if (argument instanceof Parameter)
      return getIndex(context.getParameterValue(((Parameter) argument).getName()));
    else if (argument instanceof String)
      return getIndex((String) argument);
    else if (argument instanceof Integer)
      return getIndex((Integer) argument);
    else if (argument instanceof Float)
      return getIndex((Float) argument);
    else
      throw new OntopiaRuntimeException("Argument of unknown type: " + argument);
  }

  /**
   * INTERNAL: Returns definition of column.
   */
  public Object getColumnDefinition(int ix) {
    return columnDefinitions[ix];
  }
  
  /**
   * INTERNAL: Used to increase the size of the table when full.
   */
  public void increaseCapacity() {
    ensureCapacity(size * 2);
  }

  /**
   * INTERNAL: Ensures that the table has at least the given size.
   */
  public void ensureCapacity(int requirement) {
    while (size < requirement)
      size *= 2;
    
    Object[][] newdata = new Object[size][colcount];
    System.arraycopy(data, 0, newdata, 0, last+1);
    data = newdata;
  }

  /**
   * INTERNAL: Empties the table.
   */
  public void clear() {
    last = -1;
  }

  /**
   * INTERNAL: Returns the query context.
   */
  public QueryContext getQueryContext() {
    return context;
  }

  /**
   * INTERNAL: Checks whether any of the columns are literal columns
   * representing a literal in the query.
   */
  public boolean hasLiteralColumns() {
    for (int ix = 0; ix < colcount; ix++)
      if (!(columnDefinitions[ix] instanceof Variable))
        return true;
    return false;
  }

  /**
   * INTERNAL: Returns true if there are no matches.
   */
  public boolean isEmpty() {
    return last == -1;
  }

  /**
   * INTERNAL: Inserts the constant values in the constant columns.
   * Uses the information in the column definitions to do this.
   */
  public void insertConstants() {
    Object[] template = new Object[colcount];
    for (int col = 0; col < colcount; col++) {
      if (columnDefinitions[col] instanceof TMObjectIF ||
          columnDefinitions[col] instanceof String ||
          columnDefinitions[col] instanceof Integer ||
          columnDefinitions[col] instanceof Float)
        template[col] = columnDefinitions[col];
    }
    for (int ix = 0; ix <= last; ix++) {
      for (int i = 0; i < template.length; i++) {
        if (template[i] != null)
          data[ix][i] = template[i];
      }
    }
  }
  
  // ===== QUERY INTROSPECTION ===============================================

  /**
   * INTERNAL: Checks whether the variable represented by the indexed
   * column is bound.
   */
  public boolean bound(int colix) {
    return data[0][colix] != null;
  }
  
  // ===== QUERY MATCH ALGEBRA ===============================================
  
  /**
   * INTERNAL: Adds all the matches in the given table to this table.
   * Note that the two tables must have the same layout.
   */
  public void add(QueryMatches extra) {
    if (extra.last == -1)
      return;
    
    ensureCapacity(last + extra.last + 2);
    System.arraycopy(extra.data, 0, data, last+1, extra.last+1);
    last += extra.last + 1;
  }

  /**
   * EXPERIMENTAL: Adds input array to this table.
   */
  public void add(Object[][] newdata, int length) {
    if (length < 1) return;
    // Add to query matches
    ensureCapacity(last + length + 2);
    System.arraycopy(newdata, 0, data, last+1, length);
    last += length;
  }

  /**
   * EXPERIMENTAL: Adds QueryResultIF matches to this table.
   */
  public void add(QueryResultIF extra) {

    int spec_width = columnDefinitions.length;
    int[] spec = new int[spec_width];
    Arrays.fill(spec, -1);
    
    int extra_width = extra.getWidth();
    for (int i=0; i < extra_width; i++) {
      // skip column if not in query matches
      int index = getVariableIndex(extra.getColumnName(i));
      if (index > -1)
        spec[index] = i;
    }

    int batch_size = 50; // number of rows
    int rowidx = 0;
    
    // Add to query matches
    ensureCapacity(last + batch_size + 2);

    Object[] frow = (last == 0 ? data[0] : data[last-1]); // feeding row
    Object[] crow = (last == 0 ? data[0] : data[last]); // current row

    while (extra.next()) {
      
      // Transform columns and add values.
      for (int i=0; i < spec_width; i++) {
        int idx = spec[i];
        // If index specified read value from extra, otherwise use feeding row.
        if (idx == -1)
          crow[i] = frow[i];
        else
          crow[i] = extra.getValue(idx);
      }
      if (rowidx == batch_size - 1) {
        // Prepare for new batch
        ensureCapacity(last + batch_size + 2);
        rowidx = 0;
      } else {
        rowidx++;
      }
      // Read next row
      last++;
      crow = data[last];
    }
    last--;
  }
  
  /**     
   * INTERNAL: Removes the rows which have matching counterparts in
   * the argument.
   */
  protected void remove(QueryMatches matches) {
    int cols = columnDefinitions.length;
    int next = 0;

    if (last * matches.last < 1000000) {
      // naive approach
      for (int resrow = 0; resrow <= last; resrow++) {
        Object[] row = data[resrow];
      
        // if we can find a matching row in matches, skip this one
        // if not, keep it
        boolean ok = true;
        for (int mrow = 0; ok && mrow <= matches.last; mrow++) {

          boolean eq = true;
          for (int col = 0; eq && col < cols; col++)
            if (row[col] != null)
              eq = row[col].equals(matches.data[mrow][col]);

          ok = !eq; // we're ok if it didn't match
        }

        if (ok) // the row is ok, so keep it
          data[next++] = row;
      }
      last = next-1;
      
    } else {
      // too many results for a naive scan. instead, build a Set from
      // the other result set and use it to see which rows in this one
      // also exist in the other.      
      
      // first step: we only compare bound columns. find out which
      // columns are bound. we will always have fewer columns bound
      // than the not-ed result set, so use our own count.
      int count = 0;
      for (int ix = 0; ix < colcount; ix++)
        if (bound(ix))
          count++;

      int compare[] = new int[count];
      count = 0;
      for (int ix = 0; ix < colcount; ix++)
        if (bound(ix))
          compare[count++] = ix;      

      // second step: build the set
      Set set = new CompactHashSet(last + 1);
      ArrayWrapper wrapper = new SelectiveArrayWrapper(compare);
      for (int row = 0; row <= matches.last; row++) {
        wrapper.setArray(matches.data[row]); // reuse previous wrapper

        if (!set.contains(wrapper)) {
          set.add(wrapper);
          // can't reuse, so make new wrapper
          wrapper = new SelectiveArrayWrapper(compare); 
        }
      }

      // third step: filter the rows
      wrapper = new SelectiveArrayWrapper(compare);
      for (int row = 0; row <= last; row++) {
        wrapper.setArray(data[row]);
        if (!set.contains(wrapper))
          data[next++] = data[row]; // keep it
      }
      last = next - 1;
    }
  }

  /**
   * INTERNAL: Adds all rows which do not already exist. Matching
   * ignores nulls in the rows being added.
   */
  protected void addNonRedundant(QueryMatches matches) {
    int cols = columnDefinitions.length;
    int orgLast = last;

    // for each input row
    for (int mrow = 0; mrow <= matches.last; mrow++) {

      // look for an existing match
      boolean found = false;      
      for (int resrow = 0; !found && resrow <= orgLast; resrow++) {
        Object[] row = data[resrow];
      
        boolean eq = true;
        for (int col = 0; eq && col < cols; col++)
          if (matches.data[mrow][col] != null)
            eq = matches.data[mrow][col].equals(row[col]);

        found = eq;
      }
        
      if (!found) { // the row is ok, so add it
        if (last+1 == size) 
            increaseCapacity();
        last++;
        data[last] = matches.data[mrow];
      }
    }
  }

  // ===== QUERY MATCH TRANSLATION ===========================================

  // FIXME: in method below int* and ext* are wrong way around
  /**
   * INTERNAL: Computes the translation specification array, which
   * gives the connection between this and the other match table.
   *
   * @param intarguments Actual received parameters in rule invocation.
   * @param extarguments Declared parameters in rule declaration.
   * @return an array of type int[][] that looks like [intspec,
   * extspec], where intspec is the specification for this match and
   * extspec is the specification for the other match.
   */
  public int[][] getTranslationSpec(Object[] intarguments,
                                    QueryMatches extmatches,
                                    Object[] extarguments) 
    throws InvalidQueryException {

    int width = intarguments.length;
    int[] intcols = new int[width];
    int[] extcols = new int[width];

    for (int ix = 0; ix < width; ix++) {
      intcols[ix] = getIndex(intarguments[ix]);
      extcols[ix] = extmatches.getIndex(extarguments[ix]);

      if (extcols[ix] == -1)
        throw new InvalidQueryException("Unused argument " +
                                        extarguments[ix]);
    }

    int[][] spec = new int[2][];
    spec[0] = intcols;
    spec[1] = extcols;
    return spec;
  }
  
  /**
   * INTERNAL: Translates matches in this table into corresponding
   * matches in the other.
   */
  public void translate(int[] fromCols,
                        QueryMatches toQM, int[] toCols) {
    Object[][] from = data;
    Object[][] to   = toQM.data;
    int fromlast    = last;
    int cols        = fromCols.length;

    for (int fromrow = 0; fromrow <= fromlast; fromrow++) {
      if (toQM.last+1 == toQM.size) {
        toQM.increaseCapacity();
        to = toQM.data;
      }
      toQM.last++;

      for (int col = 0; col < cols; col++) 
        to[toQM.last][toCols[col]] = from[fromrow][fromCols[col]];
    }
  }

  /**
   * INTERNAL: Merges this match table (from inside a rule) with
   * another match table (from the calling context), producing a new
   * set of matches (corresponding to the result of the rule, as
   * viewed from the outside).
   * @param intspec Mapping from general column no (?) to column no in
   *                this QM object.
   * @param extspec Mapping from general column no (?) to column no in
   *                extmatches.
   * @param equalpairs See RulePredicate.getEqualPairs() for explanation.
   *                   Numbers are argument numbers.
   */
  public QueryMatches merge(int[] intspec, QueryMatches extmatches, 
                            int[] extspec, int[] equalpairs) {

    int intspec_length = intspec.length;
    int extspec_length = extspec.length;
    
    // find out what columns to compare by creating intcols+extcols, which
    // is really a list of pairs of columns to compare.
    int compcount = 0;
    for (int ix = 0; ix < intspec_length; ix++)
      if (data[0][intspec[ix]] != null &&
          extmatches.data[0][extspec[ix]] != null)
        compcount++;

    int[] intcols = new int[compcount];
    int[] extcols = new int[compcount];
    compcount = 0;
    for (int ix = 0; ix < intspec_length; ix++) {
      if (data[0][intspec[ix]] != null &&
          extmatches.data[0][extspec[ix]] != null) {
        intcols[compcount] = intspec[ix];
        extcols[compcount] = extspec[ix];
        compcount++;
      }
    }
    
    // do the merging
    QueryMatches result = new QueryMatches(this);
    int width = columnDefinitions.length;
    for (int introw = 0; introw <= last; introw++) {
      if (introw > 0) {
        int col = 0;
        for (; col < width &&
               ((data[introw][col] == null && data[introw-1][col] == null) ||
                (data[introw][col] != null && data[introw][col].equals(data[introw-1][col]))); col++)
          ;

        if (col == width) 
          continue;
      }
      
      externalrow:
      for (int extrow = 0; extrow <= extmatches.last; extrow++) {
        // check that internal and external values match
        for (int col = 0; col < compcount; col++) {
          if (data[introw][intcols[col]] == null ||
              !data[introw][intcols[col]].equals(extmatches.data[extrow][extcols[col]]))
            continue externalrow;
        }

        // check equal pairs
        for (int ix = 0; ix+1 < equalpairs.length; ix += 2)
          if (extmatches.data[extrow][extspec[equalpairs[ix]]] != null &&
              !extmatches.data[extrow][extspec[equalpairs[ix]]].
                equals(extmatches.data[extrow][extspec[equalpairs[ix+1]]]))
            continue externalrow;

        // generate output match
        if (result.last+1 == result.size) 
          result.increaseCapacity();
        result.last++;

        // copy internal match row
        System.arraycopy(data[introw], 0,
                         result.data[result.last], 0,
                         width);

        // fill in results from internal match
        for (int col = 0; col < extspec_length; col++) 
          result.data[result.last][intspec[col]] = extmatches.data[extrow][extspec[col]];
      }
    }
    
    return result;
  }

  // ===== REMOVING DUPLICATES ===========================================

  public QueryMatches removeDuplicates() {
    QueryMatches result = new QueryMatches(this);
    Set alreadyAdded = new CompactHashSet();
    Object[][] mdata = data;
    Object[][] rdata = result.data;
    ArrayWrapper wrapper = new ArrayWrapper(); // for instance reuse...

    for (int row = 0; row <= last; row++) {
      wrapper.setArray(mdata[row]); // reuse previous wrapper
      if (!alreadyAdded.contains(wrapper)) {
        alreadyAdded.add(wrapper);
        wrapper = new ArrayWrapper(); // can't reuse, so make new wrapper
        if (result.last+1 == result.size) {
          result.increaseCapacity();
          rdata = result.data;
        }
        result.last++;
        rdata[result.last] = mdata[row];
      }
    }
    return result;
  }
  
  /// FIXME: Copied from QueryProcessor

  // We have to use this to get meaningful implementations of
  // hashCode() and equals() for arrays. Arrays have these methods,
  // but they are, stupidly, the same as for Object.
  
  class ArrayWrapper {
    protected Object[] row;
    protected int hashCode;

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
      //  - o is not null
      //  - o is an ArrayWrapper
      //  - o contains an Object[] array of the same length as row
      Object[] orow = ((ArrayWrapper) o).row;
      for (int ix = 0; ix < orow.length; ix++)
        if (orow[ix] != null && !orow[ix].equals(row[ix]))
          return false;
      return true;
    }
  }

  // doesn't compare all columns; only indicated ones
  class SelectiveArrayWrapper extends ArrayWrapper {
    private int comparedColumns[];

    public SelectiveArrayWrapper(int comparedColumns[]) {
      this.comparedColumns = comparedColumns;
    }
    
    @Override
    public void setArray(Object[] row) {
      this.row = row;

      hashCode = 0;
      for (int i = 0; i < comparedColumns.length; i++) {
        int ix = comparedColumns[i];
        if (row[ix] != null)
          hashCode = (hashCode + row[ix].hashCode()) & 0x7FFFFFFF;
      }
    }

    @Override
    public boolean equals(Object o) {
      // this class is only used here, so we are making some simplifying
      // assumptions:
      //  - o is not null
      //  - o is an ArrayWrapper
      //  - o contains an Object[] array of the same length as row
      Object[] orow = ((ArrayWrapper) o).row;
      for (int i = 0; i < comparedColumns.length; i++) {
        int ix = comparedColumns[i];
        if (orow[ix] != null && !orow[ix].equals(row[ix]))
          return false;
      }
      return true;
    }
  }
  
  // ===== DEBUG METHODS =====================================================
  
  public String dump() {
    StringBuilder sb = new StringBuilder(500)
        .append("------------------------------------------------------------------------------\n")
        .append(StringUtils.join(columnDefinitions, " | "))
        .append("\n------------------------------------------------------------------------------\n");
    for (int r=0; r <= last; r++) {
      sb.append('[');
      for (int c = 0; c < columnDefinitions.length-1; c++) {
        sb.append(toString(data[r][c])).append(", ");
      }
      sb.append(toString(data[r][columnDefinitions.length-1])).append("]\n");
    }
    sb.append((last+1)).append(" rows");
    return sb.toString();
  }

  private String toString(Object obj) {
    if (obj instanceof TopicIF) {
      Iterator it = ((TopicIF) obj).getItemIdentifiers().iterator();
      while (it.hasNext()) {
        LocatorIF loc = (LocatorIF) it.next();
        String addr = loc.getAddress();
        int ix = addr.indexOf('#');
        //! int ix = addr.lastIndexOf('/'); // include file name
        if (ix != -1)
          return addr.substring(ix + 1);
      }
      return ((TopicIF) obj).getObjectId();
    } else if (obj == null)
      return "null";
    else
      return obj.toString();
  }

}
