
package net.ontopia.topicmaps.db2tm;

import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.*;
import net.ontopia.utils.DebugUtils;

/**
 * INTERNAL: Virtual column that uses a function to map from old value
 * to new value.
 */
public class FunctionVirtualColumn implements ValueIF {
  protected Relation relation;
  protected String colname;
  protected String fullMethodName;
  protected Method method;
  
  protected List params = new ArrayList();

  public FunctionVirtualColumn(Relation relation, String colname, String fullMethodName) {
    this.relation = relation;
    this.colname = colname;
    this.fullMethodName = fullMethodName;
  }

  public void compile() {
    if (fullMethodName.length() < 3)
      throw new DB2TMConfigException("Function column method is invalid: '" + fullMethodName + "'");
    // look up method object
    int lix = fullMethodName.lastIndexOf('.');
    String className;
    String methodName;
    if (lix >= 0) {
      className = fullMethodName.substring(0, lix);
      methodName = fullMethodName.substring(lix+1);
    } else {
      throw new DB2TMConfigException("Function column method is invalid: '" + fullMethodName + "'");
    }
    if (className == null || className.trim().equals(""))
      throw new DB2TMConfigException("Function column class name is invalid: '" + className + "'");
    if (methodName == null || methodName.trim().equals(""))
      throw new DB2TMConfigException("Function column method name is invalid: '" + methodName + "'");

    // look up Class.method(String, ..., String)
    try {
      Class klass = Class.forName(className);
      Class[] paramTypes = new Class[params.size()];
      for (int i=0; i < paramTypes.length; i++) {
        paramTypes[i] = String.class;
      }
      Method m = klass.getMethod(methodName, paramTypes);
      // method must be static and return a String
      int modifiers = m.getModifiers();
      if ((modifiers & Modifier.STATIC) == Modifier.STATIC &&
          String.class.equals(m.getReturnType()))
        this.method = m;
    } catch (Exception e) {
      // ignore
    }
    if (this.method == null) {
      StringBuffer sb = new StringBuffer();
      sb.append("Could not find static method ");
      sb.append(fullMethodName);
      sb.append('(');
      for (int i=0; i < params.size(); i++) {
        if (i > 0) sb.append(", ");
        sb.append("String");
      }
      sb.append("). Note that the method must be static and have a return type of java.lang.String.");
      throw new DB2TMConfigException(sb.toString());
    }
  }
  
  public String getValue(String[] tuple) {
    // get method argument values
    Object[] args = new String[params.size()];
    for (int i=0; i < args.length; i++) {
      args[i] = ((ValueIF)params.get(i)).getValue(tuple);
    }
    // call method
    try {
      return (String)method.invoke(null, args);
    } catch (Exception e) {
      String[] dispargs = new String[args.length];
      for (int i=0; i < args.length; i++) {
        String val = "" + args[i];
        if (val.length() > 100)
          val = val.substring(0, 100) + "...";
        dispargs[i] = val;
      }
      throw new DB2TMInputException("Error occurred when invoking function column '" + colname + "' on " + DebugUtils.toString(dispargs), e);
    }
  }

  public void addParameter(String paramValue) {
    ValueIF value = Values.getPatternValue(relation, paramValue);
    params.add(value);
  }
  
}
