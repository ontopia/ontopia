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

import ontopoly.model.FieldsView;
import ontopoly.model.Topic;
import ontopoly.models.FieldsViewModel;
import ontopoly.pages.AbstractOntopolyPage;

import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.Strings;

public class TopicLink<T extends Topic> extends AbstractBookmarkablePageLink<T> {

  protected FieldsViewModel fieldsViewModel;
  
  public TopicLink(String id, IModel<T> topicModel) {
    super(id);
    setDefaultModel(topicModel); 
  }
  
  public TopicLink(String id, IModel<T> topicModel,
                   FieldsViewModel fieldsViewModel) {
    super(id);
    setDefaultModel(topicModel);
    this.fieldsViewModel = fieldsViewModel;
  }

  /**
   * Return true if the label text should be escaped.
   */
  public boolean getEscapeLabel() {
    return true;
  }
  
  @Override
  public Class<? extends Page> getPageClass() {
    AbstractOntopolyPage page = (AbstractOntopolyPage)getPage();
    return page.getPageClass(getTopic());
  }
  
  public Topic getTopic() {
    return (Topic)getDefaultModelObject();    
  }
  
  @Override
  public PageParameters getPageParameters() {
    AbstractOntopolyPage page = (AbstractOntopolyPage)getPage();
    PageParameters params = page.getPageParameters(getTopic());
    if (fieldsViewModel != null) {
      FieldsView fieldsView = fieldsViewModel.getFieldsView();
      if (fieldsView != null && !fieldsView.isDefaultView()) {
        params.put("viewId", fieldsView.getId());
      }
    }
    return params;
  }
  
  @Override
  protected void onComponentTag(ComponentTag tag) {
    tag.setName("a");
    super.onComponentTag(tag);
  }

  @Override
  public void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
    super.onComponentTagBody(markupStream, openTag);
    
    String label = getLabel();
    if (label != null) {
      if (getEscapeLabel()) {
        replaceComponentTagBody(markupStream, openTag, Strings.escapeMarkup(label));
      } else {
        replaceComponentTagBody(markupStream, openTag, label);
      }
    }
  }

  protected String getLabel() {
    AbstractOntopolyPage page = (AbstractOntopolyPage)getPage();
    return page.getLabel(getTopic());    
  }
  
  @Override
  public boolean isVisible() {
    return getTopic() != null && super.isVisible();
  }
  
  @Override
  public boolean isEnabled() {
    // TODO: need to decide whether link should be disabled or check should be done after click
    return getTopic() != null && super.isEnabled();
//    AbstractOntopolyPage page = (AbstractOntopolyPage)getPage();
//    return page.filterTopic(getTopic());
  }
 
  @Override
  public void onDetach() {
	  super.onDetach();
	  if (fieldsViewModel != null) {
      fieldsViewModel.detach();
    }
  }
}
