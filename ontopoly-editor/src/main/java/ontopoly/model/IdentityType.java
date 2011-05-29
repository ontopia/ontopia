
package ontopoly.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.utils.RowMapperIF;

/**
 * Represents an identity type.
 */
public class IdentityType extends AbstractTypingTopic {

  /**
   * Creates a new IdentityType object.
   */
  public IdentityType(TopicIF type, TopicMap tm) {
    super(type, tm);
  }

  @Override
  public LocatorIF getLocatorIF() {
    return PSI.ON_IDENTITY_TYPE;
  }
  
  public boolean equals(Object obj) {
    if (!(obj instanceof IdentityType))
      return false;

    IdentityType other = (IdentityType) obj;
    return (getTopicIF().equals(other.getTopicIF()));
  }

  @Override
	public Collection<IdentityField> getDeclaredByFields() {
    String query = "select $FD from on:has-identity-type(%TYPE% : on:identity-type, $FD : on:identity-field)?";
    Map<String,TopicIF> params = Collections.singletonMap("TYPE", getTopicIF());

    QueryMapper<IdentityField> qm = getTopicMap().newQueryMapper(IdentityField.class);    
    return qm.queryForList(query,
        new RowMapperIF<IdentityField>() {
          public IdentityField mapRow(QueryResultIF result, int rowno) {
						TopicIF fieldTopic = (TopicIF)result.getValue(0);
						return new IdentityField(fieldTopic, getTopicMap(), new IdentityType(getTopicIF(), getTopicMap()));
					}
				}, params);
	}

}
