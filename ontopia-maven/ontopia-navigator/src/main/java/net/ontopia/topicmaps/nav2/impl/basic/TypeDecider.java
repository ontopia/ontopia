// $Id: TypeDecider.java,v 1.7 2008/06/13 08:36:27 geir.gronmo Exp $

package net.ontopia.topicmaps.nav2.impl.basic;

import java.net.MalformedURLException;

import net.ontopia.utils.DeciderIF;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.utils.TypeHierarchyUtils;
import net.ontopia.infoset.core.*;
import net.ontopia.infoset.impl.basic.URILocator;

import net.ontopia.topicmaps.nav2.core.*;

/**
 * INTERNAL: Decider for verifying if a given topic type is identical or
 * a subclass of a reference topic type (for example occurrence type).
 */
public class TypeDecider implements DeciderIF, NavigatorDeciderIF {

  // constants
  public static final String OCC_METADATA = "metadata";
  public static final String OCC_DESCRIPTION = "description";

  // members
  private static TypeHierarchyUtils hierUtils;
  private LocatorIF refTypingTopicLocator;
  
  /**
   * INTERNAL: default constructor. Use the constants of this class to
   * specify to which type should be compared.
   */
  public TypeDecider(String type)
    throws MalformedURLException {
    // typingTopic
    String refTypingTopicURL = null;
    if (type.equals(OCC_METADATA))
      refTypingTopicURL = NavigatorConfigurationIF.DEFVAL_OCCTYPE_METADATA;
    if (type.equals(OCC_DESCRIPTION))
      refTypingTopicURL = NavigatorConfigurationIF.DEFVAL_OCCTYPE_DESCRIPTION;
    refTypingTopicLocator = new URILocator(refTypingTopicURL);
  }
  
  /**
   * INTERNAL: Constructor which tries to retrieve the PSI for the
   * related topic types from the navigator configuration
   */
  public TypeDecider(NavigatorPageIF contextTag, String type)
    throws MalformedURLException {

    if (hierUtils == null)
      hierUtils = new TypeHierarchyUtils();
    
    // get URIs of reference types
    NavigatorConfigurationIF navConf = contextTag.getNavigatorConfiguration();
    
    // typingTopic
    String refTypingTopicURL = null;
    if (type.equals(OCC_METADATA))
      refTypingTopicURL = navConf
        .getProperty(NavigatorConfigurationIF.OCCTYPE_METADATA,
                     NavigatorConfigurationIF.DEFVAL_OCCTYPE_METADATA);
    if (type.equals(OCC_DESCRIPTION))
      refTypingTopicURL = navConf
        .getProperty(NavigatorConfigurationIF.OCCTYPE_DESCRIPTION,
                     NavigatorConfigurationIF.DEFVAL_OCCTYPE_DESCRIPTION);
    refTypingTopicLocator = new URILocator(refTypingTopicURL);
  }

  // -----------------------------------------------------------
  // Implementation of NavigatorDeciderIF
  // -----------------------------------------------------------

  public boolean ok(NavigatorPageIF contextTag, Object obj) {
    if (obj == null)
      return false;
    TopicIF typingTopic = (TopicIF) obj;
    // retrieve topicmap
    TopicMapIF tm = typingTopic.getTopicMap();
    TopicIF refTypingTopic = tm.getTopicBySubjectIdentifier(refTypingTopicLocator);

    if ((refTypingTopic != null)
        && (refTypingTopic.equals(typingTopic)
            || hierUtils.getSupertypes(typingTopic).contains(refTypingTopic)))
      return true;
    return false;
  }

  // -----------------------------------------------------------
  // Implementation of DeciderIF
  // -----------------------------------------------------------

  public boolean ok(Object obj) {
    return ok(null, obj);
  }
  
}





