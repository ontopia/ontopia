// $Id: ClusterWithContext.java,v 1.15 2008/06/12 14:37:18 geir.gronmo Exp $

package net.ontopia.topicmaps.nav.utils.structures;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collection;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;

import net.ontopia.topicmaps.nav.utils.NavUtils;
import net.ontopia.topicmaps.nav.context.UserFilterContextStore;

import org.apache.log4j.Logger;

/** 
 * INTERNAL: A wrapper class which carries a Cluster and an Map of
 * displaysWithContext.
 */
public class ClusterWithContext implements ClusterIF {

  // Define a logging category.
  static Logger log = Logger.getLogger(ClusterWithContext.class.getName());

  protected ClusterIF cluster;
  /** Map with key: String, value: DisplayIF instance (could be Display or DisplayWithContext) */
  protected Map displaysWithContext = new HashMap();

  /**
   * Simple constructor which wraps a Cluster so that
   * DisplaysWithContext can be added to it.
   */
  public ClusterWithContext(ClusterIF cluster) {
    this.cluster = cluster;
  }

  /**
   * Constructor which wraps a Cluster and updates the Displays it
   * already has so that they can have Context applied to them. The
   * configuration Clusters in application scope will need to be
   * treated in this way, as the current topic map is required to
   * create themes from the strings in Display.variantNameContext and
   * Display.baseNameContext. Typically this is done in the topic map
   * tags when no clusters have been defined on the JSP and the
   * default must be used.
   */
  public ClusterWithContext(ClusterIF cluster, TopicMapIF tm) {
    this(cluster, tm, null, null);
  }
  
  public ClusterWithContext(ClusterIF cluster, TopicMapIF tm,
                            UserFilterContextStore userFilterContext) {
    this(cluster, tm, userFilterContext, null);
  }

  public ClusterWithContext(ClusterIF cluster, TopicMapIF tm,
                            UserFilterContextStore userFilterContext,
                            TopicIF topic) {
    this.cluster = cluster;
    if (tm != null) {
      Map oldDisplays = cluster.getDisplays();
      Iterator it = cluster.getDisplays().keySet().iterator();
      while (it.hasNext()) {
        String thisKey = (String) it.next();
        Collection cDesignerTopicNameContext = null; 
        Collection cDesignerVariantNameContext = null; 
        // check base name
        String thisTopicNameContext = ((Display) oldDisplays.get(thisKey)).getTopicNameContext();
        if (!thisTopicNameContext.equals(""))
          cDesignerTopicNameContext = NavUtils.args2Topics(tm, thisTopicNameContext, null);
        // check variant name
        String thisVariantNameContext = ((Display) oldDisplays.get(thisKey)).getVariantNameContext();
        if (!thisVariantNameContext.equals("")) {
          cDesignerVariantNameContext = NavUtils.args2Topics(tm, thisVariantNameContext, null); 
        } 

        // make a new display
        if ((cDesignerTopicNameContext!=null && !cDesignerTopicNameContext.isEmpty()) ||
            (cDesignerVariantNameContext!=null && !cDesignerVariantNameContext.isEmpty()) ||
            userFilterContext != null) {
          Display actDisplay = (Display) oldDisplays.get(thisKey);
          DisplayWithContext newDisplay = new DisplayWithContext(actDisplay);
          if (cDesignerTopicNameContext != null) 
            newDisplay.addContext("designerTopicName", cDesignerTopicNameContext);
          if (cDesignerVariantNameContext != null) 
            newDisplay.addContext("designerVariantName", cDesignerVariantNameContext);

          // <niko-context-addition> 
          if (userFilterContext != null) {
            Collection baseNameThemes = new ArrayList(userFilterContext.getScopeTopicNames(tm));
            if (topic != null)
              baseNameThemes.add( topic );
            newDisplay.addContext("filterTopicName", baseNameThemes);
          }
          // </niko-context-addition> 

          // add it to this
          this.addDisplayWithContext(thisKey, newDisplay);
        }
      } // while
    }
  }
        
  /**
   * Adds a DisplayWithContext to the cluster. When the Displays are
   * requested this DisplayIF will be returned instead of the one with
   * no context.
   */
  public void addDisplayWithContext(String key, DisplayIF displayWithContext) {
    this.displaysWithContext.put(key, displayWithContext);
  }
    
  // -------------------------------------------------------------------------
  // implementation of the ClusterIF interface.
  // -------------------------------------------------------------------------

  public Map getDisplays() {
    // loop through keys in cluster.getDisplays() and update if there
    // is something with better context in displaysWithContext
    Map finalDisplays = new HashMap();
    Iterator it = cluster.getDisplays().keySet().iterator();
    while (it.hasNext()) {
      String thisKey = (String) it.next();
      if (displaysWithContext.get(thisKey) != null) 
        finalDisplays.put(thisKey, displaysWithContext.get(thisKey));
      else 
        finalDisplays.put(thisKey, cluster.getDisplays().get(thisKey));
    }
    return finalDisplays;   
  }
  
  public String getRenderTemplate()    { return cluster.getRenderTemplate(); }
  public String getRenderName()        { return cluster.getRenderName(); }
  public String getRenderStringifier() { return cluster.getRenderStringifier(); }  

}





