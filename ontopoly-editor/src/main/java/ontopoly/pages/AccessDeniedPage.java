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

import ontopoly.components.TitleHelpPanel;
import ontopoly.models.HelpLinkResourceModel;
import org.apache.wicket.PageParameters;
import org.apache.wicket.model.ResourceModel;

public class AccessDeniedPage extends OntopolyAbstractPage {

  private TitleHelpPanel titlePartPanel;
  private boolean isOntologyPage;

  public AccessDeniedPage(PageParameters parameters) {
    super(parameters);
    
    this.isOntologyPage = (parameters.get("ontology") != null);
    
    // Adding part containing title and help link
    createTitle();

    // initialize parent components
    initParentComponents();    
  }

  @Override
  protected int getMainMenuIndex() {
    return isOntologyPage ? ONTOLOGY_INDEX_IN_MAINMENU : INSTANCES_PAGE_INDEX_IN_MAINMENU; 
  }

  private void createTitle() {

    // Adding part containing title and help link
    this.titlePartPanel = new TitleHelpPanel("titlePartPanel",
        new ResourceModel("access.denied"),
        new HelpLinkResourceModel("help.link.accessdeniedpage"));
    this.titlePartPanel.setOutputMarkupId(true);
    add(titlePartPanel);
  }

}
