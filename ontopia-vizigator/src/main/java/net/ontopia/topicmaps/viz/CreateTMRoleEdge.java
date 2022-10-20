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

import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;

import com.touchgraph.graphlayout.TGException;

public class CreateTMRoleEdge implements EdgeRecoveryObjectIF {
  private AssociationRoleIF role;

  // Use with care and keep private. Only for temporary internal storage!
  private TMRoleEdge lastEdge;

  public CreateTMRoleEdge(AssociationRoleIF role) {
    this.role = role;
  }
  
  @Override
  public void execute(TopicMapView view) {
    lastEdge = view.getEdge(role);
    if (lastEdge != null) {
      return;
    }
    
    AssociationIF association = role.getAssociation();
    TMAssociationNode assocNode = (TMAssociationNode)view
        .findObject(association, 
                    association.getType());
      
    if (assocNode == null) {
      assocNode = new TMAssociationNode(association, 
                                        view.currentScopingTopic,
                                        view);
      view.initializeAssociation(assocNode);
      view.addAssociation(assocNode);
    }
    
    lastEdge = view.makeRole(assocNode, role, true);

    while (view.newNodes.isEmpty() == false) {
      TMAbstractNode newNode = (TMAbstractNode) view.newNodes.remove(0);
      view.nodesUpdateCount.add(newNode);
      try {
        view.getTGPanel().addNode(newNode);
      } catch (TGException e) {
        view.lenientAddNode(newNode);
      }
    }
  }

  @Override
  public TMAbstractEdge recoverEdge(TopicMapView view) {
    execute(view);
    return lastEdge;
  }
}
