package ontopoly.rest.generic;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.entry.TopicMaps;
import ontopoly.model.FieldsView;
import ontopoly.model.Topic;
import ontopoly.model.TopicMap;
import ontopoly.model.TopicType;
import ontopoly.rest.generic.Utils;
import ontopoly.utils.OntopolyUtils;

@Path("/topics/{topicMapId}:{topicId}")
public class TopicResource {
  
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/super")
  public Map<String,Object> getSuper(@PathParam("topicMapId") final String topicMapId, 
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
  
//@GET
//@Produces(MediaType.APPLICATION_JSON)
//@Path("/config")
//public TopicConfig getConfig(@PathParam("topicMapId") final String topicMapId, 
//                             @PathParam("topicId") final String topicId) throws Exception {
//  
//  TopicMapStoreIF store = TopicMaps.createStore(topicMapId, true);
//  
//  try {
//    TopicMap topicMap = new TopicMap(store.getTopicMap(), topicMapId);
//
//    System.out.println("TM4: " + topicMap + " " + topicId);
//    Topic topic = topicMap.getTopicById(topicId);
//    TopicType topicType = OntopolyUtils.getDefaultTopicType(topic);
//    
//    TopicConfig topicConfig = new TopicConfig(topic.getId(), topic.getName());
//    topicConfig.setType("topic");
//    
//    List<FieldConfig> fields = new ArrayList<FieldConfig>();
//    
//    for (FieldInstance fieldInstance : topic.getFieldInstances(topicType)) {
//        FieldAssignment fieldAssignment = fieldInstance.getFieldAssignment();
//        FieldDefinition fieldDefinition = fieldAssignment.getFieldDefinition(); 
//        
//        FieldsView fieldsView = FieldsView.getDefaultFieldsView(topicMap);
//        FieldConfig fieldConfig = Utils.createFieldConfig(fieldDefinition, fieldsView);
//        
//        fields.add(fieldConfig);
//    }
//    topicConfig.setFields(fields);
//    return topicConfig;
//    
//  } catch (Exception e) {
//    store.abort();
//    throw e;
//  } finally {
//    store.close();      
//  }
//}

}
