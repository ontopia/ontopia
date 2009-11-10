
// $Id: NameType.java,v 1.3 2009/04/21 06:23:51 geir.gronmo Exp $

package net.ontopia.topicmaps.nav2.webapps.ontopoly.model;

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
public class NameType extends AbstractTypingTopic {

  /**
   * Creates a new NameType object.
   */
  public NameType(TopicIF currTopic, TopicMap tm) {
    super(currTopic, tm);
  }

  @Override
  public LocatorIF getLocatorIF() {
    return PSI.ON_NAME_TYPE;
  }
  
  public boolean equals(Object obj) {
    if (!(obj instanceof NameType))
      return false;

    NameType other = (NameType) obj;
    return (getTopicIF().equals(other.getTopicIF()));
  }

  /**
   * Returns true if name type is on:untyped.
	 */
	public boolean isUntypedName() {
    return getTopicIF().getSubjectIdentifiers().contains(PSI.ON_UNTYPED_NAME);
	}

	@Override
	public Collection<NameField> getDeclaredByFields() {
    String query = "select $FD from on:has-name-type(%TYPE% : on:name-type, $FD : on:name-field)?";
    Map<String,TopicIF> params = Collections.singletonMap("TYPE", getTopicIF());

    QueryMapper<NameField> qm = getTopicMap().newQueryMapper(NameField.class);    
    return qm.queryForList(query,
        new RowMapperIF<NameField>() {
          public NameField mapRow(QueryResultIF result, int rowno) {
						TopicIF fieldTopic = (TopicIF)result.getValue(0);
						return new NameField(fieldTopic, getTopicMap(), new NameType(getTopicIF(), getTopicMap()));
					}
				}, params);
	}

//	public Collection getUsedBy() {
//    String query = "select $TT from "
//			+ "on:has-name-type(%NT% : on:name-type, $FD : on:name-field), "
//			+ "on:has-field($FD : on:field-definition, $TT : on:field-owner)?";
//    Map params = Collections.singletonMap("NT", getTopicIF());
//
//    return getTopicMap().getQueryWrapper().queryForList(query,
//        new RowMapperIF() {
//          public Object mapRow(QueryResultIF result, int rowno) {
//						TopicIF topicType = (TopicIF)result.getValue(0);
//						return new TopicType(topicType, getTopicMap());
//					}
//				}, params);
//	}

}
