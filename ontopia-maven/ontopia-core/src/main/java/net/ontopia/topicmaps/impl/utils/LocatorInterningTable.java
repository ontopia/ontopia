
package net.ontopia.topicmaps.impl.utils;

import java.util.Map;
import java.util.HashMap;
import net.ontopia.infoset.core.LocatorIF;

/**
 * INTERNAL: This class keeps an interning table of LocatorIF objects,
 * allowing implementations to get rid of duplicate locators to save
 * memory.
 */
public class LocatorInterningTable {
  private static final Map<LocatorIF,LocatorIF> interningTable =
    new HashMap<LocatorIF,LocatorIF>();
  
  private LocatorInterningTable() {
  }

  public static synchronized LocatorIF intern(LocatorIF loc) {
    LocatorIF interned = interningTable.get(loc);
    if (interned == null) {
      interned = loc;
      interningTable.put(loc, loc);
    }
    return interned;
  }

  public synchronized void clear(){
    interningTable.clear();
  }   
}