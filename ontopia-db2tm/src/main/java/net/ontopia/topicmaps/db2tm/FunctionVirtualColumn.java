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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * INTERNAL: Virtual column that uses a function to map from old value
 * to new value.
 */
public class FunctionVirtualColumn implements ValueIF {
  protected final Relation relation;
  protected final String colname;
  protected final String fullMethodName;
  protected Method method;
  
  protected List<ValueIF> params = new ArrayList<ValueIF>();

  public FunctionVirtualColumn(Relation relation, String colname, String fullMethodName) {
    this.relation = relation;
    this.colname = colname;
    this.fullMethodName = fullMethodName;
  }

  public String getColumnName() {
    return colname;
  }

  public void compile() {
    if (fullMethodName.length() < 3) {
      throw new DB2TMConfigException("Function column method is invalid: '" + fullMethodName + "'");
    }
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
    if (className == null || className.trim().equals("")) {
      throw new DB2TMConfigException("Function column class name is invalid: '" + className + "'");
    }
    if (methodName == null || methodName.trim().equals("")) {
      throw new DB2TMConfigException("Function column method name is invalid: '" + methodName + "'");
    }

    // look up Class.method(String, ..., String)
    try {
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      Class<?> klass = Class.forName(className, true, classLoader);
      Class<?>[] paramTypes = new Class<?>[params.size()];
      for (int i=0; i < paramTypes.length; i++) {
        paramTypes[i] = String.class;
      }
      Method m = klass.getMethod(methodName, paramTypes);
      // method must be static and return a String
      int modifiers = m.getModifiers();
      if ((modifiers & Modifier.STATIC) == Modifier.STATIC &&
          String.class.equals(m.getReturnType())) {
        this.method = m;
      }
    } catch (Exception e) {
      // ignore
    }
    if (this.method == null) {
      StringBuilder sb = new StringBuilder();
      sb.append("Could not find static method ");
      sb.append(fullMethodName);
      sb.append('(');
      for (int i=0; i < params.size(); i++) {
        if (i > 0) {
          sb.append(", ");
        }
        sb.append("String");
      }
      sb.append("). Note that the method must be static and have a return type of java.lang.String.");
      throw new DB2TMConfigException(sb.toString());
    }
  }
  
  @Override
  public String getValue(String[] tuple) {
    // get method argument values
    Object[] args = new String[params.size()];
    for (int i=0; i < args.length; i++) {
      args[i] = params.get(i).getValue(tuple);
    }
    // call method
    try {
      return (String)method.invoke(null, args);
    } catch (Exception e) {
      String[] dispargs = new String[args.length];
      for (int i=0; i < args.length; i++) {
        String val = "" + args[i];
        if (val.length() > 100) {
          val = val.substring(0, 100) + "...";
        }
        dispargs[i] = val;
      }
      throw new DB2TMInputException("Error occurred when invoking function column '" + colname + "' on " + Arrays.toString(dispargs), e);
    }
  }

  public void addParameter(String paramValue) {
    ValueIF value = Values.getPatternValue(relation, paramValue);
    params.add(value);
  }
  
}
