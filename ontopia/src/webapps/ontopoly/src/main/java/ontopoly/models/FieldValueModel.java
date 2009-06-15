package ontopoly.models;


import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.RoleField;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

public class FieldValueModel extends LoadableDetachableModel {

  private FieldInstanceModel fieldInstanceModel;
  private Object value;
  private boolean isExistingValue;
  
  private FieldValueModel(FieldInstanceModel fieldInstanceModel, Object value, boolean isExistingValue) {
    super(value);
    if (fieldInstanceModel == null)
      throw new NullPointerException("fieldInstanceModel parameter cannot be null.");
    this.fieldInstanceModel = fieldInstanceModel;

    setValueInternal(value);
    this.isExistingValue = isExistingValue;
  }
  
  private void setValueInternal(Object value) {
    // turn non-serializable objects into model objects
    if (value instanceof RoleField.ValueIF)
      this.value = new AssociationFieldValueModel((RoleField.ValueIF)value);
    else if (value instanceof TMObjectIF)
      this.value = new TMObjectModel(fieldInstanceModel.getFieldInstance().getInstance().getTopicMap().getId(), (TMObjectIF)value);
    else
      this.value = value;
  }    
  
  public static FieldValueModel createModel(FieldInstanceModel fieldInstanceModel, Object value, boolean isExistingValue) {
    return new FieldValueModel(fieldInstanceModel, value, isExistingValue);  
  }
  
  public FieldInstanceModel getFieldInstanceModel() {
    return fieldInstanceModel;
  }

  public Object getFieldValue() {
    return getObject();
  }
  
  public boolean isExistingValue() {
    return isExistingValue;
  }
  
  public void setExistingValue(Object value) {
    setValueInternal(value);
    this.isExistingValue = true;
    detach();
  }
  
  @Override
  public Object load() {
    if (value instanceof IModel)
      return ((IModel)value).getObject();
    else
      return value;
  }
  
}
