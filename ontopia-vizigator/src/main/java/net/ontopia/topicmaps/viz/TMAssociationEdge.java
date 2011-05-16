
// $Id: TMAssociationEdge.java,v 1.16 2007/05/02 15:01:56 eirik.opland Exp $

package net.ontopia.topicmaps.viz;

import java.awt.Graphics;
import java.util.Iterator;

import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;

/**
 * INTERNAL: Edge class representing binary associations as edges.
 */
public class TMAssociationEdge extends TMAbstractEdge
    implements VizTMAssociationIF {
  private AssociationIF association;
  private boolean shouldDisplayScopedAssociationNames;

  public TMAssociationEdge(TMTopicNode tn1, TMTopicNode tn2,
                           AssociationIF association, TopicIF aScopingTopic) {
    super(tn1, tn2);
    this.association = association;
    setScopingTopic(aScopingTopic);
    setID(association.getObjectId());
  }

  public AssociationIF getAssociation() {
    return association;
  }

  protected String getMainHoverHelpText() {
    return TMAssociationNode.getAssociationText(association,
        shouldDisplayScopedAssociationNames, scopingTopic);
  }

  private void paintRolesToolTips(Graphics g) {
    if (!shouldDisplayRoleHoverHelp)
      return;

    Iterator iterator = association.getRoles().iterator();
    while (iterator.hasNext()) {
      AssociationRoleIF element = (AssociationRoleIF) iterator.next();
      if (element.getPlayer().equals(((TMTopicNode) this.from).getTopic()))
        this.paintToolTipText(g, this.getStringifier().toString(
            element.getType()), getFromRolePosition());
      if (element.getPlayer().equals(((TMTopicNode) this.to).getTopic()))
        this.paintToolTipText(g, this.getStringifier().toString(
            element.getType()), getToRolePosition());
    }

  }

  protected void paintToolTip(Graphics g) {
    // Make the Type tool tip paint last.
    paintRolesToolTips(g);
    paintTypeToolTip(g);
  }

  public TopicIF getTopicMapType() {
    return association.getType();
  }

  public boolean represents(Object object) {
    return association.equals(object);
  }

  public boolean isAssociation() {
    return true;
  }

  public void setShouldDisplayScopedAssociationNames(boolean newValue) {
    shouldDisplayScopedAssociationNames = newValue;
  }

  public RecoveryObjectIF getRecoveryObject() {
    return new CreateTMAssociationEdge(association, scopingTopic);
  }

  public RecoveryObjectIF getDesctructor() {
    return new DeleteTMAssociationEdge(association);
  }

  public RecoveryObjectIF getRecreator() {
    return new CreateTMAssociationEdge(association, scopingTopic);
  }
}
