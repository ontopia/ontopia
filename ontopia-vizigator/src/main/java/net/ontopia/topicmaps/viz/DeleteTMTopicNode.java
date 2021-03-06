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

public class DeleteTMTopicNode implements RecoveryObjectIF {
  private TopicIF topic;

  public DeleteTMTopicNode(TopicIF topic) {
    this.topic = topic;
  }

  @Override
  public void execute(TopicMapView view) {
    TMTopicNode node = view.assertNode(topic, false);
    if (node != null) {
      view.getTGPanel().deleteNode(node);
      view.deleteNode(node);
    }
  }

}
