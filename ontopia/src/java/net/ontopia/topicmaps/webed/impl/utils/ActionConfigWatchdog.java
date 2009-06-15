
// $Id: ActionConfigWatchdog.java,v 1.1 2003/12/22 19:15:08 larsga Exp $

package net.ontopia.topicmaps.webed.impl.utils;

import net.ontopia.utils.FileWatchdog;

/**
 * INTERNAL: 
 */
public class ActionConfigWatchdog extends FileWatchdog {

  protected ActionConfigurator configurator;

  protected ActionConfigWatchdog() {
    super();
  }
  
  public ActionConfigWatchdog(ActionConfigurator configurator) {
    this.configurator = configurator;
    initialize(configurator.getFileName());
  }

  /**
   * Calls {@link ActionConfigurator#readRegistryConfiguration()} to
   * reconfigure the action registry.
   */
  public void doOnChange() {
    configurator.readRegistryConfiguration();
  }
  
}
