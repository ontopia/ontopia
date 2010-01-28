
package net.ontopia.topicmaps.utils.ctm;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;

public class LocalParseContext extends GlobalParseContext {
  private ParseContextIF parent;
  
  public LocalParseContext(TopicMapIF topicmap, LocatorIF base,
                           ParseContextIF parent) {
    super(topicmap, base);
    this.parent = parent;
  }
  
  public TopicIF makeAnonymousTopic() {
    return parent.makeAnonymousTopic();
  }

  public TopicIF makeAnonymousTopic(String wildcard_name) {
    return parent.makeAnonymousTopic(wildcard_name);
  }
}
