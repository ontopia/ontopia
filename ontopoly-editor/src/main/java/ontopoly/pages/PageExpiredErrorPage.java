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

import ontopoly.components.OntopolyBookmarkablePageLink;
import ontopoly.components.TitleHelpPanel;
import ontopoly.models.HelpLinkResourceModel;

import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.ResourceModel;

public class PageExpiredErrorPage extends AbstractOntopolyErrorPage {

  public PageExpiredErrorPage() {	  
  }
  
  public PageExpiredErrorPage(PageParameters parameters) {
    this(null, parameters);
  }
  
  public PageExpiredErrorPage(final Class<? extends Page> previousPage, PageParameters parameters) {	
    super(parameters);
    
    createTitle();
    
    add(new OntopolyBookmarkablePageLink("startPage", StartPage.class, new ResourceModel("topic.map.index.page").getObject().toString()));
    
    // The bookmarkablePageLink class demands that the page argument is not equal to null, so
    // it has to be set to a concrete page if previousPage is null.
    Class<? extends Page> page = StartPage.class;
    if(previousPage != null) {
      page = previousPage;
    }
    
    WebMarkupContainer previousPageLinkContainer = new WebMarkupContainer("previousPageLinkContainer") {
      public boolean isVisible() {
        return previousPage != null ? true : false;
      }  
    };
    previousPageLinkContainer.setOutputMarkupPlaceholderTag(true);
    add(previousPageLinkContainer);
    
    String label = "previous page";
    previousPageLinkContainer.add(new OntopolyBookmarkablePageLink("previousPage", page, parameters, label));
  }
  
  private void createTitle() {
    add(new TitleHelpPanel("titlePartPanel", 
          new ResourceModel("page.expired.title"), new HelpLinkResourceModel("help.link.startpage")));
  }
  
}
