package net.ontopia.tropics.resources;

import java.io.IOException;
import java.io.StringReader;

import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapImporterIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.xml.XTMTopicMapReader;
import net.ontopia.tropics.groups.GroupsIndexFactory;

import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

public class TopicMapResource extends BaseResource {
  
  @Get("xtm2|xml")
  public Representation getXTM() throws ResourceException {    
    TopicMapIF tm = getTopicMapFromPath();    
    
    if (tm == null) {
      getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND);
      return null;
    } 
    
    return new StringRepresentation(TM_UTILS.writeToXTM(tm));
  }
  
  @Get("jtm|json|txt")
  public Representation getJSON() throws ResourceException {    
    TopicMapIF tm = getTopicMapFromPath();    
    
    if (tm == null) {
      getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND);
      return null;
    }
        
    return new StringRepresentation(TM_UTILS.writeToJTM(tm));
  }
  
  @Put("xtm2|xml")
  public void putXTM2() throws ResourceException {
    String content = null;
    try {
      content = getRequest().getEntity().getText();
    } catch (IOException e) {
      new ResourceException(e);
    }
    
    String topicMapId = getTopicMapId();
    
    if (topicMapId.contains("group.")) {
      return;
    }
    
    try {
      TopicMapReferenceIF originalReference = TM_UTILS.findTopicMapReference(topicMapId + ".xtm", false);
      TopicMapStoreIF store = originalReference.createStore(true);
      store.open();
      originalReference.clear();
      
      store = originalReference.createStore(false);
      TopicMapIF originalTm = store.getTopicMap();           
      
      URILocator baseLocator = URILocator.create(getRequest().getResourceRef().toString());
      TopicMapImporterIF reader = new XTMTopicMapReader(new StringReader(content), baseLocator);
      reader.importInto(originalTm);
      
      store.commit();
      originalReference.storeClosed(store);
    } catch (IOException e) {
      e.printStackTrace();
      new ResourceException(e);
    }          
    
    GroupsIndexFactory.getGroupsIndex().updated(topicMapId);
  }
}
