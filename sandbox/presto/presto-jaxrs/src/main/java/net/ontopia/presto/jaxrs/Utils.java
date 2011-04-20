package net.ontopia.presto.jaxrs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriInfo;

import net.ontopia.presto.jaxb.FieldData;
import net.ontopia.presto.jaxb.Link;
import net.ontopia.presto.jaxb.Origin;
import net.ontopia.presto.jaxb.Topic;
import net.ontopia.presto.jaxb.TopicType;
import net.ontopia.presto.jaxb.Value;
import net.ontopia.presto.jaxb.View;
import net.ontopia.presto.spi.PrestoChangeSet;
import net.ontopia.presto.spi.PrestoDataProvider;
import net.ontopia.presto.spi.PrestoField;
import net.ontopia.presto.spi.PrestoFieldUsage;
import net.ontopia.presto.spi.PrestoSchemaProvider;
import net.ontopia.presto.spi.PrestoSession;
import net.ontopia.presto.spi.PrestoTopic;
import net.ontopia.presto.spi.PrestoType;
import net.ontopia.presto.spi.PrestoView;

import org.codehaus.jettison.json.JSONException;

public class Utils {

  public static Map<String,Object> getTopicData(UriInfo uriInfo, PrestoTopic topic, PrestoType type) {
    Map<String,Object> result = new LinkedHashMap<String,Object>();

    result.put("_id", topic.getId());
    result.put(":name", topic.getName());
    result.put(":type", type.getId());

    for (PrestoField field : type.getFields()) {
      List<Object> values = getValueData(uriInfo, field, topic.getValues(field));
      if (!values.isEmpty()) {
        result.put(field.getId(), values);
      }
    }
    return result;
  }

  protected static List<Object> getValueData(UriInfo uriInfo, PrestoField field, Collection<? extends Object> fieldValues) {
    List<Object> result = new ArrayList<Object>(fieldValues.size());
    for (Object fieldValue : fieldValues) {
      if (fieldValue instanceof PrestoTopic) {
        PrestoTopic valueTopic = (PrestoTopic)fieldValue;
        result.add(valueTopic.getId());
      } else {
        result.add(fieldValue);
      }
    }
    return result;
  }

  public static Topic getTopicInfo(UriInfo uriInfo, PrestoTopic topic, PrestoType type, PrestoView view, boolean readOnlyMode) {
    Topic result = new Topic();

    result.setId(topic.getId());
    result.setName(topic.getName());
    if (readOnlyMode) {
      result.setReadOnlyMode(readOnlyMode);
    }

    TopicType typeInfo = getTypeInfo(uriInfo, type);    

    boolean readOnly = readOnlyMode || type.isReadOnly();
    typeInfo.setReadOnly(readOnly);

    List<Link> typeLinks = new ArrayList<Link>();
    if (!readOnlyMode && !type.isAbstract() && !readOnly) {
      typeLinks.add(new Link("create-instance", Links.getCreateInstanceLinkFor(uriInfo, type)));
    }
    typeInfo.setLinks(typeLinks);

    result.setType(typeInfo);

    result.setView(view.getId());

    List<Link> topicLinks = new ArrayList<Link>();
    topicLinks.add(new Link("edit", Links.getEditLinkFor(uriInfo, topic, view)));    
    //    topicLinks.add(new Link("remove", "http://examples.org/topics/" + topic.getId() + "/remove"));
    result.setLinks(topicLinks);

    List<FieldData> fields = new ArrayList<FieldData>(); 

    for (PrestoFieldUsage field : type.getFields(view)) {
      fields.add(getFieldInfo(uriInfo, topic, field, topic.getValues(field), readOnlyMode));
    }
    result.setFields(fields);
    result.setViews(getViews(uriInfo, topic, type, view, readOnlyMode));
    return result;
  }

  public static Topic getNewTopicInfo(UriInfo uriInfo, PrestoType topicType, PrestoView fieldsView) {
    return getNewTopicInfo(uriInfo, topicType, fieldsView, null, null);
  }

  public static Topic getNewTopicInfo(UriInfo uriInfo, PrestoType topicType, PrestoView fieldsView, String parentId, String parentFieldId) {
    Topic result = new Topic();

    final boolean readOnlyMode = false;
    if (parentId != null) {
      result.setOrigin(new Origin(parentId, parentFieldId));
    }
    result.setType(new TopicType(topicType.getId(), topicType.getName()));

    result.setView(fieldsView.getId());

    List<Link> topicLinks = new ArrayList<Link>();
    topicLinks.add(new Link("create", Links.getCreateLinkFor(uriInfo, topicType, fieldsView)));    
    result.setLinks(topicLinks);

    List<FieldData> fields = new ArrayList<FieldData>(); 

    PrestoTopic topic = null;
    for (PrestoFieldUsage field : topicType.getFields(fieldsView)) {
      fields.add(getFieldInfo(uriInfo, topic, field, Collections.emptyList(), readOnlyMode));
    }
    result.setFields(fields);
    result.setViews(Collections.singleton(getView(uriInfo, null, fieldsView, readOnlyMode)));
    return result;
  }

  private static FieldData getFieldInfo(UriInfo uriInfo,
      PrestoTopic topic, PrestoFieldUsage field, Collection<? extends Object> fieldValues, boolean readOnlyMode) {

    PrestoType topicType = field.getType();
    PrestoView parentView = field.getView();

    boolean isNewTopic = topic == null;

    String databaseId = field.getSchemaProvider().getDatabaseId();
    String topicId = isNewTopic ? "_" + topicType.getId() : topic.getId();
    String parentViewId = parentView.getId();
    String fieldId = field.getId();

    String fieldReference = databaseId + "/" + topicId + "/" + parentViewId + "/" + fieldId;

    FieldData fieldInfo = new FieldData();

    fieldInfo.setId(fieldId);
    fieldInfo.setName(field.getName());

    int minCard = field.getMinCardinality();
    if (minCard > 0) {
      fieldInfo.setMinCardinality(minCard);
    }

    int maxCard = field.getMaxCardinality();
    if (maxCard > 0) {
      fieldInfo.setMaxCardinality(maxCard);
    }

    String validationType = field.getValidationType();
    if (validationType != null) {
      fieldInfo.setValidation(validationType);
    }

    String interfaceControl = field.getInterfaceControl(); // ISSUE: should we default the interface control?
    if (interfaceControl != null) {
      fieldInfo.setInterfaceControl(interfaceControl);          
    }

    String externalType = field.getExternalType();
    if (externalType != null) {
      fieldInfo.setExternalType(externalType);
    }

    if (field.isEmbedded()) {
      fieldInfo.setEmbeddable(true);
    }

    if (field.isPrimitiveField()) {
      String dataType = field.getDataType();
      if (dataType != null) {
        fieldInfo.setDatatype(dataType);
      }
      if (readOnlyMode || field.isReadOnly()) {
        fieldInfo.setReadOnly(Boolean.TRUE);
        fieldInfo.setLinks(Collections.EMPTY_LIST);
      } else {
        List<Link> fieldLinks = new ArrayList<Link>();
        if (!isNewTopic) {
          fieldLinks.add(new Link("add-field-values", uriInfo.getBaseUri() + "editor/add-field-values/" + fieldReference));
          fieldLinks.add(new Link("remove-field-values", uriInfo.getBaseUri() + "editor/remove-field-values/" + fieldReference));
        }
        fieldInfo.setLinks(fieldLinks);
      }

    } else if (field.isReferenceField()) {
      // fieldInfo.put("type", field.getFieldType());
      fieldInfo.setDatatype("reference");

      boolean allowEdit = !field.isReadOnly();
      boolean allowAddRemove = allowEdit && !field.isNewValuesOnly();
      boolean allowCreate = allowEdit && !field.isExistingValuesOnly();
      if (readOnlyMode || !allowEdit) {
        fieldInfo.setReadOnly(Boolean.TRUE);
      }

      List<Link> fieldLinks = new ArrayList<Link>();      
      if (allowCreate && !isNewTopic) {
        fieldLinks.add(new Link("available-field-types", uriInfo.getBaseUri() + "editor/available-field-types/" + fieldReference));
      }
      if (allowAddRemove) {
        // ISSUE: should add-values and remove-values be links on list result instead?
        fieldLinks.add(new Link("available-field-values", uriInfo.getBaseUri() + "editor/available-field-values/" + fieldReference));
        if (!isNewTopic) {
          fieldLinks.add(new Link("add-field-values", uriInfo.getBaseUri() + "editor/add-field-values/" + fieldReference));
          fieldLinks.add(new Link("remove-field-values", uriInfo.getBaseUri() + "editor/remove-field-values/" + fieldReference));
        }
      }
      fieldInfo.setLinks(fieldLinks);

    } else {
      // used by query fields, which can have both primitive and reference values
      // fieldInfo.put("type", field.getFieldType());
      fieldInfo.setDatatype("query");
      if (readOnlyMode || field.isReadOnly()) {
        fieldInfo.setReadOnly(Boolean.TRUE);
      }
      fieldInfo.setLinks(Collections.EMPTY_LIST);
    }

    Collection<PrestoType> availableFieldValueTypes = field.getAvailableFieldValueTypes();
    if (!availableFieldValueTypes.isEmpty()) {
      List<TopicType> valueTypes = new ArrayList<TopicType>(availableFieldValueTypes.size());
      for (PrestoType playerType : availableFieldValueTypes) {
        valueTypes.add(Utils.getTypeInfo(uriInfo, playerType));
      }
      fieldInfo.setValueTypes(valueTypes);
    }

    fieldInfo.setValues(getValues(uriInfo, field, fieldValues, readOnlyMode));
    return fieldInfo;
  }

  public static List<View> getViews(UriInfo uriInfo,
      PrestoTopic topic, PrestoType topicType, PrestoView fieldsView, boolean readOnlyMode) {

    Collection<PrestoView> fieldViews = topicType.getViews(fieldsView);

    List<View> views = new ArrayList<View>(fieldViews.size()); 
    for (PrestoView _fieldsView : fieldViews) {
      views.add(getView(uriInfo, topic, _fieldsView, readOnlyMode));
    }
    return views;
  }

  public static View getView(UriInfo uriInfo, PrestoTopic topic,
      PrestoView _fieldsView, boolean readOnlyMode) {
    View view = new View();
    view.setId(_fieldsView.getId());
    view.setName(_fieldsView.getName());

    List<Link> links = new ArrayList<Link>();
    if (topic != null) {
      links.add(new Link("edit-in-view", Links.getEditLinkFor(uriInfo, topic, _fieldsView, readOnlyMode)));
    }
    view.setLinks(links);
    return view;
  }

  protected static Collection<Value> getValues(UriInfo uriInfo, PrestoFieldUsage field, Collection<? extends Object> fieldValues, boolean readOnlyMode) {
    List<Value> result = new ArrayList<Value>(fieldValues.size());
    for (Object value : fieldValues) {
      result.add(getValue(uriInfo, field, value, readOnlyMode));
    }
    Collections.sort(result, new Comparator<Value>() {
      public int compare(Value v1, Value v2) {
        String vx1 = v1.getName();
        if (vx1 == null) {
          vx1 = v1.getValue();
        }
        String vx2 = v2.getName();
        if (vx2 == null) {
          vx2 = v2.getValue();
        }
        return compareStatic(vx1, vx2);
      }
    });
    return result;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  protected static <T> int compareStatic(Comparable o1, Comparable o2) {
    if (o1 == null)
      return (o2 == null ? 0 : -1);
    else if (o2 == null)
      return 1;
    else
      return o1.compareTo(o2);
  }

  protected static Value getValue(UriInfo uriInfo, PrestoFieldUsage field, Object fieldValue, boolean readOnlyMode) {
    if (fieldValue instanceof PrestoTopic) {
      PrestoTopic valueTopic = (PrestoTopic)fieldValue;
      return getExistingTopicFieldValue(uriInfo, field, valueTopic, readOnlyMode);
    } else {
      Value result = new Value();
      result.setValue(fieldValue.toString());
      boolean removable = !field.isReadOnly();
      if (!readOnlyMode && removable) {
        result.setRemovable(Boolean.TRUE);
      }
      return result;
    }
  }

  public static Value getExistingTopicFieldValue(UriInfo uriInfo,
      PrestoFieldUsage field, PrestoTopic value, boolean readOnlyMode) {

    Value result = new Value();
    result.setValue(value.getId());
    result.setName(value.getName());
    if (field.isEmbedded()) {
      PrestoType valueType = field.getSchemaProvider().getTypeById(value.getTypeId());
      result.setEmbedded(getTopicInfo(uriInfo, value, valueType, field.getValueView(), readOnlyMode));
    }

    if (!readOnlyMode && !field.isReadOnly()) {
      result.setRemovable(Boolean.TRUE);
    }

    List<Link> links = new ArrayList<Link>();
    if (field.isTraversable()) {
      links.add(new Link("edit", Links.getEditLinkFor(uriInfo, value, field.getValueView(), readOnlyMode)));
    }
    result.setLinks(links);

    return result;
  }

  public static Value getAllowedTopicFieldValue(UriInfo uriInfo, 
      PrestoTopic value, PrestoView childView, boolean traversable) {

    Value result = new Value();
    result.setValue(value.getId());
    result.setName(value.getName());

    List<Link> links = new ArrayList<Link>();
    if (traversable) {
      links.add(new Link("edit", Links.getEditLinkFor(uriInfo, value, childView)));
    }
    result.setLinks(links);

    return result;
  }

  protected static TopicType getTypeInfo(UriInfo uriInfo, PrestoType type) {
    return new TopicType(type.getId(), type.getName());
  }

  public static TopicType getCreateFieldInstance(UriInfo uriInfo, PrestoTopic topic, PrestoFieldUsage field, PrestoType type) {

    TopicType result = getTypeInfo(uriInfo, type);

    List<Link> links = new ArrayList<Link>();
    links.add(new Link("create-field-instance", uriInfo.getBaseUri() + "editor/create-field-instance/" + field.getSchemaProvider().getDatabaseId() + "/" + topic.getId() + "/" + field.getId() + "/" + type.getId()));
    result.setLinks(links);

    return result;
  }

  public static FieldData addFieldValues(UriInfo uriInfo, PrestoSession session, PrestoTopic topic, PrestoFieldUsage field, FieldData fieldObject) {
    try {

      PrestoDataProvider dataProvider = session.getDataProvider();

      if  (field != null) {

        PrestoChangeSet changeSet = dataProvider.updateTopic(topic);        
        boolean isReferenceField = field.isReferenceField();        
        Collection<Object> addableValues = new HashSet<Object>();

        for (Value value : fieldObject.getValues()) {

          if (isReferenceField) {
            String valueId = getReferenceValue(value);
            PrestoTopic valueTopic = dataProvider.getTopicById(valueId);
            addableValues.add(valueTopic);
          } else {
            addableValues.add(getPrimitiveValue(value));
          }
        }
        changeSet.addValues(field, addableValues);
        changeSet.save();
      }
      return getFieldInfo(uriInfo, topic, field, topic.getValues(field), false);
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
  }

  public static FieldData removeFieldValues(UriInfo uriInfo, PrestoSession session, PrestoTopic topic, PrestoFieldUsage field, FieldData fieldObject) {
    try {

      PrestoDataProvider dataProvider = session.getDataProvider();

      if  (field != null) {

        PrestoChangeSet changeSet = dataProvider.updateTopic(topic);
        boolean isReferenceField = field.isReferenceField();
        Collection<Object> removeableValues = new HashSet<Object>();

        for (Value value : fieldObject.getValues()) {

          if (isReferenceField) {
            String valueId = getReferenceValue(value);
            PrestoTopic valueTopic = dataProvider.getTopicById(valueId);
            removeableValues.add(valueTopic);
          } else {
            removeableValues.add(getPrimitiveValue(value));
          }
        }
        changeSet.removeValues(field, removeableValues);
        changeSet.save();
      }
      return getFieldInfo(uriInfo, topic, field, topic.getValues(field), false);
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
  }

  public static PrestoTopic updateTopic(UriInfo uriInfo, PrestoSession session,
      PrestoTopic topic, PrestoType topicType, PrestoView fieldsView,
      Topic data) {
    PrestoDataProvider dataProvider = session.getDataProvider();

    PrestoChangeSet changeSet;
    if (topic == null) {
      changeSet = dataProvider.createTopic(topicType);
    } else {
      changeSet = dataProvider.updateTopic(topic);
    }

    Map<String, PrestoFieldUsage> fields = getFieldInstanceMap(topic, topicType, fieldsView);

    try {
      for (FieldData jsonField : data.getFields()) {
        String fieldId = jsonField.getId();

        PrestoFieldUsage field = fields.get(fieldId);

        boolean isReferenceField = field.isReferenceField();
        boolean isExternalType = field.getExternalType() != null;
        boolean isReadOnly = field.isReadOnly(); // ignore readOnly-fields 
        if (!isReadOnly) {
          if  (fields.containsKey(fieldId)) {
            Collection<Value> values = jsonField.getValues();
            Collection<Object> newValues = new ArrayList<Object>(values.size());
            for (Value value : values) {

              Topic embeddedReferenceValue = getEmbeddedReference(value);
              if (embeddedReferenceValue != null) {
                PrestoView valueView = field.getValueView();
                newValues.add(updateEmbeddedReference(uriInfo, session, valueView, embeddedReferenceValue));
              } else if (isReferenceField && !isExternalType) {
                String valueId = getReferenceValue(value);
                newValues.add(dataProvider.getTopicById(valueId));
              } else {
                newValues.add(getPrimitiveValue(value));
              }
            }
            changeSet.setValues(field, newValues);
          }
        }
      }
      topic = changeSet.save();
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
    return topic;
  }

  private static PrestoTopic updateEmbeddedReference(UriInfo uriInfo, PrestoSession session, PrestoView fieldsView, Topic newTopic) throws JSONException {

    PrestoDataProvider dataProvider = session.getDataProvider();
    PrestoSchemaProvider schemaProvider = session.getSchemaProvider();

    String topicId = newTopic.getId();

    PrestoTopic topic = null;
    PrestoType topicType;
    if (topicId == null) {
      TopicType type = newTopic.getType();
      String topicTypeId = type.getId();
      topicType = schemaProvider.getTypeById(topicTypeId);
    } else {
      topic = dataProvider.getTopicById(topicId);
      topicType = schemaProvider.getTypeById(topic.getTypeId());
    }

    return Utils.updateTopic(uriInfo, session, topic, topicType, fieldsView, newTopic);
  }

  private static Map<String, PrestoFieldUsage> getFieldInstanceMap(PrestoTopic topic,
      PrestoType topicType, PrestoView fieldsView) {
    Map<String, PrestoFieldUsage> fields = new HashMap<String, PrestoFieldUsage>();
    for (PrestoFieldUsage field : topicType.getFields(fieldsView)) {
      fields.put(field.getId(), field);
    }
    return fields;
  }

  private static Topic getEmbeddedReference(Value value) throws JSONException {
    return value.getEmbedded();
  }

  private static String getPrimitiveValue(Value value) throws JSONException {
    return value.getValue();
  }

  private static String getReferenceValue(Value value) throws JSONException {
    return value.getValue();
  }

}
