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

public class DeleteTMAssociationEdge implements RecoveryObjectIF {
  private AssociationIF association;

  public DeleteTMAssociationEdge(AssociationIF association) {
    this.association = association;
  }

  @Override
  public void execute(TopicMapView view) {
    TMAssociationEdge edge = view.getEdge(association);
    if (edge != null) {
      view.getTGPanel().deleteEdge(edge);
      view.deleteEdge(edge);
    }
  }
}
