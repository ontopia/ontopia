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

import net.ontopia.Ontopia;
import ontopoly.components.TitleHelpPanel;
import ontopoly.models.HelpLinkResourceModel;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.ResourceModel;

public class InternalErrorPage extends AbstractOntopolyErrorPage {

  public InternalErrorPage() {
	  this(null);
  }
  
  public InternalErrorPage(PageParameters parameters) {
    super(parameters);
    
    createTitle();
    
    add(new BookmarkablePageLink<Object>("link", StartPage.class));
    
    add(new Label("java_version") {
      @Override
      protected void onComponentTag(ComponentTag tag) {
      super.onComponentTag(tag);
      tag.put("value", System.getProperty("java.vm.vendor")
          +", "+System.getProperty("java.vm.version"));
      }  
    });
    
    add(new Label("os_version") {
      @Override
      protected void onComponentTag(ComponentTag tag) {
      super.onComponentTag(tag);
      tag.put("value", System.getProperty("os.name")
          +", "+System.getProperty("os.version")
          +" ("+System.getProperty("os.arch")+")");
      }  
    });
    
    add(new Label("oks_version") {
      @Override
      protected void onComponentTag(ComponentTag tag) {
      super.onComponentTag(tag);
      tag.put("value", Ontopia.getVersion());
      }  
    });
    
    add(new Label("server_name") {
      @Override
      protected void onComponentTag(ComponentTag tag) {
      super.onComponentTag(tag);
      tag.put("value", getWebRequestCycle().getWebRequest().getHttpServletRequest().getServerName());
      }  
    });
    
    add(new Label("server_info") {
      @Override
      protected void onComponentTag(ComponentTag tag) {
      super.onComponentTag(tag);
      tag.put("value", getWebRequestCycle().getWebRequest().getHttpServletRequest().getSession(true).getServletContext().getServerInfo()); 
      }  
    });
    
    add(new Label("server_port") {
      @Override
      protected void onComponentTag(ComponentTag tag) {
      super.onComponentTag(tag);
      tag.put("value", getWebRequestCycle().getWebRequest().getHttpServletRequest().getServerPort());
      }  
    });
    
    add(new Label("remote_address") {
      @Override
      protected void onComponentTag(ComponentTag tag) {
      super.onComponentTag(tag);
      tag.put("value", getWebRequestCycle().getWebRequest().getHttpServletRequest().getRemoteAddr());
      }  
    });
    
    add(new Label("remote_host") {
      @Override
      protected void onComponentTag(ComponentTag tag) {
      super.onComponentTag(tag);
      tag.put("value", getWebRequestCycle().getWebRequest().getHttpServletRequest().getRemoteHost());
      }  
    });
  }
  
  private void createTitle() {
    add(new TitleHelpPanel("titlePartPanel",
          new ResourceModel("internal.error"), new HelpLinkResourceModel("help.link.startpage")));
  }

}
