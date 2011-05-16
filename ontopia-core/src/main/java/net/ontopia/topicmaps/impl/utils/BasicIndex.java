
// $Id: BasicIndex.java,v 1.2 2008/06/11 17:14:49 geir.gronmo Exp $

package net.ontopia.topicmaps.impl.utils;

import java.util.HashMap;
import java.util.Map;

import net.ontopia.topicmaps.core.index.IndexIF;

/**
 * INTERNAL: An abstract dynamic index superclass.
 */
public abstract class BasicIndex extends AbstractIndex implements EventListenerIF {
  
  protected Map<String, EventListenerIF> handlers = new HashMap<String, EventListenerIF>();

  public IndexIF getIndex() {
    return this;
  }

  // -----------------------------------------------------------------------------
  // EventListenerIF
  // -----------------------------------------------------------------------------

  public void processEvent(Object object, String event, Object new_value, Object old_value) {
    // System.out.print("Yeah:" + event);
    if (handlers.containsKey(event)) {
      (handlers.get(event)).processEvent(object, event, new_value, old_value);
    }
    // System.out.println(". Done.");
  }

  // -----------------------------------------------------------------------------
  // Event handlers
  // -----------------------------------------------------------------------------

  public abstract class EventHandler implements EventListenerIF, java.io.Serializable {
    public abstract void processEvent(Object object, String event, Object new_value, Object old_value);
    protected void addEvent(Object object, String event, Object value) {
      // if (!handlers.containsKey(event)) System.out.println("+event> " + event + "=" + value);
      (handlers.get(event)).processEvent(object, event, value, null);
    }
    protected void removeEvent(Object object, String event, Object value) {
      // if (!handlers.containsKey(event)) System.out.println("-event> " + event + "=" + value);
      (handlers.get(event)).processEvent(object, event, null, value);
    }
  }
  
}
