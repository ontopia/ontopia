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

package ontopoly.fileupload;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;

import net.ontopia.net.Base64;
import net.ontopia.utils.StreamUtils;
import ontopoly.model.FieldInstance;
import ontopoly.model.LifeCycleListener;
import ontopoly.models.FieldValueModel;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;

public abstract class UploadIFrame extends WebPage {
    
  private boolean uploaded = false;
  private FileUploadField uploadField;
  private FieldValueModel fieldValueModel;
  
  public UploadIFrame(FieldValueModel fieldValueModel) {
    this.fieldValueModel = fieldValueModel;

    // add header contributor for stylesheet
    add(CSSPackageResource.getHeaderContribution(getStylesheet()));
    
    WebMarkupContainer container = new WebMarkupContainer("container");
    container.setOutputMarkupId(true);
    add(container);
    // add form
    container.add(new UploadForm("form", container));
    // add onUploaded method
    container.add(new WebComponent("onUploaded") {
      @Override
      protected void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
        if (uploaded) {
          replaceComponentTagBody(markupStream, openTag,
              "window.parent." + getOnUploadedCallback() + "('', '')");
          uploaded = false;
        }
      }            
    });
  }

  protected String getStylesheet() {
    return "styles/stylesheet.css";
  }
    
  protected abstract String getOnUploadedCallback();

  private class UploadForm extends Form<Object> {

    public UploadForm(String id, final WebMarkupContainer container) {
      super(id);
      uploadField = new FileUploadField("file");
      add(uploadField);
    }
    
    @Override
    public void onSubmit() {
      FileUpload upload = uploadField.getFileUpload();         
      if (upload != null) {
        try {
          Reader input = new InputStreamReader(new Base64.InputStream(upload.getInputStream(), Base64.ENCODE), "utf-8");
          FieldInstance fieldInstance = fieldValueModel.getFieldInstanceModel().getFieldInstance();
          StringWriter swriter = new StringWriter();
          StreamUtils.transfer(input, swriter);
          String value = swriter.toString();
          fieldInstance.addValue(value, getLifeCycleListener());
          fieldValueModel.setExistingValue(value);
          uploaded = true;
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
      
  }

  protected abstract LifeCycleListener getLifeCycleListener();
  
}
