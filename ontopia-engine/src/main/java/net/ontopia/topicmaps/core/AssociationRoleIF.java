
package net.ontopia.topicmaps.core;


/**
 * PUBLIC: Implemented by objects representing association roles in
 * the topic map model.  Association roles are first-class objects
 * which represent a topic playing a role in an association.</p>
 *
 * Note: in XTM 1.0 terminology, associations have members playing
 * roles in the association. In ISO 13250 terminology, playing a role
 * in an association is a characteristic of a topic. These notions are
 * intended to be equivalent.</p>
 */

public interface AssociationRoleIF extends TypedIF, ReifiableIF {

  /**
   * PUBLIC: Gets the association to which this association role
   * belongs.
   *
   * @return The association to which this association role belongs;
   *           an object implementing AssociationIF.
   */
  public AssociationIF getAssociation();

  /**
   * PUBLIC: Gets the topic that plays this association role (this member of the association).
   *
   * @return The topic (member) which plays this role in the association.
   *
  */
  public TopicIF getPlayer();

  /**
   * PUBLIC: Sets the topic that plays this association role. Note
   * that this has the side-effect of removing the role from its
   * current player, if any, and inserting it on the new player, if
   * any.
   *
   * @param player The topic (member) which plays this role in the association.
   *            Can be null; if null, then the effect is that there is no player
   *            of this role in this association.
   */
  public void setPlayer(TopicIF player);

}
