package ontopoly.jquery;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderResponse;

public class DatePickerBehavior extends JQueryBehavior {
  
  protected String dateFormat;
  
  public DatePickerBehavior(String dateFormat) {
    this.dateFormat = dateFormat;
  }
  
  @Override
  protected void onBind() {
    super.onBind();
    Component c = getComponent();
    c.setOutputMarkupId(true);
//    c.add(new AttributeAppender("onfocus", new Model("$(this).datepicker('enable')"), ";"));    
//    c.add(new AttributeAppender("onblur", new Model("$(this).datepicker('disable')"), ";"));    
//    c.add(new AttributeAppender("class", new Model("datePicker"), " "));    
  }
  
  public void renderHead(IHeaderResponse response) {
    super.renderHead(response);
    
    String markupId = getComponent().getMarkupId();
    String selector = "#" + markupId;
    String functionId = "fdp_" + markupId; 
    
    StringBuffer sb = new StringBuffer();
    sb.append("var ").append(functionId).append(" = function() {\n");
    sb.append("  $(\"").append(selector).append("\").datepicker('destroy');\n");
    sb.append("  $(\"").append(selector).append("\").datepicker({firstDay: 1, dateFormat: '")
    .append(dateFormat).append("', yearRange: '1750:2100', showOn: 'both', buttonImageOnly: true, buttonImage: '")
    .append(getDateIconURL()).append("' });\n");
    sb.append("};\n");
    sb.append("$(document).ready(function() {\n");
    sb.append("  ").append(functionId).append("();\n");
    sb.append("});");

    response.renderJavascript(sb, "jquery-dp-" + markupId);
    response.renderOnLoadJavascript("Wicket.Ajax.registerPostCallHandler(function(){ " + functionId + "(); });");
  }

  protected String getDateIconURL() {
    return "/ontopoly/images/datepicker.gif";
//    PackageResource icon = PackageResource.get(ImageResource.class, "datepicker.gif");
//    return "/" + icon.getAbsolutePath();
  }

  @Override
  protected void respond(AjaxRequestTarget target) {
    // no callbacks expected. these go to the droppable instead.
  }

}
