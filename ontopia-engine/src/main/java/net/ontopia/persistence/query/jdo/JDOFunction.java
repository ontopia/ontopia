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

/**
 * INTERNAL: JDOQL method: Object.&lt;operator&gt;(Object,...). The
 * function can also be a free form function where the name is the
 * function pattern, e.g. "contains($1, $2, 1) > 0)". Function
 * arguments are referred via their position.
 */

public class JDOFunction implements JDOValueIF {

  protected String name;
  protected Class value_type;
  protected JDOValueIF[] args;

  public JDOFunction(String name, Class value_type, JDOValueIF arg1) {
    this(name, value_type, new JDOValueIF[] { arg1 });
  }

  public JDOFunction(String name, Class value_type, JDOValueIF arg1, JDOValueIF arg2) {
    this(name, value_type, new JDOValueIF[] { arg1, arg2 });
  }

  public JDOFunction(String name, Class value_type, 
		     JDOValueIF arg1, JDOValueIF arg2, JDOValueIF arg3) {
    this(name, value_type, new JDOValueIF[] { arg1, arg2, arg3 });
  }

  public JDOFunction(String name, Class value_type, JDOValueIF[] args) {
    this.name = name;
    this.value_type = value_type;
    this.args = args;
  }
  
  @Override
  public int getType() {
    return FUNCTION;
  }

  public String getName() {
    return name;
  }

  public Class getValueType() {
    return value_type;
  }

  public JDOValueIF[] getArguments() {
    return args;
  }
  
  @Override
  public int hashCode() {
    int retval = name.hashCode() + value_type.hashCode();
    for (int i=0; i < args.length; i++) {
      retval += args[i].hashCode();
    }
    return retval;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof JDOFunction) {
      JDOFunction other = (JDOFunction)obj;
      return (name.equals(other.name) &&
	      value_type.equals(other.value_type) &&
	      Arrays.equals(args, other.args));
    }
    return false;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(name).append('(');
    for (int i=0; i < args.length; i++) {
      if (i > 0) {
        sb.append(", ");
      }
      sb.append(args[i]);
    }
    sb.append(')');
    return sb.toString();
  }

  @Override
  public void visit(JDOVisitorIF visitor) {
    for (int i=0; i < args.length; i++) {
      visitor.visitable(args[i]);
    }
  }
  
}






