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

import ontopoly.images.ImageResource;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

public abstract class OntopolyImageLink extends AjaxFallbackLink<Object> {
  
  protected String image;
  protected IModel<String> titleModel;
  
  public OntopolyImageLink(String id, String image) {
    this(id, image, null);
  }
  
  public OntopolyImageLink(String id, String image, IModel<String> titleModel) {
    super(id);
    this.image = image;
    this.titleModel = titleModel;

//    this.setRenderBodyOnly(true);
    
    add(new Image("image", new AbstractReadOnlyModel<ResourceReference>() {
      @Override
      public ResourceReference getObject() {
        return new ResourceReference(ImageResource.class, getImage());
      }      
    }));
  }

  @Override
  protected void onComponentTag(ComponentTag tag) {
    IModel<String> titleModel = getTitleModel();
    if (titleModel != null) {
      tag.put("title", titleModel.getObject());
    }
    super.onComponentTag(tag);
  }
  
  public String getImage() {
    return image;
  }

  public IModel<String> getTitleModel() {
    return titleModel;    
  }

}
