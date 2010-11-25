package ontopoly.validators;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;

import ontopoly.components.AbstractFieldInstancePanel;
import ontopoly.models.FieldInstanceModel;

import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;

public abstract class DateFormatValidator extends AbstractValidator<String> {

  private FieldInstanceModel fieldInstanceModel;
  private final Component component;
  
  public DateFormatValidator(Component component, FieldInstanceModel fieldInstanceModel) {
    this.component = component;
    this.fieldInstanceModel = fieldInstanceModel;    
  }
  
  @Override
  protected void onValidate(IValidatable<String> validatable) {
    final String value = validatable.getValue();
    if (value == null) return;
    try {
      DateFormat df = createDateFormat();
      df.parse(value);
    } catch (ParseException e) {
      String message = Application.get().getResourceSettings().getLocalizer().getString(resourceKey(), (Component)null, 
            new Model<Serializable>(new Serializable() {
              @SuppressWarnings("unused")
              public String getValue() {
                return value;
              }
            }));
      component.error(AbstractFieldInstancePanel.createErrorMessage(fieldInstanceModel, new Model<String>(message)));
    }
  }

  public abstract DateFormat createDateFormat();
  
  @Override
  protected String resourceKey() {
    return "validators.DateFormatValidator";
  }
  
}
