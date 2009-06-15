
// $Id: JDOFunction.java,v 1.3 2005/07/12 09:37:40 grove Exp $

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
  
  public int hashCode() {
    int retval = name.hashCode() + value_type.hashCode();
    for (int i=0; i < args.length; i++) {
      retval += args[i].hashCode();
    }
    return retval;
  }
  
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj instanceof JDOFunction) {
      JDOFunction other = (JDOFunction)obj;
      return (name.equals(other.name) &&
	      value_type.equals(other.value_type) &&
	      Arrays.equals(args, other.args));
    }
    return false;
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append(name).append('(');
    for (int i=0; i < args.length; i++) {
      if (i > 0) sb.append(", ");
      sb.append(args[i]);
    }
    sb.append(')');
    return sb.toString();
  }

  public void visit(JDOVisitorIF visitor) {
    for (int i=0; i < args.length; i++) {
      visitor.visitable(args[i]);
    }
  }
  
}






