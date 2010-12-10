package ontopoly.rest.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

import org.codehaus.jettison.json.JSONObject;

import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.entry.TopicMaps;
import ontopoly.model.FieldDefinition;
import ontopoly.model.FieldInstance;
import ontopoly.model.FieldsView;
import ontopoly.model.RoleField;
import ontopoly.model.Topic;
import ontopoly.model.TopicMap;
import ontopoly.model.TopicType;
import ontopoly.utils.OntopolyUtils;

@Path("/editor")
public class TopicResource {

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("topic/{topicMapId}/{topicId}")
  public Map<String,Object> getTopic(@Context UriInfo uriInfo, 
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

      return Utils.updateTopic(uriInfo, topic, topicType, fieldsView, jsonObject);

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

      return Utils.addFieldValues(uriInfo, topic, topicType, fieldsView, fieldId, jsonObject);
      
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

      return Utils.removeFieldValues(uriInfo, topic, topicType, fieldsView, fieldId, jsonObject);
      
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

      List<FieldInstance> fieldInstances = topic.getFieldInstances(topicType, fieldsView);
      for (FieldInstance fieldInstance: fieldInstances) {
        FieldDefinition fieldDefinition = fieldInstance.getFieldAssignment().getFieldDefinition();
        if (fieldDefinition.getId().equals(fieldId)) {
          if (fieldDefinition.getFieldType() == FieldDefinition.FIELD_TYPE_ROLE) {
            RoleField roleField = (RoleField)fieldDefinition;
            int arity = roleField.getAssociationField().getArity();
            Map<String,Object> result = new LinkedHashMap<String,Object>();
            result.put("id", fieldDefinition.getId());
            result.put("arity", arity);

            System.out.println("Ax: " + arity);
            if (arity < 2) {
              result.put("values", Collections.emptyList());
            } else if (arity == 2) {
              FieldsView childView = fieldDefinition.getValueView(fieldsView);
              for (RoleField otherRoleField : roleField.getOtherRoleFields()) {
                result.put("values", Utils.getExistingTopicValues(uriInfo, topic, roleField, otherRoleField.getAllowedPlayers(topic), otherRoleField, fieldsView, childView));
                break;
              }
            } else if (arity > 2) {
              FieldsView childView = fieldDefinition.getValueView(fieldsView);              
              List<Map<String,Object>> roles = new ArrayList<Map<String,Object>>();
              for (RoleField otherRoleField : roleField.getOtherRoleFields()) {
                Map<String,Object> roleData = new LinkedHashMap<String,Object>();
                roleData.put("id", otherRoleField.getId());
                roleData.put("name", otherRoleField.getFieldName());
                roleData.put("values", Utils.getExistingTopicValues(uriInfo, topic, roleField, otherRoleField.getAllowedPlayers(topic), otherRoleField, fieldsView, childView));
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

}
