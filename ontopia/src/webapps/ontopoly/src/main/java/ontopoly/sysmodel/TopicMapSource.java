
// $Id: TopicMapSource.java,v 1.4 2009/04/20 06:55:46 geir.gronmo Exp $

package ontopoly.sysmodel;

import java.io.Serializable;


import net.ontopia.topicmaps.entry.TopicMapSourceIF;
import ontopoly.OntopolyContext;

/**
 * INTERNAL: Represents a source in the Ontopoly topic map repository.
 * Only sources which support create are represented. This class only
 * exists so the repository can be notified when new topic maps are
 * created.
 */
public class TopicMapSource implements Serializable {

  private String sourceId;
  private transient TopicMapSourceIF _source;

  protected TopicMapSource() {
  }

  /**
   * INTERNAL: Creates a new source wrapper.
   */
  public TopicMapSource(String sourceId) {
    this.sourceId = sourceId;
  }

  /**
   * INTERNAL: Returns the ID of the source.
   */
  public String getId() {
    return getTopicMapSource().getId();
  }

  /**
   * INTERNAL: Returns the display title of the source.
   */
  public String getTitle() {
    return getTopicMapSource().getTitle();
  }

  private synchronized TopicMapSourceIF getTopicMapSource() {
    if (_source == null)
      _source = OntopolyContext.getOntopolyRepository().getTopicMapRepository().getSourceById(sourceId);
    return _source;
  }

}
