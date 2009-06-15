// $Id: ClusterIF.java,v 1.10 2004/11/12 11:47:19 grove Exp $

package net.ontopia.topicmaps.nav.utils.structures;

import java.util.Map;

/** 
 * INTERNAL: Interface for Clusters.
 */     
public interface ClusterIF {

  // get methods.
  public Map getDisplays();
  public String getRenderTemplate();
  public String getRenderName();
  public String getRenderStringifier();                 
}





