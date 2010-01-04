package ontopoly.components;

import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.utils.ObjectUtils;
import ontopoly.model.FieldInstance;
import ontopoly.model.OntopolyModelRuntimeException;
import ontopoly.models.FieldValueModel;
import ontopoly.pages.AbstractOntopolyPage;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;

public class FieldInstanceTextField extends TextField<String> {

  private FieldValueModel fieldValueModel;
  private String oldValue;
  private String cols = "50";
  
  public FieldInstanceTextField(String id, FieldValueModel fieldValueModel) {
    super(id);
    this.fieldValueModel = fieldValueModel;

    if (!fieldValueModel.isExistingValue()) {
      this.oldValue = null;
    } else {
      Object value = fieldValueModel.getObject();
      if (value instanceof TopicNameIF) {
        TopicNameIF name = (TopicNameIF)value;
        this.oldValue = name.getValue();
      } else if (value instanceof OccurrenceIF) {
        OccurrenceIF occ = (OccurrenceIF)value;
        this.oldValue = occ.getValue();
      } else {
        throw new RuntimeException("Unsupported field value: " + value);
      }
    }
    setModel(new Model<String>(oldValue));
  }

  public void setCols(int cols) {
    this.cols = Integer.toString(cols);
  }
  
  @Override
  protected void onComponentTag(ComponentTag tag) {
    tag.setName("input");
    tag.put("type", "text");
    tag.put("size", cols);
    super.onComponentTag(tag);
  }
  
  @Override
  protected void onModelChanged() {
    super.onModelChanged();
    try {
      String newValue = (String)getModelObject();
      if (ObjectUtils.equals(newValue, oldValue)) return;
      AbstractOntopolyPage page = (AbstractOntopolyPage)getPage();
      FieldInstance fieldInstance = fieldValueModel.getFieldInstanceModel().getFieldInstance();
      if (fieldValueModel.isExistingValue() && oldValue != null)
        fieldInstance.removeValue(oldValue, page.getListener());
      if (newValue != null && !newValue.equals("")) {
        fieldInstance.addValue(newValue, page.getListener());
        fieldValueModel.setExistingValue(newValue);
      }
      oldValue = newValue;
    } catch (OntopolyModelRuntimeException e) {
      error(AbstractFieldInstancePanel.createErrorMessage(fieldValueModel.getFieldInstanceModel(), e.getMessage()));
    }
  }
  
}
