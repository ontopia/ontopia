package ontopoly.models;


import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.AssociationType;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.NameType;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.OccurrenceType;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.RoleType;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.Topic;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.TopicMap;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.TopicType;
import net.ontopia.utils.ObjectUtils;
import ontopoly.utils.OntopolyContext;

public class TopicModel extends MutableLoadableDetachableModel {

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
  
  public TopicModel(Topic topic) {
    super(topic);
    if (topic != null) {
      this.topicMapId = topic.getTopicMap().getId();
      this.topicId = topic.getId();
    }
  }

  public TopicModel(Topic topic, int returnType) {
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
    return (Topic)getObject();
  }
  
  @Override
  protected Object load() {
    if (topicMapId == null) return null;
    TopicMap tm = OntopolyContext.getTopicMap(topicMapId);
    TopicIF topicIf = tm.getTopicIFById(topicId);
    switch (returnType) {
      case TYPE_ASSOCIATION_TYPE:
        return new AssociationType(topicIf, tm);
      case TYPE_ROLE_TYPE:
        return new RoleType(topicIf, tm);
      case TYPE_OCCURRENCE_TYPE:
        return new OccurrenceType(topicIf, tm);
      case TYPE_NAME_TYPE:
        return new NameType(topicIf, tm);
      case TYPE_TOPIC_TYPE:
        return new TopicType(topicIf, tm);
      default:
        return new Topic(topicIf, tm);
    }
  }

  @Override
  public void setObject(Object object) {
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
