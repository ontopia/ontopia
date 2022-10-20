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

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.viz.TMClassInstanceAssociation.Key;

public class DeleteTMClassInstanceAssociation implements RecoveryObjectIF {
  private TopicIF type;
  private TopicIF instance;

  public DeleteTMClassInstanceAssociation(TopicIF instance,
                                          TopicIF type) {
    this.instance = instance;
    this.type = type;
  }

  @Override
  public void execute(TopicMapView view) {
    TMClassInstanceAssociation edge = null;
    Key key = new Key(type, instance);
    edge = (TMClassInstanceAssociation)view
        .findObject(key, view.configman.getTypeInstanceType());
    if (edge != null) {
      view.deleteEdge(edge);
    }
  }
}
