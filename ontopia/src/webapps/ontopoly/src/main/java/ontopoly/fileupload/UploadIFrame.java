
package ontopoly.fileupload;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;

import net.ontopia.net.Base64;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.FieldInstance;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.LifeCycleListener;
import net.ontopia.utils.StreamUtils;
import ontopoly.models.FieldValueModel;

import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
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
    add(HeaderContributor.forCss(getStylesheet()));
    
    WebMarkupContainer container = new WebMarkupContainer("container");
    container.setOutputMarkupId(true);
    add(container);
    // add form
    container.add(new UploadForm("form", container));
    // add onUploaded method
    container.add(new WebComponent("onUploaded") {
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

  private class UploadForm extends Form {

    public UploadForm(String id, final WebMarkupContainer container) {
      super(id);
      uploadField = new FileUploadField("file");
      add(uploadField);
    }
    
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
        } catch (IOException exception) {
            exception.printStackTrace();
        }
      }
    }
      
  }

  protected abstract LifeCycleListener getLifeCycleListener();
  
}
