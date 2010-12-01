package ontopoly.rest.generic;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.entry.TopicMaps;
import ontopoly.model.FieldDefinition;
import ontopoly.model.FieldsView;
import ontopoly.model.TopicMap;

@Path("/fields/{topicMapId}:{topicId}:{fieldId}")
public class FieldResource {
  
//  @GET
//  @Produces(MediaType.APPLICATION_JSON)
//  @Path("/config")
//  public FieldConfig getConfig(@PathParam("topicMapId") final String topicMapId, 
//      @PathParam("fieldId") final String fieldId) throws Exception {
//    
//    TopicMapStoreIF store = TopicMaps.createStore(topicMapId, true);
//    
//    try {
//      TopicMap topicMap = new TopicMap(store.getTopicMap(), topicMapId);
//
//      FieldDefinition fieldDefinition = FieldDefinition.getFieldDefinition(fieldId, topicMap);
//      FieldsView fieldsView = FieldsView.getDefaultFieldsView(topicMap);
//      return Utils.createFieldConfig(fieldDefinition, fieldsView);
//      
//    } catch (Exception e) {
//      store.abort();
//      throw e;
//    } finally {
//      store.close();      
//    }
//  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/values")
  public FieldConfig getValues(@PathParam("topicMapId") final String topicMapId, 
      @PathParam("topicId") final String topicId,
      @PathParam("fieldId") final String fieldId) throws Exception {
    
    TopicMapStoreIF store = TopicMaps.createStore(topicMapId, true);
    
    try {
      TopicMap topicMap = new TopicMap(store.getTopicMap(), topicMapId);

      
      FieldDefinition fieldDefinition = FieldDefinition.getFieldDefinition(fieldId, topicMap);
      FieldsView fieldsView = FieldsView.getDefaultFieldsView(topicMap);
      return Utils.createFieldConfig(fieldDefinition, fieldsView);
      
    } catch (Exception e) {
      store.abort();
      throw e;
    } finally {
      store.close();      
    }
  }

}
