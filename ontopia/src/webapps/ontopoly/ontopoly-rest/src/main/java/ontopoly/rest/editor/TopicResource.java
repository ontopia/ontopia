package ontopoly.rest.editor;

import java.util.ArrayList;
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
  public Map<String,Object> getInfo(@Context UriInfo uriInfo) throws Exception {

    Map<String,Object> result = new LinkedHashMap<String,Object>();

    result.put("id", uriInfo.getBaseUri() + "editor");
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
      links.add(new Link("available-types", uriInfo.getBaseUri() + "editor/available-types/" + topicMap.getId()));
      links.add(new Link("topic", uriInfo.getBaseUri() + "editor/topic/" + topicMap.getId() + "/{topicId}"));
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

      return Utils.createTopicInfo(uriInfo, topic, topicType, fieldsView);

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

      return Utils.createTopicInfo(uriInfo, topic, topicType, fieldsView);

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

      Topic topic = topicMap.getTopicById(topicId);
      TopicType topicType = OntopolyUtils.getDefaultTopicType(topic);

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

            Map<String,Object> result = new LinkedHashMap<String,Object>();
            result.put("id", fieldDefinition.getId());
            result.put("arity", arity);

            if (arity < 2) {
              result.put("values", Collections.emptyList());
            } else if (arity == 2) {
              FieldsView childView = fieldDefinition.getValueView(fieldsView);
              ViewModes viewModes = fieldDefinition.getViewModes(childView);
              for (RoleField otherRoleField : roleField.getOtherRoleFields()) {
                boolean readOnly = true;
                result.put("values", Utils.getExistingTopicValues(uriInfo, topic, roleField, otherRoleField.getAllowedPlayers(topic), otherRoleField, fieldsView, childView, viewModes.isTraversable(), readOnly));
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
                roleData.put("values", Utils.getExistingTopicValues(uriInfo, topic, roleField, otherRoleField.getAllowedPlayers(topic), otherRoleField, fieldsView, childView, viewModes.isTraversable(), readOnly));
                roles.add(roleData);
              }
              result.put("values", roles);
              System.out.println("X: " + result);
            }
            return result;
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

            Map<String,Object> result = new LinkedHashMap<String,Object>();
            result.put("id", fieldDefinition.getId());
            result.put("arity", arity);

            if (arity < 2) {
              result.put("values", Collections.emptyList());
            } else if (arity == 2) {
              FieldsView childView = fieldDefinition.getValueView(fieldsView);
              EditMode editMode = roleField.getEditMode();
              ViewModes viewModes = fieldDefinition.getViewModes(childView);
              boolean allowCreate = !editMode.isNoEdit() && !editMode.isExistingValuesOnly() && !viewModes.isReadOnly();
              System.out.println("AC: " + allowCreate);
              for (RoleField otherRoleField : roleField.getOtherRoleFields()) {
                result.put("values", allowCreate ? Utils.getCreatePlayerTypes(uriInfo, topic, roleField, otherRoleField.getAllowedPlayerTypes(topic), otherRoleField, fieldsView, childView, viewModes) : Collections.emptySet());
                break;
              }
            } else if (arity > 2) {
              FieldsView childView = fieldDefinition.getValueView(fieldsView);
              ViewModes viewModes = fieldDefinition.getViewModes(childView);
              List<Map<String,Object>> roles = new ArrayList<Map<String,Object>>();
              for (RoleField otherRoleField : roleField.getOtherRoleFields()) {
                EditMode editMode = otherRoleField.getEditMode();
                boolean allowCreate = !editMode.isNoEdit() && !editMode.isExistingValuesOnly() && !viewModes.isReadOnly();
                Map<String,Object> roleData = new LinkedHashMap<String,Object>();
                roleData.put("id", otherRoleField.getId());
                roleData.put("name", otherRoleField.getFieldName());
                roleData.put("values", allowCreate ? Utils.getCreatePlayerTypes(uriInfo, topic, roleField, otherRoleField.getAllowedPlayerTypes(topic), otherRoleField, fieldsView, childView, viewModes) : Collections.emptySet());
                roles.add(roleData);
              }
              result.put("values", roles);
              System.out.println("X: " + result);
            }
            return result;
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
  @Path("available-types/{topicMapId}")
  public Map<String,Object> getAvailableTypes(@Context UriInfo uriInfo, 
      @PathParam("topicMapId") final String topicMapId) throws Exception {

    TopicMapStoreIF store = TopicMaps.createStore(topicMapId, true);

    try {
      TopicMap topicMap = new TopicMap(store.getTopicMap(), topicMapId);

      Map<String,Object> result = new LinkedHashMap<String,Object>();
      //      result.put("id", topicMap.getId());

      List<Map<String,Object>> types = new ArrayList<Map<String,Object>>(); 

      for (TopicType topicType : topicMap.getTopicTypes()) {
        if (!topicType.isSystemTopic()) {
          Map<String,Object> type = new LinkedHashMap<String,Object>();
          type.put("id", topicType.getId());
          type.put("name", topicType.getName());

          List<Link> links = new ArrayList<Link>();

          if (!topicType.isAbstract()) {
            links.add(new Link("create-instance", uriInfo.getBaseUri() + "editor/create-instance/" + topicType.getId()));
          }
          type.put("links", links);      
          types.add(type);
        }
      }
      result.put("types", types);      
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
