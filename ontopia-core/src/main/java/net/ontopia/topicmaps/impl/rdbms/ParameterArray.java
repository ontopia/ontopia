
// $Id: ParameterArray.java,v 1.3 2005/10/26 09:05:26 grove Exp $

package net.ontopia.topicmaps.impl.rdbms;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: Object wrapper class for query parameter array. This
 * wrapper class is purely used to make it possible to use arrays as
 * map keys.<p>
 */
public class ParameterArray implements Externalizable {
  protected Object[] params;
  
  public ParameterArray() {
    // used with serialization only
  }

  public ParameterArray(Object[] params) {
    this.params = params;
  }

  public Object[] getArray() {
    return params;
  }

  public String toString() {
    return "PA:" + java.util.Arrays.asList(params);
  }

  public int hashCode() {
    int result = 1;
    for (int i=0; i < params.length; i++) {
      result = 31*result + (params[i] == null ? 0 : params[i].hashCode());
    }
    return result;
  }

  public boolean equals(Object other) {
    ParameterArray o = (ParameterArray)other;
    for (int i=0; i < params.length; i++) {
      if (params[i] == null) {
        if (o.params[i] == null)
          continue;
        else
          return false;
      } else {
        if (o.params[i] == null)
          return false;
        else
          if (!params[i].equals(o.params[i]))
            return false;
      }
    }
    return true;
  }

  // ---------------------------------------------------------------------------
  // Externalization
  // ---------------------------------------------------------------------------
  
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeObject(params);
  }

  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    params = (Object[])in.readObject();
  }

  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException e) {
      // Ignore
      throw new OntopiaRuntimeException(e);
    }
  }
}
