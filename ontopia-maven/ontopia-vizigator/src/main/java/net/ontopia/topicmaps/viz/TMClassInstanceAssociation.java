//$Id: TMClassInstanceAssociation.java,v 1.13 2007/05/02 15:33:22 eirik.opland Exp $

package net.ontopia.topicmaps.viz;

import java.awt.Graphics;

import net.ontopia.topicmaps.core.TopicIF;

/**
 * INTERNAL: Edge class used to represent "Class - Instance"
 * associations.
 */
public class TMClassInstanceAssociation extends TMAbstractEdge
    implements VizTMAssociationIF {
  public static class Key extends Object {

    private TopicIF instance;
    private TopicIF type;

    public Key(TopicIF aType, TopicIF anInstance) {
      type = aType;
      instance = anInstance;
    }

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


  private TopicIF type;
  private Key key;
  
  private TopicIF classTopic;
  private TopicIF instanceTopic;

  public TMClassInstanceAssociation(TMTopicNode classType, TMTopicNode instance,
      TopicIF topicType) {
    super(classType, instance);
    this.classTopic = classType.getTopic();
    this.instanceTopic = instance.getTopic();
    type = topicType;
    key = new Key(((TMTopicNode) classType).getTopic(),
        ((TMTopicNode) instance).getTopic());
  }

  protected void paintToolTip(Graphics g) {
    if (shouldDisplayRoleHoverHelp) {
      this.paintToolTipText(g, Messages.getString("Viz.Class"), getFromRolePosition());
      this.paintToolTipText(g, Messages.getString("Viz.Instance"), getToRolePosition());
    }

    this.paintTypeToolTip(g);
  }

  public TopicIF getTopicMapType() {
    return type;
  }

  protected String getMainHoverHelpText() {
    return Messages.getString("Viz.InstanceOf");
  }

  public boolean represents(Object object) {
    return object.equals(key);
  }
  
  public boolean isAssociation() {
      return true;
    }
  
  public void setShouldDisplayScopedAssociationNames(boolean newValue) {
    // This type does not have scoped Association names
  }

  public RecoveryObjectIF getDesctructor() {
    return new DeleteTMClassInstanceAssociation(instanceTopic, classTopic);
  }

  public RecoveryObjectIF getRecreator() {
    return new CreateTMClassInstanceAssociation(instanceTopic, classTopic);
  }
}
