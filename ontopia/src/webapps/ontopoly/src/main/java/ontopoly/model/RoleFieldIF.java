
package ontopoly.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ontopoly.utils.OntopolyModelUtils;
import ontopoly.utils.Ordering;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.DataTypes;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.utils.CollectionUtils;
import net.ontopia.utils.ObjectUtils;

/**
 * Represents an association field, which is a combination of an association
 * type and a role type. Association types are not fields, since they cannot be
 * assigned to a topic type as a field without a role type.
 */
public interface RoleFieldIF extends FieldDefinitionIF {

  public boolean isSortable();

  public EditModeIF getEditMode();

  public CreateActionIF getCreateAction();

  /**
   * Gets the association type.
   * 
   * @Return the association type.
   */
  public AssociationTypeIF getAssociationType();

  /**
   * Gets the role type.
   * 
   * @Return the role type.
   */
  public RoleTypeIF getRoleType();

  public AssociationFieldIF getAssociationField();

  /**
   * Gets the other RoleField objects this object's association type
   * topic takes part in.
   * 
   * @returns the other RoleField objects this object's association
   * type topic takes part in.
   */
  public Collection<RoleFieldIF> getFieldsForOtherRoles();

  /**
   * Gets the interface control assigned for this association
   * field. If no interface control object is assigned, the method
   * will return the default interface control, which is
   * drop-down-list.
   * 
   * @return the interface control assigned to this association field. 
   */
  public InterfaceControlIF getInterfaceControl();

  /**
   * Gets the topic types that have been declared as valid and which
   * may play the other roles in this association type.
   * 
   * @return the topic types which may play the other roles in this
   * association type.
   */
  public Collection<TopicTypeIF> getDeclaredPlayerTypes();

  public Collection<TopicTypeIF> getAllowedPlayerTypes(OntopolyTopicIF currentTopic);

  public Collection<OntopolyTopicIF> getAllowedPlayers(OntopolyTopicIF currentTopic);

  /**
   * Search for the topics that match the given search term. Only
   * topics of allowed player types are returned.
   * 
   * @param searchTerm the search term used to search for topics.
   * @return a collection of Topic objects
   */
  public List<OntopolyTopicIF> searchAllowedPlayers(String searchTerm);
  
  public List getOrderedValues(OntopolyTopicIF topic, RoleFieldIF ofield); 

  public TopicIF[] getRoleTypes(ValueIF value);

  /**
   * Change field value order so that the first value is ordered
   * directly after the second value.
   */
  public void moveAfter(OntopolyTopicIF instance, RoleFieldIF ofield,
                        RoleFieldIF.ValueIF rfv1, RoleFieldIF.ValueIF rfv2);

  /**
   * Factory method for creating a ValueIF object, which represent an
   * instance topic on one side of an association.
   * 
   * @param arity the number of players that the association value
   * should have.
   * @return the ValueIF object that represent an instance topic on
   * one side of an association.
   */
  public ValueIF createValue(int arity);

  /**
   * Interface. This interface is implemented by the Value class.
   */
  public static interface ValueIF {

    public int getArity();

    public RoleFieldIF[] getRoleFields();

    public OntopolyTopicIF[] getPlayers();

    public void addPlayer(RoleFieldIF roleField, OntopolyTopicIF player);

    public OntopolyTopicIF getPlayer(RoleFieldIF roleField,
                                     OntopolyTopicIF oplayer);

  }
  
}
