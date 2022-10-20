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
import net.ontopia.topicmaps.core.TopicIF;

public class CreateTMAssociationEdge implements EdgeRecoveryObjectIF {
  private AssociationIF association;

  // Use with care and keep private. Only for temporary internal storage!
  private TMAssociationEdge lastEdge;

  public CreateTMAssociationEdge(AssociationIF association,
                                 TopicIF scopingTopic) {
    this.association = association;
  }

  @Override
  public void execute(TopicMapView view) {
    // One can rely on view.makeAssociation creating a TMAssociationEdge, since
    // the associationargument must necessarily be binary. Otherwise, an error
    // has been made in constructing this CreateTMassociationEdge.
    lastEdge =  view.getEdge(association);
    if (lastEdge == null) {
      lastEdge = (TMAssociationEdge)view
          .makeAssociation(association, null, true);  
    }  
  }

  @Override
  public TMAbstractEdge recoverEdge(TopicMapView view) {
    execute(view);
    return lastEdge;
  }
}
