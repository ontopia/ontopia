package net.ontopia.tropics.resources;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Map;

import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.tropics.utils.URIUtils;

import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

public class TopicResource extends BaseResource {

  @Get("xtm2|xml")
  public Representation getTopic() throws ResourceException {
    Map<QueryParam, String> params =  URIUtils.extractParameters(getResponse(), getQuery());
    
    TopicMapIF tm = getTopicMapFromParameter(params.get(QueryParam.INCLUDE));
    if (tm == null) {
      getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND);
      return null;
    }
    
    Reference baseRef = getRequest().getResourceRef().getBaseRef();    
    TopicIF sourceTopic;
    try {
      sourceTopic = (TopicIF) tm.getObjectByItemIdentifier(new URILocator(baseRef.toString()));
    } catch (MalformedURLException e) {
      getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
      e.printStackTrace();
      return null;
    }
    
    TopicMapIF resultTM = createTopicMapForTopics(tm, baseRef, Arrays.asList(sourceTopic));
    if (resultTM == null) return null;
    
    return new StringRepresentation(TM_UTILS.writeToXTM(resultTM));
  }

}
