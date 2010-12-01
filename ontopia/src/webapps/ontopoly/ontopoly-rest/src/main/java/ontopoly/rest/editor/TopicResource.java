package ontopoly.rest.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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
  @Path("topic/{topicMapId}:{topicId}")
  public Map<String,Object> getTopic(@PathParam("topicMapId") final String topicMapId, 
                              @PathParam("topicId") final String topicId) throws Exception {
    
    TopicMapStoreIF store = TopicMaps.createStore(topicMapId, true);
    
    try {
      TopicMap topicMap = new TopicMap(store.getTopicMap(), topicMapId);

      System.out.println("TM4: " + topicMap + " " + topicId);
      Topic topic = topicMap.getTopicById(topicId);
      TopicType topicType = OntopolyUtils.getDefaultTopicType(topic);
      FieldsView fieldsView = FieldsView.getDefaultFieldsView(topicMap);

      return Utils.createFieldConfigMap(topic, topicType, fieldsView);
      
    } catch (Exception e) {
      store.abort();
      throw e;
    } finally {
      store.close();      
    }
  }
  
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("topic/{topicMapId}:{topicId}:{fieldId}/list")
  public Map<String,Object> getList(@PathParam("topicMapId") final String topicMapId, 
                              @PathParam("topicId") final String topicId,
                              @PathParam("fieldId") final String fieldId) throws Exception {
    
    TopicMapStoreIF store = TopicMaps.createStore(topicMapId, true);
    
    try {
      TopicMap topicMap = new TopicMap(store.getTopicMap(), topicMapId);

      System.out.println("TM4: " + topicMap + " " + topicId);
      Topic topic = topicMap.getTopicById(topicId);
      TopicType topicType = OntopolyUtils.getDefaultTopicType(topic);
      FieldsView fieldsView = FieldsView.getDefaultFieldsView(topicMap);

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

            System.out.println("A: " + arity);
            if (arity < 2) {
              result.put("values", Collections.emptyList());
            } else if (arity == 2) {
              for (RoleField otherRoleField : roleField.getOtherRoleFields()) {
                result.put("values", Utils.getExistingTopicValues(topic, otherRoleField, otherRoleField.getAllowedPlayers(topic)));
                break;
              }
            } else if (arity > 2) {
              List<Map<String,Object>> roles = new ArrayList<Map<String,Object>>();
              for (RoleField otherRoleField : roleField.getOtherRoleFields()) {
                Map<String,Object> roleData = new LinkedHashMap<String,Object>();
                roleData.put("id", otherRoleField.getId());
                roleData.put("values", Utils.getExistingTopicValues(topic, otherRoleField, otherRoleField.getAllowedPlayers(topic)));
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
