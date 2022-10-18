/*
 * #!
 * Ontopia Vizigator
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

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

  @Override
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
        this.paintToolTipText(g, this.getStringifier().apply(
            element.getType()), getFromRolePosition());
      if (element.getPlayer().equals(((TMTopicNode) this.to).getTopic()))
        this.paintToolTipText(g, this.getStringifier().apply(
            element.getType()), getToRolePosition());
    }

  }

  @Override
  protected void paintToolTip(Graphics g) {
    // Make the Type tool tip paint last.
    paintRolesToolTips(g);
    paintTypeToolTip(g);
  }

  @Override
  public TopicIF getTopicMapType() {
    return association.getType();
  }

  @Override
  public boolean represents(Object object) {
    return association.equals(object);
  }

  @Override
  public boolean isAssociation() {
    return true;
  }

  @Override
  public void setShouldDisplayScopedAssociationNames(boolean newValue) {
    shouldDisplayScopedAssociationNames = newValue;
  }

  public RecoveryObjectIF getRecoveryObject() {
    return new CreateTMAssociationEdge(association, scopingTopic);
  }

  @Override
  public RecoveryObjectIF getDesctructor() {
    return new DeleteTMAssociationEdge(association);
  }

  @Override
  public RecoveryObjectIF getRecreator() {
    return new CreateTMAssociationEdge(association, scopingTopic);
  }
}
