
// $Id$

package net.ontopia.topicmaps.viz;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;

/**
 * EXPERIMENTAL: Interface to define the application context.
 */
public interface ApplicationContextIF {
  /**
   * Returns the maximum number of nodes (locality) to display linked
   * to the central node.  Large numbers (> 3) can have a significant
   * performance impact when used, especially for large maps
   * 
   * @return the maximum allowed locality
   */
  public int getMaxLocality();

  /**
   * Display the configuration frame for associations (edges)
   * 
   * @return The association configuration frame
   */
  public TypesConfigFrame getAssocFrame();

  /**
   * Display the configuration frame for topics
   * 
   * @return the topic configuration frame
   */
  public TypesConfigFrame getTopicFrame();

  /**
   * Get the default locality for the application.  This is usually 1
   * 
   * @return the default locality
   */
  public int getDefaultLocality();

  /**
   * Is this an applet?
   * 
   * @return boolean indicating if this context is an applet
   */
  public boolean isApplet();

  /**
   * Set the focus of the map to the specified topic
   * 
   * @param aTopic Topic to set as the central focus
   */
  public void goToTopic(TopicIF aTopic);

  /**
   * Opens the supplied url string in a browser window. Which window
   * is used is defined by the 'propTarget' applet parameter.  Does
   * nothing for non applet context.
   * 
   * @param url String representing the target url
   */
  public void openPropertiesURL(String aUrl);

  /**
   * Assign the starting topic of the map to display.
   * 
   * @param aTopic Start Topic
   */
  public void setStartTopic(TopicIF aTopic);

  /**
   * Returns the topic with the given subject identifier, if any.
   * 
   * @param locator the subject identifier
   * @param aTopicmap the topic map to look up in
   * 
   * @return topic with given subject identifier (or null if none)
   */
  public TopicIF getTopicForLocator(LocatorIF locator, TopicMapIF aTopicmap);

  /**
   * Get a topic from the associated store
   * 
   * @param aTopic to read from the store
   */
  public void loadTopic(TopicIF aTopic);

  /**
   * Set the node as the focus of the map
   * 
   * @param aNode node to focus on
   */
  public void focusNode(TMAbstractNode aNode);

  /**
   * Set the scoping topic for the map
   * 
   * @param aScope topic to set scope
   */
  public void setScopingTopic(TopicIF aScope);

  /**
   * Get the default scoping topic
   * 
   * @param aTopicmap map that contains the topic 
   * 
   * @return default scoping topic
   */
  public TopicIF getDefaultScopingTopic(TopicMapIF aTopicmap);

  /**
   * Return the start topic defined on the map
   * 
   * @param aTopicmap map containing topic
   * 
   * @return start topic
   */
  public TopicIF getStartTopic(TopicMapIF aTopicmap);

  /**
   * Return the menu of enabled items
   * 
   * @return menu containing enabled items
   */
  public ParsedMenuFile getEnabledItemIds();

  /**
   * Store the panel containing the map
   * 
   * @param panel to save 
   */
  public void setVizPanel(VizPanel panel);

  /**
   * Store the TopicMapConfiguration
   * 
   * @param config to save
   */
  public void setTmConfig(VizTopicMapConfigurationManager config);

  /**
   * Set the view
   * 
   * @param view to set
   */
  public void setView(TopicMapView view);
}
