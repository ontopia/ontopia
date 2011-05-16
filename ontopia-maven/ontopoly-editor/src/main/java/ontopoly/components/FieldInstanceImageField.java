package ontopoly.components;

import net.ontopia.topicmaps.core.OccurrenceIF;
import ontopoly.fileupload.UploadPanel;
import ontopoly.model.TopicMap;
import ontopoly.models.FieldValueModel;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;


public class FieldInstanceImageField extends Panel {

  protected FieldValueModel fieldValueModel;
  protected Component image;
  protected Component upload;
  
  public FieldInstanceImageField(String id, FieldValueModel _fieldValueModel, boolean readonly) {
    super(id);
    this.fieldValueModel = _fieldValueModel;
    
    image = new Image("image");
    image.add(new AttributeModifier("src", true, new AbstractReadOnlyModel<String>() {
      @Override
      public final String getObject() {
        TopicMap topicMap = fieldValueModel.getFieldInstanceModel().getFieldInstance().getInstance().getTopicMap();        
        Object o = fieldValueModel.getFieldValue();
        return getRequest().getRelativePathPrefixToContextRoot() + "occurrenceImages?topicMapId=" + topicMap.getId() + 
        "&occurrenceId=" + ((o instanceof OccurrenceIF ? ((OccurrenceIF)o).getObjectId(): "unknown"));
      }
    }));
    upload = new UploadPanel("upload", this);
    add(image);
    add(upload);
    if (fieldValueModel.isExistingValue()) {
      upload.setVisible(false);
    } else {
      image.setVisible(false);
      if (readonly)
        upload.setVisible(false);
    }
  }
  
  public FieldValueModel getFieldValueModel() {
    return fieldValueModel;
  }
  
  public void callOnUpdate(AjaxRequestTarget target) {
    if (fieldValueModel.isExistingValue()) {
      upload.setVisible(false);
      image.setVisible(true);
    }
  }

}
