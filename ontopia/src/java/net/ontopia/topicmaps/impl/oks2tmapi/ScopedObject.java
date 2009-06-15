
// $Id: ScopedObject.java,v 1.3 2004/05/12 15:39:26 grove Exp $

package net.ontopia.topicmaps.impl.oks2tmapi;

import java.util.*;
import net.ontopia.topicmaps.core.*;

/**
 * INTERNAL: OKS->TMAPI object wrapper.
 */

public abstract class ScopedObject extends TopicMapObject implements org.tmapi.core.ScopedObject {

  ScopedObject(TopicMap tm) {
    super(tm);
  }

  public Set getScope() {
    return tm.wrapSet(((ScopedIF)getWrapped()).getScope());
  }

  public void addScopingTopic(org.tmapi.core.Topic topic) {
    ((ScopedIF)getWrapped()).addTheme(tm.unwrapTopic(topic));
  }

  public void removeScopingTopic(org.tmapi.core.Topic topic) {
    ((ScopedIF)getWrapped()).removeTheme(tm.unwrapTopic(topic));
  }

}
