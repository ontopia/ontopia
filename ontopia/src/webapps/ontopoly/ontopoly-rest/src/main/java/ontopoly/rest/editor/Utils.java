package ontopoly.rest.editor;

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

import net.ontopia.utils.ObjectUtils;
import ontopoly.rest.editor.spi.PrestoChangeSet;
import ontopoly.rest.editor.spi.PrestoDataProvider;
import ontopoly.rest.editor.spi.PrestoField;
import ontopoly.rest.editor.spi.PrestoFieldUsage;
import ontopoly.rest.editor.spi.PrestoSession;
import ontopoly.rest.editor.spi.PrestoTopic;
import ontopoly.rest.editor.spi.PrestoType;
import ontopoly.rest.editor.spi.PrestoView;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class Utils {

  public static class Link {
    private String rel;
    private String href;

    public Link(String rel, String href) {
      this.rel = rel;
      this.href = href;
    }

    public void setRel(String rel) {
      this.rel = rel;
    }
    public String getRel() {
      return rel;
    }
    public void setHref(String href) {
      this.href = href;
    }
    public String getHref() {
      return href;
    }

  }

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

  public static Map<String,Object> getTopicInfo(UriInfo uriInfo, PrestoTopic topic, PrestoType type, PrestoView view) {
    Map<String,Object> result = new LinkedHashMap<String,Object>();

    result.put("id", topic.getId());
    result.put("name", topic.getName());

    Map<String,Object> typeInfo = new LinkedHashMap<String,Object>();    
    typeInfo.put("id", type.getId());
    typeInfo.put("name", type.getName());
    typeInfo.put("readOnly", type.isReadOnly());

    List<Link> typeLinks = new ArrayList<Link>();
    if (!type.isAbstract() && !type.isReadOnly()) {
      typeLinks.add(new Link("create-instance", Links.getCreateInstanceLinkFor(uriInfo, type)));
    }
    typeInfo.put("links", typeLinks);

    result.put("type", typeInfo);

    result.put("view", view.getId());

    List<Link> topicLinks = new ArrayList<Link>();
    topicLinks.add(new Link("edit", Links.getEditLinkFor(uriInfo, topic, view)));    
    //    topicLinks.add(new Link("remove", "http://examples.org/topics/" + topic.getId() + "/remove"));
    result.put("links", topicLinks);

    List<Map<String,Object>> fields = new ArrayList<Map<String,Object>>(); 

    for (PrestoFieldUsage field : type.getFields(view)) {
      fields.add(getFieldInfo(uriInfo, topic, field, topic.getValues(field)));
    }
    result.put("fields", fields);
    result.put("views", getViews(uriInfo, topic, type, view));
    return result;
  }

  public static Map<String,Object> getNewTopicInfo(UriInfo uriInfo, PrestoType topicType, PrestoView fieldsView) {
    return getNewTopicInfo(uriInfo, topicType, fieldsView, null, null);
  }

  public static Map<String,Object> getNewTopicInfo(UriInfo uriInfo, PrestoType topicType, PrestoView fieldsView, String parentId, String parentFieldId) {
    Map<String,Object> result = new LinkedHashMap<String,Object>();

    if (parentId != null) {
      Map<String,Object> origin = new LinkedHashMap<String,Object>();    
      origin.put("topicId", parentId);
      origin.put("fieldId", parentFieldId);
      result.put("origin", origin);
    }

    Map<String,Object> typeInfo = new LinkedHashMap<String,Object>();    
    typeInfo.put("id", topicType.getId());
    typeInfo.put("name", topicType.getName());
    result.put("type", typeInfo);

    result.put("view", fieldsView.getId());

    List<Link> topicLinks = new ArrayList<Link>();
    topicLinks.add(new Link("create", Links.getCreateLinkFor(uriInfo, topicType, fieldsView)));    
    result.put("links", topicLinks);

    List<Map<String,Object>> fields = new ArrayList<Map<String,Object>>(); 

    PrestoTopic topic = null;
    for (PrestoFieldUsage field : topicType.getFields(fieldsView)) {
      fields.add(getFieldInfo(uriInfo, topic, field, Collections.emptyList()));
    }
    result.put("fields", fields);
    result.put("views", Collections.singleton(getView(uriInfo, null, fieldsView)));
    return result;
  }

  private static Map<String, Object> getFieldInfo(UriInfo uriInfo,
      PrestoTopic topic, PrestoFieldUsage field, Collection<? extends Object> fieldValues) {

    PrestoType topicType = field.getType();
    PrestoView parentView = field.getView();

    boolean isNewTopic = topic == null;

    String databaseId = field.getSchemaProvider().getDatabaseId();
    String topicId = isNewTopic ? "_" + topicType.getId() : topic.getId();
    String parentViewId = parentView.getId();
    String fieldId = field.getId();

    String fieldReference = databaseId + "/" + topicId + "/" + parentViewId + "/" + fieldId;

    Map<String,Object> fieldInfo = new LinkedHashMap<String,Object>();

    fieldInfo.put("id", fieldId);
    fieldInfo.put("name", field.getName());

    int minCard = field.getMinCardinality();
    if (minCard > 0) {
      fieldInfo.put("minCardinality", minCard);
    }

    int maxCard = field.getMaxCardinality();
    if (maxCard > 0) {
      fieldInfo.put("maxCardinality", maxCard);
    }

    String validationType = field.getValidationType();
    if (validationType != null) {
      fieldInfo.put("validation", validationType);
    }

    String interfaceControl = field.getInterfaceControl();
    if (interfaceControl != null) {
      fieldInfo.put("interfaceControl", interfaceControl);          
    }

    if (field.isPrimitiveField()) {
      // fieldInfo.put("type", field.getFieldType());
      String dataType = field.getDataType();
      if (dataType != null) {
        fieldInfo.put("datatype", dataType);
      }
      if (field.isReadOnly()) {
        fieldInfo.put("readOnly", Boolean.TRUE);
        fieldInfo.put("links", Collections.EMPTY_LIST);
      } else {
        List<Link> fieldLinks = new ArrayList<Link>();
        if (!isNewTopic) {
          fieldLinks.add(new Link("add-field-values", uriInfo.getBaseUri() + "editor/add-field-values/" + fieldReference));
          fieldLinks.add(new Link("remove-field-values", uriInfo.getBaseUri() + "editor/remove-field-values/" + fieldReference));
        }
        fieldInfo.put("links", fieldLinks);
      }

    } else if (field.isReferenceField()) {
      // fieldInfo.put("type", field.getFieldType());
      fieldInfo.put("datatype", "reference");

      boolean allowEdit = !field.isReadOnly();
      boolean allowAddRemove = allowEdit && !field.isNewValuesOnly();
      boolean allowCreate = allowEdit && !field.isExistingValuesOnly();
      if (!allowEdit) {
        fieldInfo.put("readOnly", Boolean.TRUE);
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
      fieldInfo.put("links", fieldLinks);

    } else {
      // used by query fields, which can have both primitive and reference values
      // fieldInfo.put("type", field.getFieldType());
      fieldInfo.put("datatype", "query");
      if (field.isReadOnly()) {
        fieldInfo.put("readOnly", Boolean.TRUE);
      }
      fieldInfo.put("links", Collections.EMPTY_LIST);
    }

    fieldInfo.put("values", getValues(uriInfo, field, fieldValues));
    return fieldInfo;
  }

  public static List<Map<String, Object>> getViews(UriInfo uriInfo,
      PrestoTopic topic, PrestoType topicType, PrestoView fieldsView) {

    Collection<PrestoView> fieldViews = topicType.getViews(fieldsView);

    List<Map<String,Object>> views = new ArrayList<Map<String,Object>>(fieldViews.size()); 
    for (PrestoView _fieldsView : fieldViews) {
      views.add(getView(uriInfo, topic, _fieldsView));
    }
    return views;
  }

  public static Map<String, Object> getView(UriInfo uriInfo, PrestoTopic topic,
      PrestoView _fieldsView) {
    Map<String,Object> view = new LinkedHashMap<String,Object>();
    view.put("id", _fieldsView.getId());
    view.put("name", _fieldsView.getName());

    List<Link> links = new ArrayList<Link>();
    if (topic != null) {
      links.add(new Link("edit-in-view", Links.getEditLinkFor(uriInfo, topic, _fieldsView)));
    }
    view.put("links", links);
    return view;
  }

  protected static List<Object> getValues(UriInfo uriInfo, PrestoFieldUsage field, Collection<? extends Object> fieldValues) {
    List<Object> result = new ArrayList<Object>(fieldValues.size());
    for (Object value : fieldValues) {
      result.add(getValue(uriInfo, field, value));
    }
    Collections.sort(result, new Comparator<Object>() {
      public int compare(Object o1, Object o2) {
        if (o1 instanceof Map) {
          @SuppressWarnings("unchecked")
          Map<String,Object> v1 = (Map<String,Object>)o1;
          @SuppressWarnings("unchecked")
          Map<String,Object> v2 = (Map<String,Object>)o2;
          String vx1 = (String)v1.get("name");
          if (vx1 == null) {
            vx1 = (String)v1.get("value");
          }
          String vx2 = (String)v2.get("name");
          if (vx2 == null) {
            vx2 = (String)v2.get("value");
          }
          return ObjectUtils.compare(vx1, vx2);
        } else {
          @SuppressWarnings("unchecked")
          List<Map<String,Object>> v1 = (List<Map<String,Object>>)o1;
          @SuppressWarnings("unchecked")
          List<Map<String,Object>> v2 = (List<Map<String,Object>>)o2;
          return compare(v1.get(0), v2.get(0));
        } 
      }
    });
    return result;
  }

  protected static Map<String,Object> getValue(UriInfo uriInfo, PrestoFieldUsage field, Object fieldValue) {
    if (fieldValue instanceof PrestoTopic) {
      PrestoTopic valueTopic = (PrestoTopic)fieldValue;
      if (field.isEmbedded()) {
        PrestoType valueType = field.getSchemaProvider().getTypeById(valueTopic.getTypeId());
        return getTopicInfo(uriInfo, valueTopic, valueType, field.getValueView());
      } else {
        return getExistingTopicFieldValue(uriInfo, field, valueTopic);
      }
    } else {
      Map<String,Object> result = new LinkedHashMap<String, Object>();
      result.put("value", fieldValue);
      boolean removable = !field.isReadOnly();
      if (removable) {
        result.put("removable", Boolean.TRUE);
      }
      return result;
    }
  }

  public static Map<String,Object> getExistingTopicFieldValue(UriInfo uriInfo,
      PrestoFieldUsage field, PrestoTopic value) {

    Map<String, Object> result = new LinkedHashMap<String,Object>();
    result.put("id", value.getId());
    result.put("name", value.getName());

    if (!field.isReadOnly()) {
      result.put("removable", Boolean.TRUE);
    }

    List<Link> links = new ArrayList<Link>();
    if (field.isTraversable()) {
      links.add(new Link("edit", Links.getEditLinkFor(uriInfo, value, field.getValueView())));
    }
    result.put("links", links);

    return result;
  }

  public static Object getAllowedTopicFieldValue(UriInfo uriInfo, 
      PrestoTopic value, PrestoView childView, boolean traversable) {

    Map<String, Object> result = new LinkedHashMap<String,Object>();
    result.put("id", value.getId());
    result.put("name", value.getName());

    List<Link> links = new ArrayList<Link>();
    if (traversable) {
      links.add(new Link("edit", Links.getEditLinkFor(uriInfo, value, childView)));
    }
    result.put("links", links);

    return result;
  }

  public static Object getCreateFieldInstance(UriInfo uriInfo, PrestoTopic topic, PrestoFieldUsage field, PrestoType type) {

    Map<String, Object> result = new LinkedHashMap<String,Object>();
    result.put("id", type.getId());
    result.put("name", type.getName());

    List<Link> links = new ArrayList<Link>();
    links.add(new Link("create-field-instance", uriInfo.getBaseUri() + "editor/create-field-instance/" + field.getSchemaProvider().getDatabaseId() + "/" + topic.getId() + "/" + field.getId() + "/" + type.getId()));
    result.put("links", links);

    return result;
  }

  public static Map<String, Object> addFieldValues(UriInfo uriInfo, PrestoSession session, PrestoTopic topic, PrestoFieldUsage field, JSONObject fieldObject) {
    try {

      PrestoDataProvider dataProvider = session.getDataProvider();

      if  (field != null) {

        JSONArray values = fieldObject.getJSONArray("values");
        int valuesCount = values.length();
        if (valuesCount > 0) {

          PrestoChangeSet changeSet = dataProvider.updateTopic(topic);

          boolean isReferenceField = field.isReferenceField();

          Collection<Object> addableValues = new HashSet<Object>(valuesCount);
          for (int vc=0; vc < valuesCount; vc++) {
            if (isReferenceField) {
              String valueId = getReferenceValue(values, vc);
              PrestoTopic valueTopic = dataProvider.getTopicById(valueId);
              addableValues.add(valueTopic);
            } else {
              addableValues.add(getPrimitiveValue(values, vc));
            }
          }
          changeSet.addValues(field, addableValues);
          changeSet.save();
        }
      }
      return getFieldInfo(uriInfo, topic, field, topic.getValues(field));
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
  }

  public static Map<String, Object>  removeFieldValues(UriInfo uriInfo, PrestoSession session, PrestoTopic topic, PrestoFieldUsage field, JSONObject fieldObject) {
    try {

      PrestoDataProvider dataProvider = session.getDataProvider();

      if  (field != null) {

        JSONArray values = fieldObject.getJSONArray("values");
        int valuesCount = values.length();
        if (valuesCount > 0) {

          PrestoChangeSet changeSet = dataProvider.updateTopic(topic);

          boolean isReferenceField = field.isReferenceField();

          Collection<Object> removeableValues = new HashSet<Object>(valuesCount);
          for (int vc=0; vc < valuesCount; vc++) {
            if (isReferenceField) {
              String valueId = getReferenceValue(values, vc);
              PrestoTopic valueTopic = dataProvider.getTopicById(valueId);
              removeableValues.add(valueTopic);
            } else {
              removeableValues.add(getPrimitiveValue(values, vc));
            }
          }
          changeSet.removeValues(field, removeableValues);
          changeSet.save();
        }
      }
      return getFieldInfo(uriInfo, topic, field, topic.getValues(field));
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
  }

  public static Map<String, Object> updateTopic(UriInfo uriInfo, PrestoSession session, 
      PrestoTopic topic, PrestoType topicType, PrestoView fieldsView, JSONObject data) {

    PrestoDataProvider dataProvider = session.getDataProvider();

    PrestoChangeSet changeSet;
    if (topic == null) {
      changeSet = dataProvider.createTopic(topicType);
    } else {
      changeSet = dataProvider.updateTopic(topic);
    }

    Map<String, PrestoFieldUsage> fields = getFieldInstanceMap(topic, topicType, fieldsView);

    try {
      JSONArray fieldsArray = data.getJSONArray("fields");
      int fieldsCount = fieldsArray.length();
      for (int fc=0; fc < fieldsCount; fc++) {

        JSONObject fieldObject = fieldsArray.getJSONObject(fc);
        String fieldId = fieldObject.getString("id");

        PrestoFieldUsage field = fields.get(fieldId);

        boolean isReferenceField = field.isReferenceField();

        boolean isReadOnly = fieldObject.optBoolean("readOnly", false); // ignore readOnly-fields 
        if (!isReadOnly) {
          if  (fields.containsKey(fieldId)) {

            JSONArray values = fieldObject.getJSONArray("values");
            int valuesCount = values.length();
            Collection<Object> newValues = new ArrayList<Object>(valuesCount); 
            for (int vc=0; vc < valuesCount; vc++) {

              if (isReferenceField) {
                String valueId = getReferenceValue(values, vc);
                newValues.add(dataProvider.getTopicById(valueId));
              } else {
                newValues.add(getPrimitiveValue(values, vc));
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
    return Utils.getTopicInfo(uriInfo, topic, topicType, fieldsView);
  }

  private static Map<String, PrestoFieldUsage> getFieldInstanceMap(PrestoTopic topic,
      PrestoType topicType, PrestoView fieldsView) {
    Map<String, PrestoFieldUsage> fields = new HashMap<String, PrestoFieldUsage>();
    for (PrestoFieldUsage field : topicType.getFields(fieldsView)) {
      fields.put(field.getId(), field);
    }
    return fields;
  }

  private static String getPrimitiveValue(JSONArray values, int vindex) throws JSONException {

    JSONObject valueObject = values.getJSONObject(vindex);
    return valueObject.getString("value");              
  }

  private static String getReferenceValue(JSONArray values, int vindex) throws JSONException {

    JSONObject valueObject = values.getJSONObject(vindex);
    return valueObject.getString("id");              
  }

}
