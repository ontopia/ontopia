// $Id: AssociationField.java,v 1.8 2009/04/30 09:53:42 geir.gronmo Exp $

package net.ontopia.topicmaps.nav2.webapps.ontopoly.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.utils.OntopolyModelUtils;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.utils.RowMapperIF;
import net.ontopia.utils.CollectionUtils;
import net.ontopia.utils.ObjectUtils;

/**
 * Represents an association field.
 */
public class AssociationField extends Topic {

	private AssociationType cachedAssociationType;
	private List cachedFieldsForRoles;

  public AssociationField(TopicIF topic, TopicMap tm) {
		super(topic, tm);
  }

  public AssociationField(TopicIF topic, TopicMap tm, AssociationType associationType) {
		super(topic, tm);
		this.cachedAssociationType = associationType;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof AssociationField))
      return false;
		
    AssociationField other = (AssociationField)obj;
    return (getTopicIF().equals(other.getTopicIF()));
  }

  /**
   * Gets the association type that is assigned to this association field.
   * 
   * @return the association type.
   */
  public AssociationType getAssociationType() {
    if (cachedAssociationType == null) {
      TopicMap tm = getTopicMap();
      TopicIF aType = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "has-association-type");
      TopicIF rType1 = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "association-field");
      TopicIF player1 = getTopicIF();
      TopicIF rType2 = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "association-type");
      Collection players = OntopolyModelUtils.findBinaryPlayers(tm, aType, player1, rType1, rType2);
      TopicIF associationType = (TopicIF)CollectionUtils.getFirst(players);
      this.cachedAssociationType = (associationType == null ? null : new AssociationType(associationType, getTopicMap()));      
      
//			String query = "select $AT from on:has-association-type(%AF% : on:association-field, $AT : on:association-type)?";
//			Map params = Collections.singletonMap("AF", getTopicIF());
//			TopicIF atype = (TopicIF)getTopicMap().getQueryWrapper().queryForObject(query, params);
//      if (atype == null) return null;
//			this.cachedAssociationType = new AssociationType(atype, getTopicMap());
		}
    return cachedAssociationType;
  }

//  /**
//   * Getter for the association field name.
//   */
//  public String getFieldName() {
//    Collection names = getTopicIF().getTopicNames();
//    Iterator it = names.iterator();
//    while (it.hasNext()) {
//      TopicNameIF name = (TopicNameIF) it.next();
//      if (name.getType() == null && name.getScope().isEmpty())
//        return name.getValue();
//    }		
//    AssociationType atype = getAssociationType();
//    return (atype == null ? null : atype.getName());
//  }

//  /**
//   * Creates a new role field and assigns it to this association field.
//   * @param includeSystemTopic if true then the association field will consider system topics as part of the available role types. 
//   * @return the new RoleField.
//   */
//  public RoleField createField(boolean includeSystemTopic) {
//
//    RoleType availableRoleType = getFirstAvailableRoleType(includeSystemTopic);
//
//		// make role-field topic
//    TopicMapBuilderIF builder = getTopicMap().getTopicMapIF().getBuilder();
//    TopicIF roleFieldType = OntopolyModelUtils.getTopicIF(getTopicMap(), PSI.ON, "role-field");
//    TopicIF roleFieldTopic = builder.makeTopic(roleFieldType);
//
//    //! // on:has-association-type($FD : on:role-field, $AT :  on:association-type)
//    //! TopicIF hasAssociationType = OntopolyModelUtils.getTopicIF(getTopicMap(), PSI.ON, "has-association-type");
//    //! TopicIF associationType = OntopolyModelUtils.getTopicIF(getTopicMap(), PSI.ON, "association-type");
//    //! OntopolyModelUtils.makeBinaryAssociation(hasAssociationType, 
//		//! 		getTopicIF(), associationType,
//    //!     roleFieldTopic, roleFieldType);
//
//    // on:has-association-field($RF : on:role-field, $AF :  on:association-field)
//    TopicIF hasAssociationField = OntopolyModelUtils.getTopicIF(getTopicMap(), PSI.ON, "has-association-field");
//    TopicIF associationFieldType = OntopolyModelUtils.getTopicIF(getTopicMap(), PSI.ON, "association-field");
//    OntopolyModelUtils.makeBinaryAssociation(hasAssociationField, 
//				getTopicIF(), associationFieldType,
//        roleFieldTopic, roleFieldType);
//
//    // on:has-role-type($FD : on:role-field, $RT : on:role-type)
//    TopicIF hasRoleType = OntopolyModelUtils.getTopicIF(getTopicMap(), PSI.ON, "has-role-type");
//    TopicIF roleType = OntopolyModelUtils.getTopicIF(getTopicMap(), PSI.ON, "role-type");
//    OntopolyModelUtils.makeBinaryAssociation(hasRoleType, 
//				availableRoleType.getTopicIF(), roleType,
//        roleFieldTopic, roleFieldType);
//
//    // on:use-interface-control($FD : on:field-definition, $IC : on:interface-control)
//    TopicIF fieldDefinitionTopic = OntopolyModelUtils.getTopicIF(getTopicMap(), PSI.ON, "field-definition");
//    TopicIF useControl = OntopolyModelUtils.getTopicIF(getTopicMap(), PSI.ON, "use-interface-control");
//    TopicIF interfaceControl = OntopolyModelUtils.getTopicIF(getTopicMap(), PSI.ON, "interface-control");
//    OntopolyModelUtils.makeBinaryAssociation(useControl, 
//				roleFieldTopic, fieldDefinitionTopic,
//        InterfaceControl.getDefaultInterfaceControl(getTopicMap()).getTopicIF(), interfaceControl);
//
//    // make role-field name
//		String name = availableRoleType.getName();
//		if (name != null)
//			OntopolyModelUtils.makeTopicName(null, roleFieldTopic, name, Collections.EMPTY_SET);
//
//    return new RoleField(roleFieldTopic, getTopicMap(), availableRoleType, this);
//  }
//
//  /**
//   * Returns the first RoleType in the list among available roleTypes.
//   */
//  private RoleType getFirstAvailableRoleType(boolean includeSystemTopic) {
//    List allRoleTypes = new ArrayList(getTopicMap().getRoleTypes(includeSystemTopic));
//    Iterator it = getFieldsForRoles().iterator();
//    while (it.hasNext()) {
//      allRoleTypes.remove(((RoleField) it.next()).getRoleType());
//    }
//    if (allRoleTypes.size() > 0)
//      return (RoleType) allRoleTypes.get(0);
//    else
//      // This should never happen
//      throw new OntopolyModelRuntimeException(
//          "Can't create field for role because no available role type exist.");
//  }

//  /**
//   * Removes the given role field from the association field. This will effectively delete the role field from the topic map.
//   * 
//   * @param The RoleField that is going to be removed from this association field.
//   */
//  public void removeField(RoleField rf) {
//		TopicIF roleFieldTopic = rf.getTopicIF();
//		roleFieldTopic.remove();
//  }

  /**
   * Returns the arity of the association field, i.e. the number of roles that
   * can be played.
   * 
   * @return integer representing the number of allowed roles.
   */
  public int getArity() {
		return getFieldsForRoles().size();
  }

  /**
   * Returns the fields for the roles in this association type.
   * 
   * @return List of RoleField objects
   */
  public List getFieldsForRoles() {
		if (cachedFieldsForRoles != null) return cachedFieldsForRoles;

    String query = "select $RF from "
			+ "on:has-association-field(%AF% : on:association-field, $RF : on:role-field)?";
    Map params = Collections.singletonMap("AF", getTopicIF());

    List roleFields = getTopicMap().getQueryWrapper().queryForList(query,
        new RowMapperIF() {
          public Object mapRow(QueryResultIF result, int rowno) {
						TopicIF roleFieldTopic = (TopicIF)result.getValue(0);
						return new RoleField(roleFieldTopic, getTopicMap());
					}
				}, params);

		if (roleFields.size() == 1 && getAssociationType().isSymmetric()) {
			// if association is symmetric we have to add the other field manually
			RoleField rfield = (RoleField)roleFields.get(0);
			roleFields.add(rfield);
		} else {
			Collections.sort(roleFields, RoleFieldComparator.getInstance());
		}
		this.cachedFieldsForRoles = roleFields;
    return roleFields;
  }

  @Override
	public void remove(LifeCycleListener listener) {
		// remove all associated role fields
		Iterator iter = getFieldsForRoles().iterator();
		while (iter.hasNext()) {
			RoleField rf = (RoleField)iter.next();
			rf.remove(listener);
		}
		// remove association type topic
		listener.onBeforeDelete(this);
		getTopicIF().remove();
	}

	/**
	 * Gets the role fields that are assigned to this association field.
	 * @return Collection of RoleField
	 */
	public Collection getDeclaredByFields() {
		return getFieldsForRoles();
	}

//	public Collection getUsedBy() {
//    String query = "select $TT from "
//			+ "on:has-association-field(%AF% : on:association-field, $RF : on:role-field), "
//			+ "on:has-field($RF : on:field-definition, $TT : on:field-owner)?";
//    Map params = Collections.singletonMap("AF", getTopicIF());
//
//    return getTopicMap().getQueryWrapper().queryForList(query,
//        new RowMapperIF() {
//          public Object mapRow(QueryResultIF result, int rowno) {
//						TopicIF topicType = (TopicIF)result.getValue(0);
//						return new TopicType(topicType, getTopicMap());
//					}
//				}, params);
//	}

  static class RoleFieldComparator implements Comparator {
    private static final RoleFieldComparator INSTANCE = new RoleFieldComparator();

    private RoleFieldComparator() {
      super();
    }

    public static RoleFieldComparator getInstance() {
      return INSTANCE;
    }

    public int compare(Object o1, Object o2) {
      RoleField rf1 = (RoleField) o1;
      RoleField rf2 = (RoleField) o2;

      return ObjectUtils.compare(rf1.getFieldName(), rf2.getFieldName());
    }
  }

}
