package ontopoly.models;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.utils.ObjectUtils;
import ontopoly.OntopolyContext;
import ontopoly.model.AssociationType;
import ontopoly.model.NameType;
import ontopoly.model.OccurrenceType;
import ontopoly.model.RoleType;
import ontopoly.model.Topic;
import ontopoly.model.TopicMap;
import ontopoly.model.TopicType;

public class TopicModel<T extends Topic> extends MutableLoadableDetachableModel<T> {

  private static final long serialVersionUID = -8374148020034895666L;

  public static final int TYPE_TOPIC = 1;
  public static final int TYPE_ASSOCIATION_TYPE = 2;
  public static final int TYPE_ROLE_TYPE = 4;
  public static final int TYPE_NAME_TYPE = 8;
  public static final int TYPE_OCCURRENCE_TYPE = 16;
  public static final int TYPE_TOPIC_TYPE = 32;
  
  private int returnType = TYPE_TOPIC;
  
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
 
  public Topic getTopic() {    
    return getObject();
  }
  
  @SuppressWarnings("unchecked")
  @Override
  protected T load() {
    if (topicMapId == null) return null;
    TopicMap tm = OntopolyContext.getTopicMap(topicMapId);
    TopicIF topicIf = tm.getTopicIFById(topicId);
    if (topicIf == null) return null;
    switch (returnType) {
      case TYPE_ASSOCIATION_TYPE:
        return (T)new AssociationType(topicIf, tm);
      case TYPE_ROLE_TYPE:
        return (T)new RoleType(topicIf, tm);
      case TYPE_OCCURRENCE_TYPE:
        return (T)new OccurrenceType(topicIf, tm);
      case TYPE_NAME_TYPE:
        return (T)new NameType(topicIf, tm);
      case TYPE_TOPIC_TYPE:
        return (T)new TopicType(topicIf, tm);
      default:
        return (T)new Topic(topicIf, tm);
    }
  }

  @Override
  public void setObject(T object) {
    super.setObject(object);
    if (object == null) {
      this.topicMapId = null;
      this.topicId = null;      
    } else {
      Topic topic = (Topic)object;
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
