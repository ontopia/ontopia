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

public class DoHideEdge implements RecoveryObjectIF {
  private VizController controller;
  private EdgeRecoveryObjectIF recreator;

  public DoHideEdge(VizController controller, EdgeRecoveryObjectIF recreator) {
    this.controller = controller;
    this.recreator = recreator;
  }

  @Override
  public void execute(TopicMapView view) {
    TMAbstractEdge node = recreator.recoverEdge(view);
    controller.hideEdge(node);
  }
}
