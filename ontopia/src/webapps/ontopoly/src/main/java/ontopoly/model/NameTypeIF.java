
package ontopoly.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.utils.RowMapperIF;

/**
 * Represents a name type.
 */
public interface NameTypeIF extends TypingTopicIF {

  /**
   * Returns true if name type is on:untyped.
   */
  public boolean isUntypedName();

}
