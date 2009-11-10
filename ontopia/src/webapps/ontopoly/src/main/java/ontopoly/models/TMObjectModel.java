package ontopoly.models;


import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.TopicMap;
import net.ontopia.utils.ObjectUtils;
import ontopoly.utils.OntopolyContext;

import org.apache.wicket.model.LoadableDetachableModel;

public class TMObjectModel extends LoadableDetachableModel<TMObjectIF> {

  private static final long serialVersionUID = -8374148020034895666L;

  private String topicMapId;

  private String objectId;

  public TMObjectModel(String topicMapId, TMObjectIF object) {
    super(object);
    if (object != null) {
      this.topicMapId = topicMapId;
      this.objectId = object.getObjectId();
    }
  }

  public TMObjectModel(String topicMapId, String objectId) {
    if (topicMapId == null)
      throw new NullPointerException("topicMapId parameter cannot be null.");
    if (objectId == null)
      throw new NullPointerException("objectId parameter cannot be null.");
    this.topicMapId = topicMapId;
    this.objectId = objectId;
  }
  
  public TMObjectIF getTMObject() {
    return (TMObjectIF)getObject();
  }
  
  @Override
  protected TMObjectIF load() {
    if (topicMapId == null) return null;
    TopicMap tm = OntopolyContext.getTopicMap(topicMapId);
    // FIXME: should probably complain if object not found
    return (TMObjectIF) tm.getTopicMapIF().getObjectById(objectId);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof TMObjectModel)
      return ObjectUtils.equals(topicMapId, ((TMObjectModel)obj).topicMapId) &&
        ObjectUtils.equals(objectId, ((TMObjectModel)obj).objectId);
    else
      return false;
  }

  @Override
  public int hashCode() {
    return topicMapId.hashCode() + objectId.hashCode();
  }
  
}
