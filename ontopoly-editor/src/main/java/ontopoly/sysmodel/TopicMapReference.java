
package ontopoly.sysmodel;

import java.io.Serializable;

import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import ontopoly.OntopolyContext;

/**
 * INTERNAL: Represents a topic map in the Ontopoly topic map
 * repository.
 */
public class TopicMapReference implements Serializable {

  protected String referenceId;

  protected TopicMapReference() {
  }

  /**
   * INTERNAL: Creates a reference to a non-Ontopoly topic map.
   */
  TopicMapReference(String referenceId) {
    this.referenceId = referenceId;
  }

  /**
   * INTERNAL: Creates a reference to an Ontopoly topic map. The
   * reference parameter will be null if the topic map does not
   * actually exist.
   */
  TopicMapReference(TopicMapReferenceIF reference) {
    this.referenceId = reference.getId();
  }

  protected TopicMapReferenceIF getReference() {
    return OntopolyContext.getOntopolyRepository().getTopicMapRepository().getReferenceByKey(referenceId);
  }

  /**
   * INTERNAL: Returns the ID of the reference (like 'foo.xtm').
   */
  public String getId() {
    return referenceId;
  }

  /**
   * INTERNAL: Returns the name of the topic map. For non-Ontopoly
   * topic maps this will be the same as the ID.
   */
  public String getName() {
    TopicMapReferenceIF reference = getReference();
    return reference == null ? referenceId : reference.getTitle();
  }

  /**
   * INTERNAL: Tests if the topic map is actually in the repository.
   */
  public boolean isPresent() {
    return getReference() != null;
  }

  @Override
  public boolean equals(Object o) {
      if (o instanceof TopicMapReference) {
        return ((TopicMapReference)o).referenceId.equals(referenceId);
      }
      return false;
  }

  @Override
  public int hashCode() {
      return referenceId.hashCode();
  }

  @Override
  public String toString() {
    return "<TopicMapReference " + getId() + ">";
  }

}
