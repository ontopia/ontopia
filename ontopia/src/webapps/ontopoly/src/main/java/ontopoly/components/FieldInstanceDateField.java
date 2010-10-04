package ontopoly.components;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.utils.ObjectUtils;
import ontopoly.jquery.DatePickerBehavior;
import ontopoly.model.FieldInstanceIF;
import ontopoly.models.FieldValueModel;
import ontopoly.pages.AbstractOntopolyPage;
import ontopoly.validators.DateFormatValidator;
import ontopoly.validators.ExternalValidation;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.AbstractTextComponent.ITextFormatProvider;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.IValidatable;

public class FieldInstanceDateField extends TextField<String> implements ITextFormatProvider, IValidatable {

  private FieldValueModel fieldValueModel;
  private String oldValue;
  private String cols = "10";
  
  public FieldInstanceDateField(String id, FieldValueModel fieldValueModel) {
    super(id);
    this.fieldValueModel = fieldValueModel;
    
    OccurrenceIF occ = (OccurrenceIF)fieldValueModel.getObject();
    this.oldValue = (occ == null ? null : occ.getValue());
    setModel(new Model<String>(oldValue));
    
    add(new DatePickerBehavior("yy-mm-dd"));
    add(new DateFormatValidator(this, fieldValueModel.getFieldInstanceModel()) {
      @Override
      public DateFormat createDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd");
      }
      @Override
      protected String resourceKey() {
        return super.resourceKey() + ".date";
      }
    });

    // validate field using registered validators
    ExternalValidation.validate(this, oldValue);
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
  
  public String getTextFormat() {
    return "yyyy-MM-dd";
  }

  @Override
  protected void onModelChanged() {
    super.onModelChanged();
    String newValue = (String)getModelObject();
    if (ObjectUtils.equals(newValue, oldValue)) return;
    AbstractOntopolyPage page = (AbstractOntopolyPage)getPage();
    FieldInstanceIF fieldInstance = fieldValueModel.getFieldInstanceModel().getFieldInstance();
    if (fieldValueModel.isExistingValue() && oldValue != null)
      fieldInstance.removeValue(oldValue, page.getListener());
    if (newValue != null && !newValue.equals("")) {
      fieldInstance.addValue(newValue, page.getListener());
      fieldValueModel.setExistingValue(newValue);
    }
    oldValue = newValue;
  }
  
}
