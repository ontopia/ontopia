
package net.ontopia.topicmaps.webed.impl.utils;

import java.util.Collection;

/**
 * INTERNAL: Helper class for storing a collection of TopicIF objects
 * for type specification as well as a flag which indicates that the
 * untyped type is allowed.
 */
public class TypeStorage {

  protected Collection types;
  protected boolean untypedIncluded;
  
  public TypeStorage(Collection types, boolean untypedIncluded) {
    this.types = types;
    this.untypedIncluded = untypedIncluded;
  }

  public Collection getTypes() {
    return types;
  }

  public void setTypes(Collection types) {
    this.types = types;
  }

  public boolean isUntypedIncluded() {
    return untypedIncluded;
  }

  public void setUntypedIncluded(boolean untypedIncluded) {
    this.untypedIncluded = untypedIncluded;
  }
  
}
