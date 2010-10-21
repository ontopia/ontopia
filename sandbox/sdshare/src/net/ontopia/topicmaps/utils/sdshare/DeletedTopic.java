
package net.ontopia.topicmaps.utils.sdshare;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TMObjectIF;

/**
 * INTERNAL: Represents the deletion of a single topic. We need this
 * to preserve the identity of deleted topics, since that will no
 * longer exist in the topic map. Making these a separate class in
 * order to save memory with long change lists. Could make it even
 * more compact, but not bothering right now.
 */
public class DeletedTopic extends ChangedTopic {
  private Collection<LocatorIF> sis;
  private Collection<LocatorIF> sls;
  private Collection<LocatorIF> iis;
  
  public DeletedTopic(TMObjectIF object) {
    super(object.getObjectId()); // sufficient for duplicate removal
    TopicIF topic = (TopicIF) object;
    this.sis = copyOf(topic.getSubjectIdentifiers());
    this.sls = copyOf(topic.getSubjectLocators());
    this.iis = copyOf(topic.getItemIdentifiers());
  }  

  public boolean isDeleted() {
    return true;
  }
  
  public Collection<LocatorIF> getSubjectIdentifiers() {
    return sis;
  }

  public Collection<LocatorIF> getSubjectLocators() {
    return sls;
  }

  public Collection<LocatorIF> getItemIdentifiers() {
    return iis;
  }
  
  private Collection<LocatorIF> copyOf(Collection<LocatorIF> ids) {
    if (ids.isEmpty())
      return Collections.EMPTY_SET;

    return new ArrayList<LocatorIF>(ids);
  }
  
}
