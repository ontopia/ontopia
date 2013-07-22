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
package ontopoly.components;

import org.apache.wicket.Component;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

public abstract class OmnigatorLinkFunctionBoxPanel extends CustomLinkFunctionBoxPanel {
  
  public OmnigatorLinkFunctionBoxPanel(String id) {
    super(id);
  }
  
  @Override
  protected IModel<String> getFirstResourceModel() {
    return new ResourceModel("omnigator.text1");
  }

  @Override
  protected IModel<String> getSecondResourceModel() {
    return new ResourceModel("omnigator.text2");    
  }
 
  @Override
  protected Component getLink(String id) {
    String url = new ResourceModel("omnigator.url").getObject().toString()+"?tm="+getTopicMapId()+"&id="+getTopicId();
    
    return new ExternalLink(id, url, new ResourceModel("omnigator.link.label").getObject().toString()) {
      @Override
      protected void onComponentTag(ComponentTag tag) {
        tag.setName("a");
        tag.put("target", "_blank");
        super.onComponentTag(tag);
      }
    };
  }
  
  protected abstract String getTopicMapId();
  protected abstract String getTopicId();
}
