
package net.ontopia.persistence.query.jdo;

import java.util.Arrays;

import net.ontopia.utils.StringUtils;

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
    if (root == null) throw new NullPointerException("Value root cannot be null.");
    if (args == null || args.length < 1) throw new NullPointerException("Field args cannot be null.");
    if (value_type == null) throw new NullPointerException("Value value_type cannot be null.");
    this.root = root;
    this.args = args;
    this.value_type = value_type;
  }

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

  public int hashCode() {
    int hashCode = root.hashCode();
    for (int ix = 0; ix < args.length; ix++) {
      if (args[ix] != null)
        hashCode = (hashCode + args[ix].hashCode()) & 0x7FFFFFFF;
    }
    return hashCode;
  }

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
  
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append(root.toString());
    sb.append(".{");
    StringUtils.join(args, ", ", sb);
    sb.append('}');
    return sb.toString();
  }

  public void visit(JDOVisitorIF visitor) {
    visitor.visitable(root);
  }
  
}






