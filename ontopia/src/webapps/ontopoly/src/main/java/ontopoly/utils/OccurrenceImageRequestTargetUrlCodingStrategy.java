package ontopoly.utils;

import java.util.Map;

import net.ontopia.topicmaps.core.OccurrenceIF;
import ontopoly.OntopolyContext;
import ontopoly.components.OccurrenceWebResource;
import ontopoly.model.OntopolyTopicMapIF;

import org.apache.wicket.IRequestTarget;
import org.apache.wicket.request.RequestParameters;
import org.apache.wicket.request.target.coding.AbstractRequestTargetUrlCodingStrategy;
import org.apache.wicket.request.target.resource.ResourceStreamRequestTarget;
import org.apache.wicket.util.resource.IResourceStream;

public class OccurrenceImageRequestTargetUrlCodingStrategy extends
    AbstractRequestTargetUrlCodingStrategy {

  public OccurrenceImageRequestTargetUrlCodingStrategy(String mountPath) {
    super(mountPath);
  }
  
  public IRequestTarget decode(RequestParameters requestParameters) {   
    //String name = requestParameters.getPath().substring(getMountPath().length()); 

    Map params = requestParameters.getParameters();
    
    String topicMapId = ((String[])params.get("topicMapId"))[0];
    OntopolyTopicMapIF topicMap = OntopolyContext.getTopicMap(topicMapId);

    String occurrenceId = ((String[])params.get("occurrenceId"))[0];    
    OccurrenceIF occ = (OccurrenceIF) topicMap.getTopicMapIF().getObjectById(occurrenceId);
    
    return new OccurrenceResourceStreamRequestTarget(OccurrenceWebResource.getResourceStream(occ), topicMapId, occurrenceId); 
  }

  public CharSequence encode(IRequestTarget requestTarget) { 
    OccurrenceResourceStreamRequestTarget target = (OccurrenceResourceStreamRequestTarget)requestTarget;    
    return getMountPath() + "?topicMapID=" + target.getTopicMapId() + "&occurrenceId=" + target.getOccurrenceId();
  }

  public boolean matches(IRequestTarget requestTarget) {
    return (requestTarget instanceof OccurrenceResourceStreamRequestTarget); 
  }
  
  public static class OccurrenceResourceStreamRequestTarget extends ResourceStreamRequestTarget {

    protected String topicMapId;
    protected String occurrenceId;
    
    public OccurrenceResourceStreamRequestTarget(IResourceStream resourceStream, String topicMapId, String occurrenceId) {
      super(resourceStream);
      this.topicMapId = topicMapId;
      this.occurrenceId = occurrenceId;
    }

    public String getTopicMapId() {
      return topicMapId;
    }
    
    public String getOccurrenceId() {
      return occurrenceId;
    }
  }

}
