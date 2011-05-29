
package net.ontopia.topicmaps.utils.ctm;

import net.ontopia.topicmaps.core.TopicIF;

public class NamedWildcardTopicGenerator extends AbstractTopicGenerator {
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

  public ValueGeneratorIF copy() {
    return this; // no state, so...
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
