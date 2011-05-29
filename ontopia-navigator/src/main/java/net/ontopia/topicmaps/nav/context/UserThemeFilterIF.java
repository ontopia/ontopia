
package net.ontopia.topicmaps.nav.context;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;

/**
 * Interface for defining a method which filters out themes
 * which are not relevant to the user context.
 */
public interface UserThemeFilterIF {

  /**
   * Process theme and find out if it belongs to the user context and
   * should therefore be used for configuration.
   *
   * @return boolean: true if this theme should not be displayed
   *                  for user context configuration
   */
  public boolean shouldNotBeUsed(TopicIF actTheme);


  /**
   * gets TopicMapIF object
   */
  public TopicMapIF getTopicMap();

  /**
   * set TopicMapIF object
   */
  public void setTopicMap(TopicMapIF topicMap);

}





