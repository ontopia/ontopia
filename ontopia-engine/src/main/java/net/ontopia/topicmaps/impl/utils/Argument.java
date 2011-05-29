
package net.ontopia.topicmaps.impl.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * INTERNAL: Represents one argument in the signature checked by
 * ArgumentValidator.
 */
public class Argument {
  private List<Class<?>> types;
  private boolean optional;
  private boolean repeatable;
  private boolean mustBeBound;
  private boolean multiValue;

  public Argument() {
    types = new ArrayList<Class<?>>();
  }

  public void addType(Class<?> type) {
    types.add(type);
  }

  public Class<?>[] getTypes() {
    Class<?>[] a = new Class<?>[types.size()];
    for (int ix = 0; ix < a.length; ix++)
      a[ix] = types.get(ix);
    return a;
  }

  public void setOptional() {
    optional = true;
  }

  public void setRepeatable() {
    repeatable = true;
  }

  public void setMustBeBound() {
    mustBeBound = true;
  }
  
  public void setMultiValue() {
    multiValue = true;
  }
  
  public boolean isOptional() {
    return optional;
  }

  public boolean isRepeatable() {
    return repeatable;
  }

  public boolean isMultiValue() {
    return multiValue;
  }
  
  public boolean mustBeBound() {
    return mustBeBound;
  }

  public boolean allows(Class<?> type) {
    for (int ix = 0; ix < types.size(); ix++) {
      Class<?> required = types.get(ix);
      if (required.isAssignableFrom(type))
        return true;
    }
    return false;
  }
    
  public boolean requires(Class<?> type) {
    return types.size() == 1 && type.equals(types.get(0));
  }
}
