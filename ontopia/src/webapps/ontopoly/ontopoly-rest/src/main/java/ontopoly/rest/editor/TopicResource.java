package ontopoly.rest.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.entry.TopicMaps;
import ontopoly.model.EditMode;
import ontopoly.model.FieldAssignment;
import ontopoly.model.FieldDefinition;
import ontopoly.model.FieldInstance;
import ontopoly.model.FieldsView;
import ontopoly.model.RoleField;
import ontopoly.model.Topic;
import ontopoly.model.TopicMap;
import ontopoly.model.TopicType;
import ontopoly.model.ViewModes;
import ontopoly.rest.editor.Utils.Link;
import ontopoly.utils.OntopolyUtils;

import org.codehaus.jettison.json.JSONObject;

@Path("/editor")
public class TopicResource {

  // TODO: add more endpoints: 
  //
  // 1: / - information about server and link to /available-topicmaps
  // 2: /available-topicmaps - lists available topic maps
  // 3: /create-instance/{topicMapId}

  private TopicListener topicListener;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("")
  public Map<String,Object> getRootInfo(@Context UriInfo uriInfo) throws Exception {

    Map<String,Object> result = new LinkedHashMap<String,Object>();

    result.put("id", uriInfo.getBaseUri() + "editor");
    result.put("version", 0);
    result.put("name", "Ontopoly Editor REST API");

    List<Link> links = new ArrayList<Link>();
    links.add(new Link("available-topicmaps", uriInfo.getBaseUri() + "editor/available-topicmaps"));
    result.put("links", links);      
    return result;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("available-topicmaps")
  public Map<String,Object> getTopicMaps(@Context UriInfo uriInfo) throws Exception {

    Map<String,Object> result = new LinkedHashMap<String,Object>();

    result.put("id", "topicmaps");
    result.put("name", "Ontopoly Editor REST API");

    List<Map<String,Object>> topicmaps = new ArrayList<Map<String,Object>>();
    Map<String,Object> topicmap = new LinkedHashMap<String,Object>();
    topicmap.put("id", "litteraturklubben.xtm");
    topicmap.put("name", "Litteraturklubben");
    List<Link> links = new ArrayList<Link>();
    links.add(new Link("edit", uriInfo.getBaseUri() + "editor/topicmap-info/litteraturklubben.xtm"));
    topicmap.put("links", links);    
    topicmaps.add(topicmap);
    result.put("topicmaps", topicmaps);      
    return result;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("topicmap-info/{topicMapId}")
  public Map<String,Object> getTopicMapInfo(
      @Context UriInfo uriInfo, 
      @PathParam("topicMapId") final String topicMapId) throws Exception {

    TopicMapStoreIF store = TopicMaps.createStore(topicMapId, true);
    try {
      TopicMap topicMap = new TopicMap(store.getTopicMap(), topicMapId);

      Map<String,Object> result = new LinkedHashMap<String,Object>();

      result.put("id", topicMap.getId());
      result.put("name", topicMap.getName());

      List<Link> links = new ArrayList<Link>();
      links.add(new Link("available-types-tree", uriInfo.getBaseUri() + "editor/available-types-tree/" + topicMap.getId()));
      links.add(new Link("available-types-tree-lazy", uriInfo.getBaseUri() + "editor/available-types-tree-lazy/" + topicMap.getId()));
      links.add(new Link("edit-topic-by-id", uriInfo.getBaseUri() + "editor/topic/" + topicMap.getId() + "/{topicId}"));
      result.put("links", links);      
      return result;


    } catch (Exception e) {
      store.abort();
      throw e;
    } finally {
      store.close();      
    }
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("create-instance/{topicMapId}/{topicTypeId}")
  public Map<String,Object> createInstance(
      @Context UriInfo uriInfo, 
      @PathParam("topicMapId") final String topicMapId, 
      @PathParam("topicTypeId") final String topicTypeId) throws Exception {

    TopicMapStoreIF store = TopicMaps.createStore(topicMapId, true);
    try {
      TopicMap topicMap = new TopicMap(store.getTopicMap(), topicMapId);

      Topic topicType_ = topicMap.getTopicById(topicTypeId);
      TopicType topicType = new TopicType(topicType_.getTopicIF(), topicMap);

      FieldsView fieldsView = FieldsView.getDefaultFieldsView(topicMap);

      return Utils.getNewTopicInfo(uriInfo, topicType, fieldsView);

    } catch (Exception e) {
      store.abort();
      throw e;
    } finally {
      store.close();      
    }
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("create-field-instance/{topicMapId}/{parentTopicId}/{parentFieldId}/{playerTypeId}")
  public Map<String,Object> createInstance(
      @Context UriInfo uriInfo, 
      @PathParam("topicMapId") final String topicMapId,
      @PathParam("parentTopicId") final String parentTopicId,
      @PathParam("parentFieldId") final String parentFieldId, 
      @PathParam("playerTypeId") final String playerTypeId) throws Exception {

    TopicMapStoreIF store = TopicMaps.createStore(topicMapId, true);
    try {
      TopicMap topicMap = new TopicMap(store.getTopicMap(), topicMapId);

      Topic playerType_ = topicMap.getTopicById(playerTypeId);
      TopicType playerType = new TopicType(playerType_.getTopicIF(), topicMap);

      FieldsView fieldsView = FieldsView.getDefaultFieldsView(topicMap);

      return Utils.getNewTopicInfo(uriInfo, playerType, fieldsView, parentTopicId, parentFieldId);

    } catch (Exception e) {
      store.abort();
      throw e;
    } finally {
      store.close();      
    }
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("topic/{topicMapId}/{topicId}")
  public Map<String,Object> getTopicInDefaultView(
      @Context UriInfo uriInfo, 
      @PathParam("topicMapId") final String topicMapId, 
      @PathParam("topicId") final String topicId) throws Exception {

    TopicMapStoreIF store = TopicMaps.createStore(topicMapId, true);
    try {
      TopicMap topicMap = new TopicMap(store.getTopicMap(), topicMapId);

      System.out.println("TM4: " + topicMap + " " + topicId + " " + uriInfo.getAbsolutePath() + " " + uriInfo.getPath() + " " + uriInfo.getBaseUri());
      Topic topic = topicMap.getTopicById(topicId);
      TopicType topicType = OntopolyUtils.getDefaultTopicType(topic);
      FieldsView fieldsView = FieldsView.getDefaultFieldsView(topicMap);

      return Utils.getTopicInfo(uriInfo, topic, topicType, fieldsView);

    } catch (Exception e) {
      store.abort();
      throw e;
    } finally {
      store.close();      
    }
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("topic/{topicMapId}/{topicId}/{viewId}")
  public Map<String,Object> getTopicInView(@Context UriInfo uriInfo, 
      @PathParam("topicMapId") final String topicMapId, 
      @PathParam("topicId") final String topicId,
      @PathParam("viewId") final String viewId) throws Exception {

    TopicMapStoreIF store = TopicMaps.createStore(topicMapId, true);

    try {
      TopicMap topicMap = new TopicMap(store.getTopicMap(), topicMapId);

      System.out.println("TM5: " + topicMap + " " + topicId + " " + uriInfo.getAbsolutePath() + " " + uriInfo.getPath() + " " + uriInfo.getBaseUri());
      Topic topic = topicMap.getTopicById(topicId);

      TopicType topicType = OntopolyUtils.getDefaultTopicType(topic);

      Topic viewTopic = topicMap.getTopicById(viewId);
      FieldsView fieldsView = new FieldsView(viewTopic);

      return Utils.getTopicInfo(uriInfo, topic, topicType, fieldsView);

    } catch (Exception e) {
      store.abort();
      throw e;
    } finally {
      store.close();      
    }
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("topic-info/{topicMapId}/{topicId}")
  public Map<String,Object> getTopicInfo(@Context UriInfo uriInfo, 
      @PathParam("topicMapId") final String topicMapId, 
      @PathParam("topicId") final String topicId) throws Exception {

    TopicMapStoreIF store = TopicMaps.createStore(topicMapId, true);

    try {
      TopicMap topicMap = new TopicMap(store.getTopicMap(), topicMapId);
      Topic topic = topicMap.getTopicById(topicId);

      System.out.println("TT: " + topic + " "  + topicId);
      TopicType topicType = OntopolyUtils.getDefaultTopicType(topic);

      FieldsView fieldsView = FieldsView.getDefaultFieldsView(topicMap);

      Map<String,Object> result = new LinkedHashMap<String,Object>();

      result.put("id", topic.getId());
      result.put("views", Utils.getViews(uriInfo, topic, topicType, fieldsView));
      return result;

    } catch (Exception e) {
      store.abort();
      throw e;
    } finally {
      store.close();      
    }
  }

  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("topic/{topicMapId}/{topicId}/{viewId}")
  public Map<String,Object> updateTopic(@Context UriInfo uriInfo, 
      @PathParam("topicMapId") final String topicMapId, 
      @PathParam("topicId") final String topicId, 
      @PathParam("viewId") final String viewId, JSONObject jsonObject) throws Exception {
    System.out.println("IN2: " + jsonObject);

    TopicMapStoreIF store = TopicMaps.createStore(topicMapId, true);

    try {
      TopicMap topicMap = new TopicMap(store.getTopicMap(), topicMapId);

      Topic topic;
      TopicType topicType;
      if (topicId.startsWith("_")) {
        Topic topicType_ = topicMap.getTopicById(topicId.substring(1));
        topicType = new TopicType(topicType_.getTopicIF(), topicMap);
        topic  = topicType.createInstance(null);
      } else {
        topic = topicMap.getTopicById(topicId);
        topicType = OntopolyUtils.getDefaultTopicType(topic);
      }

      Topic viewTopic = topicMap.getTopicById(viewId);
      FieldsView fieldsView = new FieldsView(viewTopic);

      Map<String, Object> result = Utils.updateTopic(uriInfo, topic, topicType, fieldsView, jsonObject);
      String id = topic.getId();

      store.commit();
      topicListener.onTopicUpdated(id);

      return result;
    } catch (Exception e) {
      store.abort();
      throw e;
    } finally {
      store.close();
    }
  }

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("add-field-values/{topicMapId}/{topicId}/{viewId}/{fieldId}")
  public Map<String,Object> addFieldValues(@Context UriInfo uriInfo, 
      @PathParam("topicMapId") final String topicMapId, 
      @PathParam("topicId") final String topicId, 
      @PathParam("viewId") final String viewId,
      @PathParam("fieldId") final String fieldId, JSONObject jsonObject) throws Exception {
    System.out.println("IN3_: " + topicMapId + " " + topicId + " " + viewId +  " " + fieldId);
    System.out.println("IN3: " + jsonObject);

    TopicMapStoreIF store = TopicMaps.createStore(topicMapId, true);

    try {
      TopicMap topicMap = new TopicMap(store.getTopicMap(), topicMapId);

      Topic topic = topicMap.getTopicById(topicId);
      TopicType topicType = OntopolyUtils.getDefaultTopicType(topic);

      Topic viewTopic = topicMap.getTopicById(viewId);
      FieldsView fieldsView = new FieldsView(viewTopic);

      Map<String, Object> result = Utils.addFieldValues(uriInfo, topic, topicType, fieldsView, fieldId, jsonObject);

      String id = topic.getId();

      store.commit();
      topicListener.onTopicUpdated(id);

      return result;
    } catch (Exception e) {
      store.abort();
      throw e;
    } finally {
      store.close();      
    } 
  }

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("remove-field-values/{topicMapId}/{topicId}/{viewId}/{fieldId}")
  public Map<String,Object> removeFieldValues(@Context UriInfo uriInfo, 
      @PathParam("topicMapId") final String topicMapId, 
      @PathParam("topicId") final String topicId, 
      @PathParam("viewId") final String viewId,
      @PathParam("fieldId") final String fieldId, JSONObject jsonObject) throws Exception {
    System.out.println("IN4: " + jsonObject);

    TopicMapStoreIF store = TopicMaps.createStore(topicMapId, true);

    try {
      TopicMap topicMap = new TopicMap(store.getTopicMap(), topicMapId);

      Topic topic = topicMap.getTopicById(topicId);
      TopicType topicType = OntopolyUtils.getDefaultTopicType(topic);

      Topic viewTopic = topicMap.getTopicById(viewId);
      FieldsView fieldsView = new FieldsView(viewTopic);

      Map<String, Object> result =  Utils.removeFieldValues(uriInfo, topic, topicType, fieldsView, fieldId, jsonObject);

      String id = topic.getId();

      store.commit();
      topicListener.onTopicUpdated(id);

      return result;
    } catch (Exception e) {
      store.abort();
      throw e;
    } finally {
      store.close();      
    } 
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("available-field-values/{topicMapId}/{topicId}/{viewId}/{fieldId}")
  public Map<String,Object> getAvailableFieldValues(@Context UriInfo uriInfo, 
      @PathParam("topicMapId") final String topicMapId, 
      @PathParam("topicId") final String topicId, 
      @PathParam("viewId") final String viewId,
      @PathParam("fieldId") final String fieldId) throws Exception {

    TopicMapStoreIF store = TopicMaps.createStore(topicMapId, true);

    try {
      TopicMap topicMap = new TopicMap(store.getTopicMap(), topicMapId);

      Topic topic;
      TopicType topicType;
      if (topicId.startsWith("_")) {
        Topic topicType_ = topicMap.getTopicById(topicId.substring(1));
        topicType = new TopicType(topicType_.getTopicIF(), topicMap);
        topic  = null;
      } else {
        topic = topicMap.getTopicById(topicId);
        topicType = OntopolyUtils.getDefaultTopicType(topic);
      }

      Topic viewTopic = topicMap.getTopicById(viewId);
      FieldsView fieldsView = new FieldsView(viewTopic);

      if (topic != null) {
        for (FieldInstance fieldInstance : topic.getFieldInstances(topicType, fieldsView)) {
          FieldDefinition fieldDefinition = fieldInstance.getFieldAssignment().getFieldDefinition();
          if (fieldDefinition.getId().equals(fieldId) &&
              fieldDefinition.getFieldType() == FieldDefinition.FIELD_TYPE_ROLE) {

            return createFieldInfoAllowed(uriInfo, topic, topicType, fieldsView, fieldDefinition);
          }
        }
      } else {
        for (FieldAssignment fieldAssigment : topicType.getFieldAssignments(fieldsView)) {
          FieldDefinition fieldDefinition = fieldAssigment.getFieldDefinition();
          if (fieldDefinition.getId().equals(fieldId) &&
              fieldDefinition.getFieldType() == FieldDefinition.FIELD_TYPE_ROLE) {

            return createFieldInfoAllowed(uriInfo, topic, topicType, fieldsView, fieldDefinition);
          }
        }
      }
      throw new RuntimeException("Illegal field reference.");
    } catch (Exception e) {
      store.abort();
      throw e;
    } finally {
      store.close();      
    }
  }

  private Map<String,Object> createFieldInfoAllowed(UriInfo uriInfo,
      Topic topic, TopicType topicType, FieldsView fieldsView, FieldDefinition fieldDefinition) {
    RoleField roleField = (RoleField)fieldDefinition;
    int arity = roleField.getAssociationField().getArity();

    Map<String,Object> result = new LinkedHashMap<String,Object>();
    result.put("id", fieldDefinition.getId());
    result.put("name", fieldDefinition.getFieldName());

    if (arity < 2) {
      result.put("values", Collections.emptyList());
    } else if (arity == 2) {
      FieldsView childView = fieldDefinition.getValueView(fieldsView);
      ViewModes viewModes = fieldDefinition.getViewModes(childView);
      for (RoleField otherRoleField : roleField.getOtherRoleFields()) {
        
        boolean addable = true;
        List<Topic> allowedPlayers = otherRoleField.getAllowedPlayers(topic);
        List<Object> values = new ArrayList<Object>(allowedPlayers.size());
        for (Topic value : allowedPlayers) {
          values.add(Utils.getAllowedTopicFieldValue(uriInfo, topic, topicType, roleField, value , otherRoleField, childView, viewModes.isTraversable(), addable));
        }
        result.put("values", values);
        break;
      }
    } else if (arity > 2) {
      FieldsView childView = fieldDefinition.getValueView(fieldsView);
      ViewModes viewModes = fieldDefinition.getViewModes(childView);
      List<Map<String,Object>> roles = new ArrayList<Map<String,Object>>();
      for (RoleField otherRoleField : roleField.getOtherRoleFields()) {
        Map<String,Object> roleData = new LinkedHashMap<String,Object>();
        roleData.put("id", otherRoleField.getId());
        roleData.put("name", otherRoleField.getFieldName());

        boolean readOnly = true;
        List<Topic> allowedPlayers = otherRoleField.getAllowedPlayers(topic);
        List<Object> values = new ArrayList<Object>(allowedPlayers.size());
        for (Topic value : allowedPlayers) {
          values.add(Utils.getAllowedTopicFieldValue(uriInfo, topic, topicType, roleField, value , otherRoleField, childView, viewModes.isTraversable(), readOnly));
        }
        roleData.put("values", values);
        
        roles.add(roleData);
      }
      result.put("values", roles);
      System.out.println("X: " + result);
    }
    return result;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("available-field-types/{topicMapId}/{topicId}/{viewId}/{fieldId}")
  public Map<String,Object> getAvailableFieldTypes(@Context UriInfo uriInfo, 
      @PathParam("topicMapId") final String topicMapId, 
      @PathParam("topicId") final String topicId, 
      @PathParam("viewId") final String viewId,
      @PathParam("fieldId") final String fieldId) throws Exception {

    TopicMapStoreIF store = TopicMaps.createStore(topicMapId, true);

    try {
      TopicMap topicMap = new TopicMap(store.getTopicMap(), topicMapId);

      Topic topic = topicMap.getTopicById(topicId);
      TopicType topicType = OntopolyUtils.getDefaultTopicType(topic);

      Topic viewTopic = topicMap.getTopicById(viewId);
      FieldsView fieldsView = new FieldsView(viewTopic);

      for (FieldInstance fieldInstance : topic.getFieldInstances(topicType, fieldsView)) {
        FieldDefinition fieldDefinition = fieldInstance.getFieldAssignment().getFieldDefinition();
        if (fieldDefinition.getId().equals(fieldId)) {
          if (fieldDefinition.getFieldType() == FieldDefinition.FIELD_TYPE_ROLE) {
            RoleField roleField = (RoleField)fieldDefinition;
            int arity = roleField.getAssociationField().getArity();

            if (arity == 2) {

              FieldsView childView = fieldDefinition.getValueView(fieldsView);
              EditMode editMode = roleField.getEditMode();
              ViewModes viewModes = fieldDefinition.getViewModes(childView);
              boolean allowCreate = !editMode.isNoEdit() && !editMode.isExistingValuesOnly() && !viewModes.isReadOnly();

              Map<String,Object> result = new LinkedHashMap<String,Object>();
              result.put("id", fieldDefinition.getId());
              result.put("name", fieldDefinition.getFieldName());
              for (RoleField otherRoleField : roleField.getOtherRoleFields()) {
                
                if (allowCreate) {
                  Collection<TopicType> allowedPlayerTypes = otherRoleField.getAllowedPlayerTypes(topic);
                  List<Object> types = new ArrayList<Object>(allowedPlayerTypes.size());
                  for (TopicType playerType : allowedPlayerTypes) {
                    types.add(Utils.getCreateFieldInstance(uriInfo, topic, roleField, playerType, otherRoleField, childView, viewModes));
                  }
                  result.put("types", types);
                } else {
                  result.put("types", Collections.EMPTY_LIST);
                }
                break;
              }
              return result;
            }
          }
        }
      }
      throw new RuntimeException("Illegal field reference.");
    } catch (Exception e) {
      store.abort();
      throw e;
    } finally {
      store.close();      
    }
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("available-types-tree-lazy/{topicMapId}")
  public Map<String,Object> getAvailableTypesTreeLazy(@Context UriInfo uriInfo, 
      @PathParam("topicMapId") final String topicMapId) throws Exception {

    TopicMapStoreIF store = TopicMaps.createStore(topicMapId, true);

    try {
      TopicMap topicMap = new TopicMap(store.getTopicMap(), topicMapId);

      Map<String,Object> result = new LinkedHashMap<String,Object>();
      result.put("types", TypeUtils.getAvailableTypesTreeLazy(uriInfo, topicMap.getRootTopicTypes()));      
      return result;

    } catch (Exception e) {
      store.abort();
      throw e;
    } finally {
      store.close();      
    }
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("available-types-tree-lazy/{topicMapId}/{topicId}")
  public Map<String,Object> getAvailableTypesTreeLazy(@Context UriInfo uriInfo, 
      @PathParam("topicMapId") final String topicMapId, 
      @PathParam("topicId") final String topicId) throws Exception {

    TopicMapStoreIF store = TopicMaps.createStore(topicMapId, true);

    try {
      TopicMap topicMap = new TopicMap(store.getTopicMap(), topicMapId);

      Topic topicType_ = topicMap.getTopicById(topicId);
      TopicType topicType = new TopicType(topicType_.getTopicIF(), topicMap);

      Map<String,Object> result = new LinkedHashMap<String,Object>();
      result.put("types", TypeUtils.getAvailableTypesTreeLazy(uriInfo, topicType.getDirectSubOrdinateTypes()));      
      return result;

    } catch (Exception e) {
      store.abort();
      throw e;
    } finally {
      store.close();      
    }
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("available-types-tree/{topicMapId}")
  public Map<String,Object> getAvailableTypesTree(@Context UriInfo uriInfo, 
      @PathParam("topicMapId") final String topicMapId) throws Exception {

    TopicMapStoreIF store = TopicMaps.createStore(topicMapId, true);

    try {
      TopicMap topicMap = new TopicMap(store.getTopicMap(), topicMapId);

      Map<String,Object> result = new LinkedHashMap<String,Object>();
      result.put("types", TypeUtils.getAvailableTypesTree(uriInfo, topicMap.getRootTopicTypes()));      
      return result;

    } catch (Exception e) {
      store.abort();
      throw e;
    } finally {
      store.close();      
    }
  }

  @Context
  public void setServletContext(ServletContext servletContext) {
    String listenerClassName = servletContext.getInitParameter("ontopoly-rest.listener");
    if (listenerClassName != null) {
      try {
        Class<?> listenerClass = Class.forName(listenerClassName);
        this.topicListener = (TopicListener) listenerClass.newInstance();

      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    } else {
      this.topicListener = new TopicListener() {
        public void onTopicUpdated(String topicId) {
        }
      };
    }
  }

}
