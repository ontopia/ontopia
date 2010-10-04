package ontopoly.models;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.utils.ObjectUtils;
import ontopoly.OntopolyContext;
import ontopoly.model.AssociationTypeIF;
import ontopoly.model.NameTypeIF;
import ontopoly.model.OccurrenceTypeIF;
import ontopoly.model.RoleTypeIF;
import ontopoly.model.OntopolyTopicIF;
import ontopoly.model.OntopolyTopicMapIF;
import ontopoly.model.TopicTypeIF;

public class TopicModel<T extends OntopolyTopicIF> extends MutableLoadableDetachableModel<T> {

  private static final long serialVersionUID = -8374148020034895666L;
  
  private int returnType = OntopolyTopicMapIF.TYPE_TOPIC;
  
  private String topicMapId;
  private String topicId;
  
  public TopicModel(T topic) {
    super(topic);
    if (topic != null) {
      this.topicMapId = topic.getTopicMap().getId();
      this.topicId = topic.getId();
    }
  }

  public TopicModel(T topic, int returnType) {
    this(topic);
    this.returnType = returnType;
  }
  
  public TopicModel(String topicMapId, String topicId) {
    if (topicMapId == null)
      throw new NullPointerException("topicMapId parameter cannot be null.");
    if (topicId == null)
      throw new NullPointerException("topicId parameter cannot be null.");
    this.topicMapId = topicMapId;
    this.topicId = topicId;
  }
 
  public OntopolyTopicIF getTopic() {    
    return getObject();
  }
  
  @SuppressWarnings("unchecked")
  @Override
  protected T load() {
    if (topicMapId == null) return null;
    OntopolyTopicMapIF tm = OntopolyContext.getTopicMap(topicMapId);
    return (T) tm.findTypingTopic(topicId, returnType);
  }

  @Override
  public void setObject(T object) {
    super.setObject(object);
    if (object == null) {
      this.topicMapId = null;
      this.topicId = null;      
    } else {
      OntopolyTopicIF topic = (OntopolyTopicIF)object;
      this.topicMapId = topic.getTopicMap().getId();
      this.topicId = topic.getId();
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof TopicModel)
      return ObjectUtils.equals(topicMapId, ((TopicModel)obj).topicMapId) &&
        ObjectUtils.equals(topicId, ((TopicModel)obj).topicId);
    else
      return false;
  }

  @Override
  public int hashCode() {
    return topicMapId.hashCode() + topicId.hashCode();
  }
  
}
