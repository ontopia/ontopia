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
