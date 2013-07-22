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

import ontopoly.OntopolySession;
import ontopoly.pages.SignInPage;
import ontopoly.pages.SignOutPage;

import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

public class UserPanel extends Panel { 
  public UserPanel(String id, Class<? extends Page> logoutPageClass) { 
    super(id);
    add(new Label("fullname", new PropertyModel<Object>(this, "session.user.fullname"))); 
    PageParameters parameters = new PageParameters(); 
    parameters.add(SignOutPage.REDIRECTPAGE_PARAM, logoutPageClass.getName()); 
    add(new BookmarkablePageLink<Page>("signout", SignOutPage.class, parameters) { 
      @Override 
      public boolean isVisible() { 
        OntopolySession session = (OntopolySession)Session.get();
        return session.isAuthenticated(); 
      }
      @Override
      public boolean isEnabled() {
        OntopolySession session = (OntopolySession)Session.get();
        return !session.isAutoLogin();
      }
    }); 
    add(new Link<Object>("signin") { 
      @Override 
      public void onClick() { 
        throw new RestartResponseAtInterceptPageException(SignInPage.class); 
      } 
      @Override 
      public boolean isVisible() {
        OntopolySession session = (OntopolySession)Session.get();
        return !session.isAuthenticated(); 
      } 
    }); 
  }
  @Override
  public boolean isVisible() {
    OntopolySession session = (OntopolySession)Session.get();
    return session.isAccessStrategyEnabled();     
  }
  
}
