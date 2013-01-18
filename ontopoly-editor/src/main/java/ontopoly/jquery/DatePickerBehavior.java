package ontopoly.jquery;

import ontopoly.images.ImageResource;

import org.apache.wicket.Component;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.util.value.ValueMap;

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
    .append(getDateIconURL())
    .append("', onSelect: function(d, i) { $('").append(selector).append("').trigger('blur').change(); $.datepicker._hideDatepicker(); }")
    .append(" });\n");
    sb.append("};\n");
    sb.append("$(document).ready(function() {\n");
    sb.append("  ").append(functionId).append("();\n");
    sb.append("});");
    
    response.renderJavascript(sb, "jquery-dp-" + markupId);
    response.renderOnLoadJavascript("Wicket.Ajax.registerPostCallHandler(function(){ " + functionId + "(); });");
  }

  protected CharSequence getDateIconURL() {
    return RequestCycle.get().urlFor(new ResourceReference(ImageResource.class, "datepicker.gif"), ValueMap.EMPTY_MAP);
  }

  @Override
  protected void respond(AjaxRequestTarget target) {
    // no callbacks expected. these go to the droppable instead.
  }

}
