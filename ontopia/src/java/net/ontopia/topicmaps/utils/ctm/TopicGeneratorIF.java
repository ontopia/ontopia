
// $Id: TopicGeneratorIF.java,v 1.2 2009/02/27 12:03:21 lars.garshol Exp $

package net.ontopia.topicmaps.utils.ctm;

import net.ontopia.topicmaps.core.TopicIF;

public interface TopicGeneratorIF {

  public TopicIF getTopic();

  public TopicGeneratorIF copyTopic();
  
}
