/*
 * #!
 * Ontopoly Editor
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */
package ontopoly.utils;

import java.util.Map;

import net.ontopia.topicmaps.core.OccurrenceIF;
import ontopoly.OntopolyContext;
import ontopoly.model.TopicMap;

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

    Map<String,?> params = requestParameters.getParameters();
    
    String topicMapId = ((String[])params.get("topicMapId"))[0];
    TopicMap topicMap = OntopolyContext.getTopicMap(topicMapId);

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
