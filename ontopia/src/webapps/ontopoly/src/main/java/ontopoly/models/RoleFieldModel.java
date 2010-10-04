package ontopoly.models;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.utils.ObjectUtils;
import ontopoly.OntopolyContext;
import ontopoly.model.RoleField;
import ontopoly.model.TopicMap;

import org.apache.wicket.model.LoadableDetachableModel;

public class RoleFieldModel extends LoadableDetachableModel<RoleField> {
  
  private String topicMapId;
  private String fieldId;
  
  public RoleFieldModel(RoleField roleField) {
    super(roleField);
    if (roleField == null)
      throw new NullPointerException("roleField parameter cannot be null.");
    topicMapId = roleField.getTopicMap().getId();
    fieldId = roleField.getId();
  }

  public RoleFieldModel(String topicMapId, String fieldId) {
    if (topicMapId == null)
      throw new NullPointerException("topicMapId parameter cannot be null.");
    if (fieldId == null)
      throw new NullPointerException("fieldId parameter cannot be null.");
    this.topicMapId = topicMapId;
    this.fieldId = fieldId;
  }

  public RoleField getRoleField() {
    return (RoleField)getObject();
  }
  
  @Override
  protected RoleField load() {
    TopicMap tm = OntopolyContext.getTopicMap(topicMapId);
    TopicIF fieldTopic = tm.getTopicIFById(fieldId);
    return new RoleField(fieldTopic, tm);    
  }

  public static List<RoleFieldModel> wrapInRoleFieldModels(Collection<RoleField> roleFields) {
    List<RoleFieldModel> result = new ArrayList<RoleFieldModel>(roleFields.size());
    Iterator iter = roleFields.iterator();
    while (iter.hasNext()) {
      RoleField roleField = (RoleField)iter.next();
      result.add(new RoleFieldModel(roleField));
    }
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof RoleFieldModel)
      return ObjectUtils.equals(topicMapId, ((RoleFieldModel)obj).topicMapId) &&
        ObjectUtils.equals(fieldId, ((RoleFieldModel)obj).fieldId);
    else
      return false;
  }

  @Override
  public int hashCode() {
    return topicMapId.hashCode() + fieldId.hashCode();
  }
  
}
