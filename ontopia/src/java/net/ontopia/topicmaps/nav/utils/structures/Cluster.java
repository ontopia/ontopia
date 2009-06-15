// $Id: Cluster.java,v 1.11 2004/11/12 11:47:19 grove Exp $

package net.ontopia.topicmaps.nav.utils.structures;

import java.util.Map;
import java.util.HashMap;

/** 
 * INTERNAL: Provides a structure for carrying presentation information from a ClusterTag
 * ClusterTags will pass a claster up to other topicmap tags if they implement
 * the SetterIF interface.
 *
 * <p>Cluster carries basic template information which will eventually be used by
 * Stringifiers which can accept render objects (renderName, renderTemplate, 
 * renderStringifier). 
 */     
public class Cluster implements ClusterIF {
  
  // members
  protected String renderTemplate;
  protected String renderName;
  protected String renderStringifier;
  /** Map with key: String, value: DisplayIF object */
  protected Map displays = new HashMap();

  /**
   * Empty constructor used by configuration objects which will use set methods.
   */
  public Cluster() {
  }
    
  /**
   * Constructor used by ClusterTag to complete full Cluster.
   */
  public Cluster(String renderName, String renderTemplate, String renderStringifier, Map displays) {
    this.renderName = renderName;
    this.renderTemplate = renderTemplate;
    this.renderStringifier = renderStringifier;      
    this.displays = displays;
  }

  // ...........
  // set methods
  // ...........
  
  public void setDisplays(Map m) { displays = m; }
  public void setRenderTemplate(String s) { renderTemplate = s; }
  public void setRenderName(String s) { renderName = s; }
  public void setRenderStringifier(String s) { renderStringifier = s; }

  // ------------------------------------------------------------------
  // Implementation of ClusterIF
  // ------------------------------------------------------------------

  public Map getDisplays() { return displays; }
  public String getRenderTemplate() { return renderTemplate; }
  public String getRenderName() { return renderName; }
  public String getRenderStringifier() { return renderStringifier; }

}





