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
package ontopoly.jquery;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.model.Model;

public class DraggableBehavior extends JQueryBehavior {

  protected String id;
  
  public DraggableBehavior(String id) {
    this.id = id;
  }
  
  @Override
  protected void onBind() {
    super.onBind();
    Component c = getComponent();
    c.setOutputMarkupId(true);
    c.add(new AttributeAppender("class", new Model<String>("dg_" + id), " "));    
  }
  
  public void renderHead(IHeaderResponse response) {
    super.renderHead(response);
    
    String markupId = getComponent().getMarkupId();
    String selector = "#" + markupId;
    String functionId = "fdg_" + markupId; 

    StringBuffer sb = new StringBuffer();
    sb.append("var ").append(functionId).append(" = function() {\n");
    sb.append("  $(\"").append(selector).append("\").draggable('destroy');\n");
    sb.append("  $(\"").append(selector).append("\").draggable({helper: 'clone'});\n");
    sb.append("};\n");   
    sb.append("$(document).ready(function() {\n");
    sb.append("  ").append(functionId).append("();\n");
    sb.append("});");
    
    response.renderJavascript(sb, "jquery-dg-" + markupId);
    response.renderOnLoadJavascript("Wicket.Ajax.registerPostCallHandler(function(){ " + functionId + "(); });");
  }

  @Override
  protected void respond(AjaxRequestTarget target) {
    // no callbacks expected. these go to the droppable instead.
  }

}
