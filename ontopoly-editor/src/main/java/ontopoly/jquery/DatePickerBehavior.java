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
  
  @Override
  public void renderHead(IHeaderResponse response) {
    super.renderHead(response);
    
    String markupId = getComponent().getMarkupId();
    String selector = "#" + markupId;
    String functionId = "fdp_" + markupId; 
    
    StringBuilder sb = new StringBuilder();
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
