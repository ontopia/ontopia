package net.ontopia.tropics.resources;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;
import net.ontopia.tropics.utils.URIUtils;

import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

public class TopicsResource extends BaseResource {

  @Get("xtm2|xml")
  public Representation getTopics() throws ResourceException {
    Map<QueryParam, String> params = URIUtils.extractParameters(getResponse(), getQuery());
    
    TopicMapIF tm = getTopicMapFromParameter(params.get(QueryParam.INCLUDE));
    if (tm == null) {
      getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND);
      return null;
    }
    
    Collection<TopicIF> topics = new ArrayList<TopicIF>();

    if (params.containsKey(QueryParam.HAS_TYPE)) {
      ClassInstanceIndexIF ciIndex = (ClassInstanceIndexIF) tm.getIndex("net.ontopia.topicmaps.core.index.ClassInstanceIndexIF");

      try {
        LocatorIF locator = new URILocator(getRequest().getHostRef() + "/api/v1" + params.get(QueryParam.HAS_TYPE));
        TopicIF topicType = (TopicIF) tm.getObjectByItemIdentifier(locator);
        if (topicType != null) topics.addAll(ciIndex.getTopics(topicType));
      } catch (MalformedURLException e) {
        getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
        e.printStackTrace();
        return null;
      }      
    }
    
    TopicMapIF resultTM = createTopicMapForTopics(tm, getRequest().getResourceRef().getBaseRef(), topics);
    if (resultTM == null) return null;
    
    return new StringRepresentation(TM_UTILS.writeToXTM(resultTM));
  }
}
