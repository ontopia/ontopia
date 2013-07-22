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

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;

import net.ontopia.Ontopia;
import ontopoly.components.TitleHelpPanel;
import ontopoly.models.HelpLinkResourceModel;

import org.apache.wicket.Page;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.ResourceModel;

public class InternalErrorPageWithException extends AbstractOntopolyErrorPage {

  public InternalErrorPageWithException() {
  }
  
  public InternalErrorPageWithException(Page page, final RuntimeException e) {
    super(page == null ? null : page.getPageParameters());
    
    createTitle();
    
    add(new BookmarkablePageLink<Page>("link", StartPage.class));
    
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
    
    HttpServletRequest request = getWebRequestCycle().getWebRequest().getHttpServletRequest();  
    final String serverName = request.getServerName();
    
    add(new Label("server_name") {
      @Override
      protected void onComponentTag(ComponentTag tag) {
      super.onComponentTag(tag);
      tag.put("value", serverName);
      }  
    });
    
    /*add(new Label("server_info") {
      @Override
      protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);
        if(request != null && request.getSession(true) != null && request.getSession(true).getServletContext() != null) {
          tag.put("value", request.getSession(true).getServletContext().getServerInfo()); 
        }     
      }  
    });*/
    
    final String serverPort = Integer.toString(request.getServerPort());
    
    add(new Label("server_port") {
      @Override
      protected void onComponentTag(ComponentTag tag) {
      super.onComponentTag(tag);
      tag.put("value", serverPort);
      }  
    });
    
    final String remoteAddr = request.getRemoteAddr();
    
    add(new Label("remote_address") {
      @Override
      protected void onComponentTag(ComponentTag tag) {
      super.onComponentTag(tag);
      tag.put("value", remoteAddr);
      }  
    });
    
    final String remoteHost = request.getRemoteHost();
    
    add(new Label("remote_host") {
      @Override
      protected void onComponentTag(ComponentTag tag) {
      super.onComponentTag(tag);
      tag.put("value", remoteHost);
      }  
    });
    
    final String stackTrace;
    if (e == null) {
      stackTrace = "unknown";
    } else {
      StringWriter sw = new StringWriter();
      e.printStackTrace(new PrintWriter(sw));
      stackTrace = sw.toString();
    }
    
    add(new Label("stack_trace") {
      @Override
      protected void onComponentTag(ComponentTag tag) {
      super.onComponentTag(tag);
      tag.put("value", stackTrace); 
      }  
    });
    
    add(new Label("error_message") {
      @Override
      protected void onComponentTag(ComponentTag tag) {
      super.onComponentTag(tag);
      tag.put("value", e.getMessage()); 
      }  
    });  
    
    add(new Label("stackTrace", stackTrace));
  }
  
  private void createTitle() {
    add(new TitleHelpPanel("titlePartPanel",
          new ResourceModel("an.error.occurred"), new HelpLinkResourceModel("help.link.startpage")));
  }

}
