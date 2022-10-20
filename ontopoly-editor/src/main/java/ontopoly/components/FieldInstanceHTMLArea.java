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

import java.util.Objects;
import net.ontopia.topicmaps.core.OccurrenceIF;
import ontopoly.model.FieldInstance;
import ontopoly.models.FieldValueModel;
import ontopoly.pages.AbstractOntopolyPage;
import ontopoly.resources.Resources;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

public class FieldInstanceHTMLArea extends Panel implements IHeaderContributor {

  private ResourceReference reference = new ResourceReference(Resources.class, "tiny_mce/tiny_mce.js");

  protected final FieldValueModel fieldValueModel;
  protected String oldValue;

  protected String cols = "70";
  protected String rows = "20";

  protected TextArea<String> textArea;
  
  public FieldInstanceHTMLArea(String id, FieldValueModel _fieldValueModel) {
    super(id);
    this.fieldValueModel = _fieldValueModel;
    
    OccurrenceIF occ = (OccurrenceIF)fieldValueModel.getObject();
    this.oldValue = (occ == null ? null : occ.getValue());
    setDefaultModel(new Model<String>(oldValue));
    
    this.textArea = new TextArea<String>("field", new Model<String>(oldValue)) {      
      @Override
      protected void onComponentTag(ComponentTag tag) {
        tag.setName("textarea");
        tag.put("cols", cols);
        tag.put("rows", rows);
        tag.put("class", getMarkupId());
        super.onComponentTag(tag);
      }
      
      @Override
      protected void onModelChanged() {
        super.onModelChanged();
        String newValue = (String)getModelObject();
        if (Objects.equals(newValue, oldValue)) {
          return;
        }
        AbstractOntopolyPage page = (AbstractOntopolyPage)getPage();
        FieldInstance fieldInstance = fieldValueModel.getFieldInstanceModel().getFieldInstance();
        if (fieldValueModel.isExistingValue() && oldValue != null) {
          fieldInstance.removeValue(oldValue, page.getListener());
        }
        if (newValue != null && !newValue.equals("")) {
          fieldInstance.addValue(newValue, page.getListener());
          fieldValueModel.setExistingValue(newValue);
        }
        oldValue = newValue;
      }      
    };
    textArea.setOutputMarkupId(true);
    add(textArea);
    
    add(new WebComponent("fieldScript") {
      @Override
      protected void onComponentTagBody(final MarkupStream markupStream, final ComponentTag openTag) {
        StringBuilder sb = new StringBuilder();
        //sb.append("\ntinyMCE.onLoad();");
        sb.append("\ntinyMCE.execCommand('mceAddControl', true, '" + textArea.getMarkupId() + "');");

        // the next two lines are a work-around for issue 459
        // https://github.com/ontopia/ontopia/issues/459
        // motivated by this posting
        // http://apache-wicket.1842946.n4.nabble.com/tinymce-textarea-in-a-modal-window-not-letting-to-type-td1886534.html
        sb.append("\ntinyMCE.execCommand('mceRemoveControl', false, '" + textArea.getMarkupId() + "');");
        sb.append("\ntinyMCE.execCommand('mceAddControl', true, '" + textArea.getMarkupId() + "');");
        // end of workaround
        
        replaceComponentTagBody(markupStream, openTag, sb.toString());
      }
    });
  }

  @Override
  public void renderHead(IHeaderResponse response) {
    // import script    
    response.renderJavascriptReference(reference);
    // initializer script
    StringBuilder sb = new StringBuilder();
    sb.append("function onchangeTinyMCE(inst) {\n");
    sb.append("  if (inst.isDirty()) {\n");
    sb.append("    var value = inst.getBody().innerHTML;\n");
    sb.append("    var textArea = document.getElementById(inst.editorId);\n");
    //sb.append("  alert('TA: ' + textArea);\n");
    sb.append("    if (textArea != null && textArea.value != value) {\n");
    //sb.append("      alert('V: ' + value + ' ' + textArea.value);\n");
    sb.append("      textArea.value = value;\n");
    sb.append("      textArea.onchange();\n");
    sb.append("    }\n");    
    sb.append("  }\n");    
    sb.append("}\n");
    sb.append("tinyMCE.init({\n");
    sb.append("mode : \"none\", ");
    sb.append("theme : \"advanced\", ");
    sb.append("plugins : \"save\", ");
    sb.append("theme_advanced_buttons3_add : \"save\", ");
    //sb.append("onchange_callback : onchangeTinyMCE");
    sb.append("save_enablewhendirty : true,");    
    sb.append("save_onsavecallback : onchangeTinyMCE");
    sb.append("\n});\n");
    response.renderJavascript(sb.toString(), "init");
  }

  public TextArea<String> getTextArea() {
    return textArea;    
  }
  
  public void setCols(int cols) {
    this.cols = Integer.toString(cols);
  }

  public void setRows(int rows) {
    this.rows = Integer.toString(rows);
  }

}
