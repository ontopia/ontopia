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

import ontopoly.components.FooterPanel;
import ontopoly.components.HeaderPanel;

import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;

public class SignOutPage extends AbstractOntopolyPage { 

  public static final String REDIRECTPAGE_PARAM = "redirectpage"; 
  
  @SuppressWarnings("unchecked") 
  public SignOutPage(final PageParameters parameters) {
    add(new HeaderPanel("header"));    
    add(new FooterPanel("footer"));
    
    String page = parameters.getString(REDIRECTPAGE_PARAM); 
    Class<? extends Page> pageClass; 
    if (page != null) { 
      try { 
        pageClass = (Class<? extends Page>) Class.forName(page); 
      } catch (ClassNotFoundException e) { 
        throw new RuntimeException(e); 
      } 
    } else { 
      pageClass = getApplication().getHomePage(); 
    } 
    getSession().invalidate();    
    setResponsePage(pageClass);
    setRedirect(true);
  } 
} 
