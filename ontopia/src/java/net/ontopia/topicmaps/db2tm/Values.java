
// $Id: Values.java,v 1.5 2006/11/30 10:39:11 grove Exp $

package net.ontopia.topicmaps.db2tm;

import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.lang.reflect.*;

import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.StringUtils;

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

  static ValueIF getColumnValue(Relation relation, String colname) {
    if (relation.isVirtualColumn(colname)) {
      // use virtual column
      return relation.getVirtualColumn(colname);
    } else {
      // use standard column
      int cix = relation.getColumnIndex(colname);
      if (cix < 0)
        throw new DB2TMException("Cannot find column '" + colname + "' in relation '" + relation.getName() + "'");
      return getColumnValue(relation, cix);
    }
  }

  static ValueIF getColumnValue(Relation relation, int cix) {
    if (cix < 0 || cix >= relation.getColumns().length)
      throw new DB2TMException("Cannot find column $" + cix + " in relation '" + relation.getName() + "'");
    return new TupleValue(cix);
  }
  
  
  static ValueIF getPatternValue(Relation relation, String value) {
    // use pattern value
    List list = new ArrayList();
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
        if (beginOffset > ix)
          list.add(value.substring(ix, beginOffset));
        list.add(colval);
        colvals++;
      } else {
        int endOffset = matcher.end();
        if (endOffset > ix)
          list.add(value.substring(ix, endOffset));
      }
      ix = matcher.end();
    }
    if (ix > 0 && value.length() > ix)
      list.add(value.substring(ix));
    else if (list.isEmpty())
      return new StaticValue(value);
    return new PatternValue(list.toArray(), colvals);
  }

  // -----------------------------------------------------------------------------
  // ValueIF implementations
  // -----------------------------------------------------------------------------
  
  private static class TupleValue implements ValueIF {
    private int ix;
    private TupleValue(int ix) {
      this.ix = ix;
    }
    public String getValue(String[] tuple) {
      if (ix >= tuple.length)
        return null;
      else
        return tuple[ix];
    }    
  }
  
  private static class StaticValue implements ValueIF {
    private String value;
    private StaticValue(String value) {
      this.value = value;
    }
    public String getValue(String[] tuple) {
      return value;
    }    
  }
  
  private static class PatternValue implements ValueIF {
  
    private Object[] list;
    private int colvals;
    
    private PatternValue(Object[] list, int colvals) {
      this.list = list;
      this.colvals = colvals;
    }
    
    public String getValue(String[] tuple) {
      int empties = 0;
      StringBuffer sb = new StringBuffer();
      for (int i=0; i < list.length; i++) {
        Object o = list[i];
        if (o instanceof String)
          sb.append(o);
        else {
          ValueIF colval = (ValueIF)o;
          String value = colval.getValue(tuple);
          if (Utils.isValueEmpty(value)) {
            empties++;
            if (empties == colvals)
              return null;
            //! else
            //!   sb.append("");
          } else
            sb.append(value);
        }
      }
      return sb.toString();
    }
  }
  
}
