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

import com.touchgraph.graphlayout.TGException;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.utils.OntopiaRuntimeException;

public class CreateTMClassInstanceAssociation implements EdgeRecoveryObjectIF {
  private TopicIF instance;
  private TopicIF type;

  // Use with care and keep private. Only for temporary internal storage!
  private TMClassInstanceAssociation lastEdge;

  public CreateTMClassInstanceAssociation(TopicIF instance, TopicIF type) {
    this.instance = instance;
    this.type = type;
  }

  @Override
  public void execute(TopicMapView view) {
    lastEdge = view.getEdge(type, instance);
    if (lastEdge != null) {
      return;
    }
    
    TMTopicNode typeNode = view.assertNode(type, true);
    TMTopicNode instanceNode = view.assertNode(instance, true); 

    try {
      // The following lines were necessary to make the far topic visible.
      view.getTGPanel().addNode(typeNode);
      view.getTGPanel().addNode(instanceNode);
    } catch (TGException e) {
      throw new OntopiaRuntimeException(e);
    }
    
    view.makeTypeInstanceEdge(instanceNode, typeNode);
  }

  @Override
  public TMAbstractEdge recoverEdge(TopicMapView view) {
    execute(view);
    return lastEdge;
  }
}
