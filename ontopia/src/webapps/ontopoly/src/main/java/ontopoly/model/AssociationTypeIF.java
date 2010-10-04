
package ontopoly.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ontopoly.utils.OntopolyModelUtils;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.utils.RowMapperIF;
import net.ontopia.topicmaps.utils.ObjectIdComparator;
import net.ontopia.utils.ObjectUtils;

/**
 * Represents an association type.
 */
public interface AssociationTypeIF extends TypingTopicIF {

  /**
   * Indicates whether the association type is symmetric.
   */
  public boolean isSymmetric();

  /**
   * Tests whether this association type is hierarchical.
   * 
   * @return true if this is association type is hierarchical.
   */
  public boolean isHierarchical();

  /**
   * Returns all role types that have been declared for this association type.
   * @return list of role types
   */
  public List<RoleTypeIF> getDeclaredRoleTypes();

  /**
   * Returns a collection of lists that contain the role type
   * combinations that have been used in actual associations. The
   * RoleTypes are sorted by object id.
   * 
   * @return Collection<List<RoleTypeIF>>
   */
  public Collection<List<RoleTypeIF>> getUsedRoleTypeCombinations();
  
  /**
   * Transforms associations from the role types of the given form to
   * the new one as given.
   * @param roleTypesFrom list of role types that should match
   * existing associations
   * @param roleTypesTo list of role types to which the associations
   * should be changed
   */
  public void transformInstances(List roleTypesFrom, List roleTypesTo);

}
