
// $Id: AssociationRole.java,v 1.46 2008/06/02 10:50:12 geir.gronmo Exp $

package net.ontopia.topicmaps.impl.basic;

import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.ConstraintViolationException;
import net.ontopia.topicmaps.core.CrossTopicMapException;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.impl.utils.DeletionUtils;
import net.ontopia.topicmaps.impl.utils.ObjectStrings;

/**
 * INTERNAL: The basic association role implementation.
 */

public class AssociationRole extends TMObject implements AssociationRoleIF {

  static final long serialVersionUID = 8387889553134058046L;

  protected TopicIF reifier;
  protected TopicIF type;
  protected TopicIF player;

  protected AssociationRole(TopicMap tm) {
    super(tm);
  }

  // -----------------------------------------------------------------------------
  // AssociationRoleIF implementation
  // -----------------------------------------------------------------------------

  public AssociationIF getAssociation() {
    return (AssociationIF) parent;
  }

  /**
   * INTERNAL: Sets the association that the association role belongs to. [parent]
   */
  void setAssociation(Association parent) {
    // Validate topic map
    if (parent != null && parent.topicmap != this.topicmap)
        throw new ConstraintViolationException(
            "Cannot move objects across topic maps: " + this.topicmap + " and "
                + parent.topicmap);
    // Set parent
    this.parent = parent;
  }

  public TopicIF getPlayer() {
    return player;
  }

  public void setPlayer(TopicIF player) {
    if (player == null) throw new NullPointerException("Association role player must not be null.");
    CrossTopicMapException.check(player, this);
    // Notify listeners
    fireEvent("AssociationRoleIF.setPlayer", player, this.player);
    // Unregister association role with topic
    if (this.player != null && parent != null && parent.parent != null)
        ((Topic) this.player).removeRole(this);
    // Set property
    this.player = player;
    // Register association role with topic
    if (player != null && parent != null && parent.parent != null)
        ((Topic) this.player).addRole(this);
  }

  public void remove() {
    if (parent != null) {
      DeletionUtils.removeDependencies(this);
      ((Association)parent).removeRole(this);
    }
  }

  // -----------------------------------------------------------------------------
  // TypedIF implementation
  // -----------------------------------------------------------------------------

  public TopicIF getType() {
    return type;
  }

  public void setType(TopicIF type) {
    if (type == null) throw new NullPointerException("Association role type must not be null.");
    CrossTopicMapException.check(type, this);
    // Notify listeners
    fireEvent("AssociationRoleIF.setType", type, getType());
    this.type = type;
  }
  
  // -----------------------------------------------------------------------------
  // ReifiableIF implementation
  // -----------------------------------------------------------------------------

  public TopicIF getReifier() {
    return reifier;
  }
  
  public void setReifier(TopicIF _reifier) {
    if (_reifier != null) CrossTopicMapException.check(_reifier, this);
    // Notify listeners
    Topic reifier = (Topic)_reifier;
    Topic oldReifier = (Topic)getReifier();
    fireEvent("ReifiableIF.setReifier", reifier, oldReifier);
    this.reifier = reifier;
    if (oldReifier != null) oldReifier.setReified(null);
    if (reifier != null) reifier.setReified(this);
  }

  // -----------------------------------------------------------------------------
  // Misc. methods
  // -----------------------------------------------------------------------------

  protected void fireEvent(String event, Object new_value, Object old_value) {
    if (parent == null || parent.parent == null)
      return;
    else
      topicmap.processEvent(this, event, new_value, old_value);
  }

  public boolean isConnected() {
    return (parent != null && parent.isConnected());
  }

  public String toString() {
    return ObjectStrings.toString("basic.AssociationRole",
        (AssociationRoleIF) this);
  }

}
