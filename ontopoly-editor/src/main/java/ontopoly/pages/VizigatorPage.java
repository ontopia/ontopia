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
package ontopoly.pages;


import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.xml.XTMFragmentExporter;
import ontopoly.components.FunctionBoxesPanel;
import ontopoly.components.TitleHelpPanel;
import ontopoly.model.Topic;
import ontopoly.model.TopicMap;
import ontopoly.models.HelpLinkResourceModel;
import ontopoly.models.TopicModel;

import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.PropertyModel;

public class VizigatorPage extends OntopolyAbstractPage {

  protected TopicModel<Topic> topicModel;
  
  public VizigatorPage() {	  
  }
  
  public VizigatorPage(PageParameters parameters) {
    super(parameters);
  
    this.topicModel = new TopicModel<Topic>(parameters.getString("topicMapId"), parameters.getString("topicId"));
    
    // Adding part containing title and help link
    createTitle();

    // Add fields panel
    createApplet();
    
    // Function boxes
    createFunctionBoxes(); 
    
    // initialize parent components
    initParentComponents();    
  }

  private void createApplet() {
    TopicIF topic = topicModel.getTopic().getTopicIF();

    String idtype = "source";
    String idvalue = null;
    Iterator<LocatorIF> it = topic.getItemIdentifiers().iterator();
    if (it.hasNext()) {
      idvalue = it.next().getExternalForm();
    }

    if (idvalue == null) {
      it = topic.getSubjectIdentifiers().iterator();
      if (it.hasNext()) {
        idvalue = ((LocatorIF) it.next()).getExternalForm();
        idtype = "indicator";
      }
    }

    if (idvalue == null) {
      it = topic.getSubjectLocators().iterator();
      if (it.hasNext()) {
        idvalue = ((LocatorIF) it.next()).getExternalForm();
        idtype = "subject";
      }
    }

    if (idvalue == null) {
      TopicMap topicMap = this.getTopicMapModel().getTopicMap();
      idvalue = XTMFragmentExporter.makeVirtualReference(topic, topicMap.getId());
      idtype = "source";
    }
    final String _idtype = idtype;
    final String _idvalue = idvalue;
    add(new AppletParamLabel("tmid") {
      @Override
      public String getValue() {
        return getTopicMapModel().getTopicMap().getId();
      }      
    });
    add(new AppletParamLabel("idtype") {
      @Override
      public String getValue() {
        return _idtype;
      }      
    });
    add(new AppletParamLabel("idvalue") {
      @Override
      public String getValue() {
        return _idvalue;
      }      
    });
    // TODO: implement support for "config" parameter
  }
  
  private static abstract class AppletParamLabel extends Label {
    AppletParamLabel(String id) {
      super(id);
    }
    public String getName() {
      return getId();
    }
    public abstract String getValue();
    
    @Override
    protected void onComponentTag(ComponentTag tag) {
      tag.setName("param");
      tag.put("name", getName());
      tag.put("value", getValue());
      super.onComponentTag(tag);
    }
  }

  @Override
  protected int getMainMenuIndex() {
    return INSTANCES_PAGE_INDEX_IN_MAINMENU; 
  }
  
  private void createTitle() {
    // Adding part containing title and help link
    TitleHelpPanel titlePartPanel = new TitleHelpPanel("titlePartPanel",
        new PropertyModel<String>(topicModel, "name"), new HelpLinkResourceModel("help.link.instancepage"));
    titlePartPanel.setOutputMarkupId(true);
    add(titlePartPanel);    
  }

  private void createFunctionBoxes() {

    add(new FunctionBoxesPanel("functionBoxes") {

      @Override
      protected List<Component> getFunctionBoxesList(String id) {
        return Collections.emptyList();
      }

    });
  }

  @Override
  public void onDetach() {
    topicModel.detach();
    super.onDetach();
  }
  
}
