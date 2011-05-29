
package net.ontopia.topicmaps.utils.ctm;

import net.ontopia.topicmaps.core.TopicIF;

public class WildcardTopicGenerator extends AbstractTopicGenerator {
  private ParseContextIF context;
  
  public WildcardTopicGenerator(ParseContextIF context) {
    this.context = context;
  }
  
  public TopicIF getTopic() {
    return context.makeAnonymousTopic();
  }

  public ValueGeneratorIF copy() {
    return this; // no state, so...
  }
}
