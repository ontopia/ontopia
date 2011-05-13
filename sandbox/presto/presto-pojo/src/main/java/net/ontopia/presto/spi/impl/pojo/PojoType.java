package net.ontopia.presto.spi.impl.pojo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import net.ontopia.presto.spi.PrestoField;
import net.ontopia.presto.spi.PrestoFieldUsage;
import net.ontopia.presto.spi.PrestoSchemaProvider;
import net.ontopia.presto.spi.PrestoType;
import net.ontopia.presto.spi.PrestoView;

public class PojoType implements PrestoType {

  private PrestoSchemaProvider schemaProvider;

  private String id;
  private String name;
  private boolean isReadOnly;
  private boolean isHidden;
  private boolean isCreatable = true;

  private Collection<PrestoType> directSubTypes = new HashSet<PrestoType>();

  private List<PrestoField> fields = new ArrayList<PrestoField>();
  private Map<String,PojoField> fieldsMap = new HashMap<String,PojoField>();

  private Collection<PrestoView> views = new ArrayList<PrestoView>();
  private Map<String,PrestoView> viewsMap = new HashMap<String,PrestoView>();

  PojoType(String id, PrestoSchemaProvider schemaProvider) {
    this.id = id;
    this.schemaProvider = schemaProvider;        
  }

  @Override
  public String toString() {
      return "PojoType[" + getId() + "|" + getName() + "]";
  }
  
  @Override
  public boolean equals(Object other) {
    if (other instanceof PojoType) {
      PojoType o = (PojoType)other;
      return id.equals(o.id);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public PrestoSchemaProvider getSchemaProvider() {
    return schemaProvider;
  }

  public boolean isReadOnly() {
    return isReadOnly;
  }

  public boolean isHidden() {
    return isHidden;
  }

  public boolean isCreatable() {
    return isCreatable && !isReadOnly;
  }
  
  public Collection<PrestoType> getDirectSubTypes() {
    return directSubTypes;
  }

  public List<PrestoField> getFields() {
    return fields;
  }

  public List<PrestoFieldUsage> getFields(PrestoView fieldsView) {
    List<PrestoFieldUsage> result = new ArrayList<PrestoFieldUsage>();
    for (PrestoField field : fields) {
      PojoField pojoField = (PojoField)field;
      if (pojoField.isInView(fieldsView)) {
        result.add(new PojoFieldUsage(pojoField, this, fieldsView));
      }
    }
    return result;
  }

  public PrestoField getFieldById(String fieldId) {
      PojoField field = fieldsMap.get(fieldId);
      if (field == null) {
          throw new RuntimeException("Field '" + fieldId + "' not found on type " + this.getId());
      }
      return field;
  }
  
  public PrestoFieldUsage getFieldById(String fieldId, PrestoView view) {
    PojoField field = fieldsMap.get(fieldId);
    if (field == null) {
      throw new RuntimeException("Field '" + fieldId + "' in view " + view.getId() + " not found on type " + this.getId());
    } else if (!field.isInView(view)) {
      throw new RuntimeException("Field '" + fieldId + "' in not defined in view " + view.getId() + " on type " + this.getId());
    }
    return new PojoFieldUsage(field, this, view);
  }

  public PrestoView getDefaultView() {
    return getViewById("info");
//    return views.iterator().next();
  }

  public PrestoView getViewById(String viewId) {
    PrestoView view = viewsMap.get(viewId);
    if (view == null) {
      throw new RuntimeException("View '" + viewId + "' not found on type " + this.getId());
    }
    return view;
  }

  public Collection<PrestoView> getViews(PrestoView fieldsView) {
    return views;
  }

  protected void setName(String name) {
    this.name = name;
  }

  protected void setReadOnly(boolean isReadOnly) {
    this.isReadOnly = isReadOnly;
  }

  protected void setHidden(boolean isHidden) {
    this.isHidden = isHidden;
  }

  protected void setCreatable(boolean isCreatable) {
    this.isCreatable = isCreatable;
  }

  protected void addDirectSubType(PrestoType type) {
    this.directSubTypes.add(type);

  }

  protected void addView(PojoView view) {
    this.viewsMap.put(view.getId(), view);
    this.views.add(view);
  }

  protected void addField(PojoField field) {
    this.fieldsMap.put(field.getId(), field);
    this.fields.add(field);
  }

}
