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

import net.ontopia.topicmaps.core.TopicIF;

/**
 * INTERNAL: Edge class used to represent "Class - Instance"
 * associations.
 */
public class TMClassInstanceAssociation extends TMAbstractEdge
    implements VizTMAssociationIF {

  private TopicIF type;
  private Key key;
  
  private TopicIF classTopic;
  private TopicIF instanceTopic;

  public static class Key {

    private TopicIF instance;
    private TopicIF type;

    public Key(TopicIF aType, TopicIF anInstance) {
      type = aType;
      instance = anInstance;
    }

    @Override
    public boolean equals(Object obj) {
      return type.equals(((Key) obj).getType())
          && instance.equals(((Key) obj).getInstance());
    }

    public TopicIF getInstance() {
      return this.instance;
    }

    public TopicIF getType() {
      return this.type;
    }
  }

  public TMClassInstanceAssociation(TMTopicNode classType, TMTopicNode instance,
      TopicIF topicType) {
    super(classType, instance);
    this.classTopic = classType.getTopic();
    this.instanceTopic = instance.getTopic();
    type = topicType;
    key = new Key(classType.getTopic(),
        instance.getTopic());
  }

  @Override
  protected void paintToolTip(Graphics g) {
    if (shouldDisplayRoleHoverHelp) {
      this.paintToolTipText(g, Messages.getString("Viz.Class"), getFromRolePosition());
      this.paintToolTipText(g, Messages.getString("Viz.Instance"), getToRolePosition());
    }

    this.paintTypeToolTip(g);
  }

  @Override
  public TopicIF getTopicMapType() {
    return type;
  }

  @Override
  protected String getMainHoverHelpText() {
    return Messages.getString("Viz.InstanceOf");
  }

  @Override
  public boolean represents(Object object) {
    return object.equals(key);
  }
  
  @Override
  public boolean isAssociation() {
      return true;
    }
  
  @Override
  public void setShouldDisplayScopedAssociationNames(boolean newValue) {
    // This type does not have scoped Association names
  }

  @Override
  public RecoveryObjectIF getDesctructor() {
    return new DeleteTMClassInstanceAssociation(instanceTopic, classTopic);
  }

  @Override
  public RecoveryObjectIF getRecreator() {
    return new CreateTMClassInstanceAssociation(instanceTopic, classTopic);
  }
}
