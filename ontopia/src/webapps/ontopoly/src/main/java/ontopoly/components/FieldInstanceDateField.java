package ontopoly.components;

import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.FieldInstance;
import net.ontopia.utils.ObjectUtils;
import ontopoly.jquery.DatePickerBehavior;
import ontopoly.models.FieldValueModel;
import ontopoly.pages.AbstractOntopolyPage;
import ontopoly.validators.DateValidator;
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
    add(new DateValidator());
    //add(new PatternValidator("^((((19|20)(([02468][048])|([13579][26]))-02-29))|((20[0-9][0-9])|(19[0-9][0-9]))-((((0[1-9])|(1[0-2]))-((0[1-9])|(1\\d)|(2[0-8])))|((((0[13578])|(1[02]))-31)|(((0[1,3-9])|(1[0-2]))-(29|30)))))$"));

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
    FieldInstance fieldInstance = fieldValueModel.getFieldInstanceModel().getFieldInstance();
    if (fieldValueModel.isExistingValue() && oldValue != null)
      fieldInstance.removeValue(oldValue, page.getListener());
    if (newValue != null && !newValue.equals("")) {
      fieldInstance.addValue(newValue, page.getListener());
      fieldValueModel.setExistingValue(newValue);
    }
    oldValue = newValue;
  }
  
}
