
// $Id: IdentityType.java,v 1.3 2009/04/21 06:23:51 geir.gronmo Exp $

package net.ontopia.topicmaps.nav2.webapps.ontopoly.model;

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

	public Collection getDeclaredByFields() {
    String query = "select $FD from on:has-identity-type(%TYPE% : on:identity-type, $FD : on:identity-field)?";
    Map params = Collections.singletonMap("TYPE", getTopicIF());

    return getTopicMap().getQueryWrapper().queryForList(query,
        new RowMapperIF() {
          public Object mapRow(QueryResultIF result, int rowno) {
						TopicIF fieldTopic = (TopicIF)result.getValue(0);
						return new IdentityField(fieldTopic, getTopicMap(), new IdentityType(getTopicIF(), getTopicMap()));
					}
				}, params);
	}

//	public Collection getUsedBy() {
//    String query = "select $TT from "
//			+ "on:has-identity-type(%IT% : on:identity-type, $FD : on:identity-field), "
//			+ "on:has-field($FD : on:field-definition, $TT : on:field-owner)?";
//    Map params = Collections.singletonMap("IT", getTopicIF());
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
