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
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Component.IVisitor;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.model.Model;

public abstract class DroppableBehavior extends JQueryBehavior {

  protected String id;
  
  public DroppableBehavior(String id) {
    this.id = id;
  }
  
  @Override
  protected void onBind() {
    super.onBind();
    Component c = getComponent();
    c.setOutputMarkupId(true);
    c.add(new AttributeAppender("class", new Model<String>("do_" + id), " "));    
  }
  
  @Override
  public void renderHead(IHeaderResponse response) {
    super.renderHead(response);
    
    String markupId = getComponent().getMarkupId();
    String selector = "#" + markupId;
    String functionId = "fdo_" + markupId; 

    StringBuilder sb = new StringBuilder();
    sb.append("var ").append(functionId).append(" = function() {\n");
    sb.append("  $(\"").append(selector).append("\").droppable('destroy');\n");
    sb.append("  $(\"").append(selector).append("\").droppable({\n");
    sb.append("    accept: \".dg_").append(id).append("\", activeClass: 'droppable-active', hoverClass: 'droppable-hover',\n");
    sb.append("    drop: function(ev, ui) { wicketAjaxGet('").append((String)getCallbackUrl()).append("&id=' + $(ui.draggable).attr('id')); }\n");
    sb.append("  })\n");
    sb.append("};\n");   
    sb.append("$(document).ready(function() {\n");
    sb.append("  ").append(functionId).append("();\n");
		sb.append("});");
		
    response.renderJavascript(sb, "jquery-do-" + markupId);
    response.renderOnLoadJavascript("Wicket.Ajax.registerPostCallHandler(function(){ " + functionId + "(); });");
  }

  @Override
  protected void respond(AjaxRequestTarget target) {
    final String draggableId = getComponent().getRequest().getParameter("id");
    // find component with the markup id given in the 'id' request parameter
    MarkupIdVisitor visitor = new MarkupIdVisitor(draggableId);
//    System.out.println("R: " + getComponent() + " " + getComponent().getParent()+ " " + getComponent().getParent().getParent());
//    getComponent().getPage().visitChildren(visitor);
    getDropContainer().visitChildren(visitor);
    onDrop(visitor.getFoundComponent(), target);
  }

  protected abstract void onDrop(Component component, AjaxRequestTarget target);

  /**
   * Should return the container that contains all the droppable 
   * targets. This will then be used to search for the appropriate 
   * target.
   */
  protected abstract MarkupContainer getDropContainer();
  
  private static class MarkupIdVisitor implements IVisitor<Component> {
    private final String id;
    private Component found;

    public MarkupIdVisitor(String id) {
      this.id = id;
    }

    @Override
    public Object component(Component component) {
      if (component.getMarkupId().equals(id)) {
        this.found = component;
        return IVisitor.STOP_TRAVERSAL;
      } else  if (component instanceof MarkupContainer) {
        return ((MarkupContainer)component).visitChildren(this);
      } else {
        return IVisitor.CONTINUE_TRAVERSAL;
      }
    }
    
    public Component getFoundComponent() {
      return found;
    }
  }

}
