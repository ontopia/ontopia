
package net.ontopia.topicmaps.impl.rdbms;

import net.ontopia.infoset.core.*;
import net.ontopia.topicmaps.core.*;

/**
 * INTERNAL: A locator class used for representing topic map object
 * source locators.<p>
 *
 * No normalization or absolutization is done.<p>
 */

public class SourceLocator extends RDBMSLocator {
  
  // ---------------------------------------------------------------------------
  // Data members
  // ---------------------------------------------------------------------------
  
  protected String indicator;
  protected long tmobject;
  protected long topicmap;
  
  public SourceLocator() {
  }

  public SourceLocator(LocatorIF locator) {
    super(locator);
  }

  public long _getTMObject() {
    return tmobject;
  }

  public void _setTMObject(long tmobject) {
    this.tmobject = tmobject;
  }

  public long _getTopicMap() {
    return topicmap;
  }

  public void _setTopicMap(long topicmap) {
    this.topicmap = topicmap;
  }

  public String _getClassIndicator() {
    return indicator;
  }

  public void _setClassIndicator(String indicator) {
    this.indicator = indicator;
  }
  
}
