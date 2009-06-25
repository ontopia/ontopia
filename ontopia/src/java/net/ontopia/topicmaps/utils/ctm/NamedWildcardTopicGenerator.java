
// $Id: NamedWildcardTopicGenerator.java,v 1.3 2009/02/27 12:01:50 lars.garshol Exp $

package net.ontopia.topicmaps.utils.ctm;

import net.ontopia.topicmaps.core.TopicIF;

public class NamedWildcardTopicGenerator implements TopicGeneratorIF {
  private ParseContextIF context;
  private String name;
  private TopicIF topic; // made only once

  public NamedWildcardTopicGenerator(ParseContextIF context, String name) {
    this.context = context;
    this.name = name;
  }
  
  public TopicIF getTopic() {
    if (topic == null)
      topic = context.makeAnonymousTopic(name);
    return topic;
  }

  public TopicGeneratorIF copyTopic() {
    return this; // no state, so... FIXME: well, that ain't entirely true...
  }

  /**
   * Called when the parse context the named wildcard occurs in ends,
   * so that next time this named wildcard is seen a new topic will
   * need to be created.
   */
  public void contextEnd() {
    topic = null;
  }
}
