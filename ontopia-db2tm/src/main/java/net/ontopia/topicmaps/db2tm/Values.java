/*
 * #!
 * Ontopia DB2TM
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

package net.ontopia.topicmaps.db2tm;

import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * INTERNAL: Virtual column that used a hash table to map from old
 * value to new value. A default value can also be specified when no
 * entry exists.
 */
public final class Values {
    
  private static final Pattern PATTERN = Pattern.compile("\\$((\\d+)|\\{(\\w+)\\})");

  // -----------------------------------------------------------------------------
  // Utility methods
  // -----------------------------------------------------------------------------

  protected static ValueIF getColumnValue(Relation relation, String colname) {
    if (relation.isVirtualColumn(colname)) {
      // use virtual column
      return relation.getVirtualColumn(colname);
    } else {
      // use standard column
      int cix = relation.getColumnIndex(colname);
      if (cix < 0) {
        throw new DB2TMException("Cannot find column '" + colname + "' in relation '" + relation.getName() + "'");
      }
      return getColumnValue(relation, cix);
    }
  }

  protected static ValueIF getColumnValue(Relation relation, int cix) {
    if (cix < 0 || cix >= relation.getColumns().length) {
      throw new DB2TMException("Cannot find column $" + cix + " in relation '" + relation.getName() + "'");
    }
    return new TupleValue(cix);
  }
  
  
  protected static ValueIF getPatternValue(Relation relation, String value) {
    // use pattern value
    List<Object> list = new ArrayList<Object>();
    Matcher matcher = PATTERN.matcher(value);
    int colvals = 0;
    int ix = 0;
    while (matcher != null && matcher.find()) {
      ValueIF colval = null;
      // First try: Found number?
      String name = matcher.group(2);
      if (name != null) {
        int cix = Integer.parseInt(name)-1;
        colval = getColumnValue(relation, cix);
      }
      else {
        // Must have been matched $\w
        name = matcher.group(3);
        colval = getColumnValue(relation, name);
      }
      if (name != null) {
        int beginOffset = matcher.start();
        if (beginOffset > ix) {
          list.add(value.substring(ix, beginOffset));
        }
        list.add(colval);
        colvals++;
      } else {
        int endOffset = matcher.end();
        if (endOffset > ix) {
          list.add(value.substring(ix, endOffset));
        }
      }
      ix = matcher.end();
    }
    if (ix > 0 && value.length() > ix) {
      list.add(value.substring(ix));
    } else if (list.isEmpty()) {
      return new StaticValue(value);
    }
    return new PatternValue(list.toArray(), colvals);
  }

  // -----------------------------------------------------------------------------
  // ValueIF implementations
  // -----------------------------------------------------------------------------
  
  private static class TupleValue implements ValueIF {
    private final int ix;
    private TupleValue(int ix) {
      this.ix = ix;
    }
    @Override
    public String getValue(String[] tuple) {
      return (ix >= tuple.length) ? null : tuple[ix];
    }    
  }
  
  private static class StaticValue implements ValueIF {
    private final String value;
    private StaticValue(String value) {
      this.value = value;
    }
    @Override
    public String getValue(String[] tuple) {
      return value;
    }
  }
  
  private static class PatternValue implements ValueIF {
  
    private final Object[] list;
    private final int colvals;
    
    private PatternValue(Object[] list, int colvals) {
      this.list = list;
      this.colvals = colvals;
    }
    
    @Override
    public String getValue(String[] tuple) {
      int empties = 0;
      StringBuilder sb = new StringBuilder();
      for (int i=0; i < list.length; i++) {
        Object o = list[i];
        if (o instanceof String) {
          sb.append(o);
        } else {
          ValueIF colval = (ValueIF)o;
          String value = colval.getValue(tuple);
          if (Utils.isValueEmpty(value)) {
            empties++;
            if (empties == colvals) {
              return null;
            }
          } else {
            sb.append(value);
          }
        }
      }
      return sb.toString();
    }
  }
  
}
