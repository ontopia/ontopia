// $Id: DisplayWithContext.java,v 1.14 2008/06/12 14:37:18 geir.gronmo Exp $

package net.ontopia.topicmaps.nav.utils.structures;

import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Collection;
import java.util.ArrayList;

import net.ontopia.topicmaps.core.TopicIF;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * INTERNAL: A wrapper class which carries a Display and an Map of Contexts.
 * The contexts (Collection of scope topics) will be appended by the user 
 * and application as the basic Display (set by the designer) moves through 
 * the application.
 */     
public class DisplayWithContext implements DisplayIF {

  // Define a logging category.
  protected static Logger log = LoggerFactory.getLogger(DisplayWithContext.class.getName());

  protected Display display;
  /** Map defined by key: String,
      value: Collection (of TopicIF objects used to define interested themes used for scoping */
  protected Map context = new HashMap();

  /**
   * Constructor which takes the Display object to be wrapped, ie. the object
   * to have context applied to it.
   */
  public DisplayWithContext(Display display) {
    this.display = display;
  }

  /**
   * get display object.
   */
  public Display getDisplay() {
    return display;
  }

  /**
   * Adds context for an aspect of a Display. 
   * Eg. Can add Context for "applicationTopicName".
   */
  public void addContext(String key, Collection scopes) {
    this.context.put(key, scopes);
  }     
  
  /**
   * Adds a theme to an aspect of a Display. 
   * Eg. Can a theme to "applicationTopicName".
   */ 
  public void addContextTheme(String key, TopicIF theme) {
    Collection scopes = (Collection) context.get(key);
    if (context.get(key) == null) {
      scopes = new HashSet();
      context.put(key, scopes);
    }

    scopes.add(theme);
  }

  /**
   * Clear the context of a Display.
   */
  public void clearContext(String key) {
    Collection scopes = (Collection) context.get(key);
    if (scopes != null)
      scopes.clear();
  }
  
  // -----------------------------------------------------------------------
  // implentation of the DisplayIF interface
  // -----------------------------------------------------------------------

  public String getObject() { return display.getObject(); }     
  public String getArgs() { return display.getArgs(); }
  public String getRenderName() { return display.getRenderName(); } 
  public String getRenderTemplate() { return display.getRenderTemplate(); }
  public String getRenderStringifier() { return display.getRenderStringifier(); } 
  public String getTopicNameContext() { return display.getTopicNameContext(); }
  public String getTopicNameGrabber() { return display.getTopicNameGrabber(); }
  public String getTopicNameDecider() { return display.getTopicNameDecider(); }
  public String getVariantNameContext() { return display.getVariantNameContext(); }
  public String getVariantNameGrabber() { return display.getVariantNameGrabber(); }
  public String getVariantNameDecider() { return display.getVariantNameDecider(); } 
  public String getDisplayTitle() { return display.getDisplayTitle(); }
  public String getDisplayHref() { return display.getDisplayHref(); }
  public String getDisplayBehaviour() { return display.getDisplayBehaviour(); }   
  public Map getContext() { return context; } 

}





