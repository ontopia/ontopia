
package ontopoly.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;

/**
 * Represents a role type.
 */
public class RoleType extends AbstractTypingTopic {

  /**
   * Creates a new RoleType object.
   */
  public RoleType(TopicIF currTopic, TopicMap tm) {
    super(currTopic, tm);
  }

  @Override
  public LocatorIF getLocatorIF() {
    return PSI.ON_ROLE_TYPE;
  }
  
  public boolean equals(Object obj) {
    if (!(obj instanceof RoleType))
      return false;

    RoleType other = (RoleType) obj;
    return (getTopicIF().equals(other.getTopicIF()));
  }

  @Override
	public Collection<RoleField> getDeclaredByFields() {
    String query = "select $RF from "
			+ "on:has-role-type(%RT% : on:role-type, $RF : on:role-field)?";
    Map<String,TopicIF> params = Collections.singletonMap("RT", getTopicIF());

    QueryMapper<RoleField> qm = getTopicMap().newQueryMapper(RoleField.class);
    return qm.queryForList(query, params);
	}

}
