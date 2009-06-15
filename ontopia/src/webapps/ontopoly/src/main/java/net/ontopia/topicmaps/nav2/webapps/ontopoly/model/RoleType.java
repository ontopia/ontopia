// $Id: RoleType.java,v 1.3 2009/04/21 06:23:51 geir.gronmo Exp $

package net.ontopia.topicmaps.nav2.webapps.ontopoly.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.utils.RowMapperIF;

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

//  /**
//   * Indicates whether the role type can also be used as a topic type.
//   */
//  public boolean isValidTopicType() {
//    String query = "instance-of(%topic% , on:topic-type)?";
//
//    Map params = Collections.singletonMap("topic", getTopicIF());
//
//    return getTopicMap().getQueryWrapper().isTrue(query, params);
//  }
//
//  /**
//   * Enable or disable this role type to be used as a topic type depending of
//   * the parameter (value) and the RoleType object's current state.
//   * 
//   * @param value
//   *            value indicates whether this RoleType object is going to be used
//   *            as a TopicType.
//   */
//  public void setValidTopicType(boolean value) {
//    TopicIF tType = OntopolyModelUtils.getTopicIF(getTopicMap(), PSI.ON, "topic-type");
//
//		boolean validTopicType = isValidTopicType();
//    if (value && !validTopicType) {
//      getTopicIF().addType(tType);
//    } else if (!value && validTopicType) {
//      getTopicIF().removeType(tType);
//    }
//  }

	public Collection getDeclaredByFields() {
    String query = "select $RF from "
			+ "on:has-role-type(%RT% : on:role-type, $RF : on:role-field)?";
    Map params = Collections.singletonMap("RT", getTopicIF());

    return getTopicMap().getQueryWrapper().queryForList(query,
        new RowMapperIF() {
          public Object mapRow(QueryResultIF result, int rowno) {
						TopicIF roleFieldTopic = (TopicIF)result.getValue(0);
						return new RoleField(roleFieldTopic, getTopicMap());
					}
				}, params);
	}

//	public Collection getUsedBy() {
//    String query = "select $TT from "
//			+ "on:has-role-type(%RT% : on:role-type, $FD : on:role-field), "
//			+ "on:has-field($FD : on:field-definition, $TT : on:topic-type)?";
//    Map params = Collections.singletonMap("RT", getTopicIF());
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
