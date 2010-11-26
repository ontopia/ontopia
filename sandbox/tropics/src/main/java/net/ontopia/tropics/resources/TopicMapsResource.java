package net.ontopia.tropics.resources;

import java.util.Collection;

import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.ConstraintViolationException;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.tropics.groups.GroupsIndexFactory;
import net.ontopia.tropics.utils.Predicate;
import net.ontopia.tropics.utils.TopicCreatorForTopicMapIds;
import net.ontopia.tropics.utils.TopicMapUtils;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;

public class TopicMapsResource extends BaseResource {
  private final TopicMapUtils tmUtils = new TopicMapUtils();
  
  @Get("htm|html")
  public Representation getHtml() {
    StringBuilder sb = new StringBuilder();
    sb.append("<html>\n");
    sb.append("\t<head>\n");
    sb.append("\t\t<title>TROPICS TESTING</title>\n");
    sb.append("\t</head>\n");
    sb.append("\t<body>\n");
    sb.append("\t\t<h2>I can see stored topic maps:</h2>\n");
    sb.append("\t\t<ul>\n");
    
    HtmlDumper htmlDumper = new HtmlDumper();
    tmUtils.iterateTopicMapIds(getTopicMapRepository(), htmlDumper);
    sb.append(htmlDumper.getHtml());

    sb.append("\t\t</ul>\n");
    sb.append("\t</body>\n");
    sb.append("<html>");
    
    return new StringRepresentation(sb.toString(), MediaType.TEXT_HTML);
  }
  
  @Get("xtm2|xml")
  public Representation getXTM() {
    TopicMapIF tm = createTopicMapOfTopicMaps();
    
    if (tm == null) {
      getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND);
      return null;
    }        
    
    return new StringRepresentation(TM_UTILS.writeToXTM(tm));
  }
  
  private TopicMapIF createTopicMapOfTopicMaps() {
    String hostRef = getRequest().getHostRef().toString();
    
    InMemoryTopicMapStore store = new InMemoryTopicMapStore();    
    store.setBaseAddress(URILocator.create(hostRef + "/api/v1/topicmaps"));
    
    TopicMapIF tm = store.getTopicMap();
    TopicCreatorForTopicMapIds topicCreator = new TopicCreatorForTopicMapIds(tm, hostRef);
    
    try {
      topicCreator.init();
      tmUtils.iterateTopicMapIds(getTopicMapRepository(), topicCreator);
    } catch (ConstraintViolationException e) {
      e.printStackTrace();
      return null;
    }
    
    createTopicMapGroupType(tm);
    
    Collection<String> groupIds = GroupsIndexFactory.getGroupsIndex().getGroupIds();
    for (String groupId : groupIds) {
      createTopicMapGroupInstance(tm, groupId);      
    }
    
    return tm;
  }
  
  private static class HtmlDumper implements Predicate {
    private final StringBuilder sb = new StringBuilder();

    public void apply(String topicMapId) {
      sb.append("\t\t\t<li><a href=\"http://localhost:8182/api/v1/topicmaps/").append(topicMapId).append("\">");
      sb.append(topicMapId).append("</a></li>\n");
    }
    
    public String getHtml() {
      return sb.toString();
    }    
  }  
}
