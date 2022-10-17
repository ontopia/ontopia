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

package net.ontopia.persistence.query.jdo;

import java.util.Arrays;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

/**
 * INTERNAL: JDOQL value: native value expression. Represents the
 * traversal from a variable to the value returned byte the native
 * expression. Syntax: 'A.{args}'. This might be useful when you know
 * that the root variable refers to a database table and that there
 * are a specific column that you want to get at.
 */

public class JDONativeValue implements JDOValueIF {

  // NOTE: Class currently only supports columns references with
  // JDOVariableIF as root. May want to make it more general than
  // that.

  protected JDOVariable root;
  protected String[] args;
  protected Class value_type;

  public JDONativeValue(JDOVariable root, String arg, Class value_type) {
    this(root, new String[] { arg }, value_type);
  }

  public JDONativeValue(JDOVariable root, String[] args, Class value_type) {
    Objects.requireNonNull(root, "Value root cannot be null.");
    Objects.requireNonNull(args, "Field args cannot be null.");
    if (args.length < 1) { throw new IllegalArgumentException("Field args cannot be null."); }
    Objects.requireNonNull(value_type, "Value value_type cannot be null.");
    this.root = root;
    this.args = args;
    this.value_type = value_type;
  }

  @Override
  public int getType() {
    return NATIVE_VALUE;
  }
  
  public JDOVariable getRoot() {
    return root;
  }
  
  public String[] getArguments() {
    return args;
  }

  public Class getValueType() {
    return value_type;
  }

  @Override
  public int hashCode() {
    int hashCode = root.hashCode();
    for (int ix = 0; ix < args.length; ix++) {
      if (args[ix] != null)
        hashCode = (hashCode + args[ix].hashCode()) & 0x7FFFFFFF;
    }
    return hashCode;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj instanceof JDONativeValue) {
      JDONativeValue other = (JDONativeValue)obj;
      if (root.equals(other.root))
        if (Arrays.equals(args, other.args))
          return true;
    }
    return false;
  }
  
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder(root.toString());
    sb.append(".{");
    sb.append(StringUtils.join(args, ", "));
    sb.append('}');
    return sb.toString();
  }

  @Override
  public void visit(JDOVisitorIF visitor) {
    visitor.visitable(root);
  }
  
}






