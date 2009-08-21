
// $Id$

package net.ontopia.topicmaps.viz;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;

/**
 * EXPERIMENTAL: Interface to define the application context.
 */
public interface ApplicationContextIF {
  public int getMaxLocality();

  public TypesConfigFrame getAssocFrame();

  public TypesConfigFrame getTopicFrame();

  public int getDefaultLocality();

  public boolean isApplet();

  public void goToTopic(TopicIF aTopic);

  public void openPropertiesURL(String aUrl);

  public void setStartTopic(TopicIF aTopic);

  public TopicIF getTopicForLocator(LocatorIF locator, TopicMapIF aTopicmap);

  public void loadTopic(TopicIF aTopic);

  public void focusNode(TMAbstractNode aNode);

  public void setScopingTopic(TopicIF aScope);

  public TopicIF getDefaultScopingTopic(TopicMapIF aTopicmap);

  public TopicIF getStartTopic(TopicMapIF aTopicmap);

  public ParsedMenuFile getEnabledItemIds();

  public void setVizPanel(VizPanel panel);

  public void setTmConfig(VizTopicMapConfigurationManager config);

  public void setView(TopicMapView view);
}
