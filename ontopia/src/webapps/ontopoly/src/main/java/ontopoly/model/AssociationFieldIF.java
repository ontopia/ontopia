
package ontopoly.model;

import java.util.Collection;
import java.util.List;

/**
 * Represents an association field.
 */
public interface AssociationFieldIF extends OntopolyTopicIF {

  /**
   * Gets the association type that is assigned to this association field.
   * 
   * @return the association type.
   */
  public AssociationTypeIF getAssociationType();

  /**
   * Returns the arity of the association field, i.e. the number of
   * roles that can be played.
   * 
   * @return integer representing the number of allowed roles.
   */
  public int getArity();

  /**
   * Returns the fields for the roles in this association type.
   * 
   * @return List of RoleField objects
   */
  public List<RoleFieldIF> getFieldsForRoles();
  
  /**
   * Gets the role fields that are assigned to this association field.
   * @return Collection of RoleField
   */
  public Collection<RoleFieldIF> getDeclaredByFields();
}
