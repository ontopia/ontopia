
// $Id: ActionConfigRegistrator.java,v 1.1 2003/12/22 19:15:08 larsga Exp $

package net.ontopia.topicmaps.webed.impl.utils;

import javax.servlet.ServletContext;
import net.ontopia.topicmaps.webed.impl.basic.*;

import org.apache.log4j.Logger;

/**
 * INTERNAL: Keeps track with registering the action registry object
 * in the web application scope.
 */
public class ActionConfigRegistrator implements ConfigurationObserverIF {

  // initialization of log facility
  private static Logger log = Logger
    .getLogger(ActionConfigRegistrator.class.getName());
  
  protected ServletContext ctxt;
  
  public ActionConfigRegistrator(ServletContext ctxt) {
    this.ctxt = ctxt;
  }

  public void configurationChanged(Object configuration) {
    // remove registry from servlet context
    ctxt.removeAttribute(Constants.AA_REGISTRY);
    // set new registry to servlet context
    log.info("setting action registry in application scope.");
    ctxt.setAttribute(Constants.AA_REGISTRY, configuration);
  }
  
}
