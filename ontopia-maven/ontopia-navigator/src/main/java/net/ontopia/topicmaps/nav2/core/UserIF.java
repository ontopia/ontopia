
// $Id: UserIF.java,v 1.29 2007/07/13 12:35:07 geir.gronmo Exp $

package net.ontopia.topicmaps.nav2.core;

import java.util.List;
import net.ontopia.utils.HistoryMap;
import net.ontopia.topicmaps.nav.context.UserFilterContextStore;
import org.slf4j.Logger;

/**
 * INTERNAL: interface for classes which implement user data management
 * usually stored in the session.
 */
public interface UserIF {

  /** Default User identifier */
  public static final String COMMON_USER = "defaultUser";
  
  /** Default Model name */
  public static final String DEFAULT_MODEL = "complete";

  /** Default View/Template */
  public static final String DEFAULT_VIEW = "no_frames";

  /** Default Skin/CSS */
  public static final String DEFAULT_SKIN = "ontopia";

  /**
   * INTERNAL: Gets the user identifier.
   */
  public String getId();
  
  /**
   * INTERNAL: Gets the Filter Context object which stores for each
   * topicmap a set of themes the user has selected.
   */
  public UserFilterContextStore getFilterContext();

  /**
   * INTERNAL: Gets the last used objects (instances of Object,
   * specialisation through the web application) which the user has
   * visited.
   *
   * @since 1.2.5
   */
  public HistoryMap getHistory();

  /**
   * INTERNAL: Sets the last used objects that are in relation to the
   * user and his path through the web application.
   *
   * @since 1.2.5
   */
  public void setHistory(HistoryMap hm);

  /**
   * INTERNAL: Gets the Slf4J <code>Logger</code> object to which the
   * User object should transmit the log messages. <b>This method is
   * now deprecated, and we recommend using addLogMessage()
   * instead.</b>
   *
   * @since 1.3.2
   * @deprecated Use addLogMessage() instead
   */
  public Logger getLogger();

  /**
   * INTERNAL: Adds a message to the user's log. The order of these
   * messages is preserved, but if too many messages are added, the
   * latest ones are lost.
   */
  public void addLogMessage(String message);

  /**
   * INTERNAL: Clears the user's log.
   */
  public void clearLog();
  
  /**
   * INTERNAL: Gets the current log messages from the ring buffer.
   *
   * @since 1.3.2
   */
  public List getLogMessages();

  /**
   * INTERNAL: Stores a working bundle of objects under the specified
   * <code>id</code>.
   *
   * @since 1.3.2
   */
  public void addWorkingBundle(String bundle_id, Object object);

  /**
   * INTERNAL: Gets an ordered lists of objects (parameter name as
   * key, list of objects as value) grouped together by the given
   * identifier <code>id</code>.
   *
   * @since 1.3.2
   */
  public Object getWorkingBundle(String bundle_id);

  /**
   * INTERNAL: Removes the specified working bundle.
   *
   * @since 1.3.2
   */
  public void removeWorkingBundle(String bundle_id);   
  
  
  // -----------------------------------------------------------
  // Model-View-Skin (MVS) accessor and mutator methods
  // -----------------------------------------------------------
  
  /**
   * Get Model Setting for MVS.
   */
  public String getModel();

  /**
   * Set Model Setting.
   */
  public void setModel(String model);
  
  /**
   * Get View (Template) Setting for MVS.
   */
  public String getView();

  /**
   * Set View Setting.
   */
  public void setView(String view);
  
  /**
   * Get Skin (Stylesheet) Setting for MVS.
   */
  public String getSkin();
  
  /**
   * Set Skin Setting.
   */
  public void setSkin(String skin);
  
}
