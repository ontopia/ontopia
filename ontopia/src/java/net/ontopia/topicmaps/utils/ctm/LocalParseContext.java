
// $Id: GlobalParseContext.java,v 1.3 2009/02/27 12:01:06 lars.garshol Exp $

package net.ontopia.topicmaps.utils.ctm;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.net.MalformedURLException;
import net.ontopia.utils.CompactHashSet;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.xml.InvalidTopicMapException;

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
