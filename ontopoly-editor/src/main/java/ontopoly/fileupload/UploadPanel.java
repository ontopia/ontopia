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

import ontopoly.components.FieldInstanceImageField;
import ontopoly.model.LifeCycleListener;
import ontopoly.pages.AbstractOntopolyPage;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.link.IPageLink;
import org.apache.wicket.markup.html.link.InlineFrame;
import org.apache.wicket.markup.html.panel.Panel;

public class UploadPanel extends Panel {

    protected InlineFrame uploadIFrame;
    protected FieldInstanceImageField parentField;
    
    public UploadPanel(String id, FieldInstanceImageField parentField) {
      super(id);
      this.parentField = parentField;

      // add onUploaded behavior
      final OnUploadedBehavior onUploadBehavior = new OnUploadedBehavior();
      add(onUploadBehavior);
      add(new WebComponent("onUploaded") {
        @Override
        protected void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
          // calling it through setTimeout we ensure that the callback is called
          // in the proper execution context, that is the parent frame
          replaceComponentTagBody(markupStream, openTag,
              "function onUpload_" + UploadPanel.this.getMarkupId() +
              "(uploadedFile, clientFileName) {  window.setTimeout(function() { " + // window.location.reload(true); " +
              onUploadBehavior.getCallback() + "; }, 0 )}");
          } 
      });        
    }
    
    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        
        if (uploadIFrame == null) {
          // the iframe should be attached to a page to be able to get its pagemap,
          // that's why i'm adding it in onBeforeRender
          IPageLink iFrameLink = new IPageLink() {
            @Override
            public Page getPage() {
              return new UploadIFrame(parentField.getFieldValueModel()) {
                @Override
                protected String getOnUploadedCallback() {
                  return "onUpload_" + UploadPanel.this.getMarkupId();
                }
                @Override
                protected LifeCycleListener getLifeCycleListener() {
                  return (AbstractOntopolyPage)UploadPanel.this.getPage();
                }
              };
            }
            @Override
            public Class<? extends Page> getPageIdentity() {
              return UploadIFrame.class;
            }            
        };
        uploadIFrame = new InlineFrame("upload", getPage().getPageMap(), iFrameLink);
        add(uploadIFrame);
      }
    }
    
    private class OnUploadedBehavior extends AbstractDefaultAjaxBehavior {
      
      public String getCallback() {
        // I'm passing the filename using javascript because the iframe is reinstantiated
        // each time from the ipagelink, so it's not really safe to keep state inside it.
        // An alternative could be keeping one only instance of the iframe per panel
        // (although wicket savvis discouraged me about doing so).
        // And I think the iframe setting this panel state won't work either, because only
        // the iframe state will be saved upon upload form submission.
        return generateCallbackScript(
            "wicketAjaxGet('" + getCallbackUrl(false) +
            "&uploadedFile=' + encodeURIComponent(uploadedFile) + '" +
            "&clientFileName=' + encodeURIComponent(clientFileName)").toString();
      }
      
      @Override
      protected void respond(AjaxRequestTarget target) {
        parentField.callOnUpdate(target);
      }
    }

}
