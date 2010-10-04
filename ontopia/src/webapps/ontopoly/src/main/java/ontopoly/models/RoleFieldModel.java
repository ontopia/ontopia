package ontopoly.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.utils.ObjectUtils;
import ontopoly.OntopolyContext;
import ontopoly.model.RoleFieldIF;
import ontopoly.model.OntopolyTopicMapIF;

import org.apache.wicket.model.LoadableDetachableModel;

public class RoleFieldModel extends LoadableDetachableModel<RoleFieldIF> { 
  private String topicMapId;
  private String fieldId;
  
  public RoleFieldModel(RoleFieldIF roleField) {
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

  public RoleFieldIF getRoleField() {
    return (RoleFieldIF)getObject();
  }
  
  @Override
  protected RoleFieldIF load() {
    OntopolyTopicMapIF tm = OntopolyContext.getTopicMap(topicMapId);
    return tm.findRoleField(fieldId);
  }

  public static List<RoleFieldModel> wrapInRoleFieldModels(Collection<RoleFieldIF> roleFields) {
    List<RoleFieldModel> result = new ArrayList<RoleFieldModel>(roleFields.size());
    Iterator iter = roleFields.iterator();
    while (iter.hasNext()) {
      RoleFieldIF roleField = (RoleFieldIF) iter.next();
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
