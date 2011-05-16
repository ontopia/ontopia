package ontopoly.validators;

import java.io.Serializable;
import java.util.regex.PatternSyntaxException;

import ontopoly.components.AbstractFieldInstancePanel;
import ontopoly.models.FieldInstanceModel;

import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;

public abstract class RegexValidator extends AbstractValidator<String> {

  protected FieldInstanceModel fieldInstanceModel;
  private final Component component;
  
  public RegexValidator(Component component, FieldInstanceModel fieldInstanceModel) {
    this.component = component;
    this.fieldInstanceModel = fieldInstanceModel;
  }

  @Override
  protected void onValidate(IValidatable<String> validatable) {
    final String value = validatable.getValue();
    final String regex = getRegex();
    
    if (value == null) return;
    try {
      if (value.matches(regex))
        return;
      else
        reportError(resourceKeyInvalidValue(), value, regex);
              
    } catch (PatternSyntaxException e) {
      reportError(resourceKeyInvalidRegex(), value, regex);
    }
  }

  private void reportError(String resourceKey, final String value, final String regex) {
    try {
      String message = Application.get().getResourceSettings().getLocalizer().getString(resourceKey, (Component)null, 
          new Model<Serializable>(new Serializable() {
            @SuppressWarnings("unused")
            public String getValue() {
              return value;
            }
            @SuppressWarnings("unused")
            public String getRegex() {
              return regex;
            }
          }));
      component.error(AbstractFieldInstancePanel.createErrorMessage(fieldInstanceModel, new Model<String>(message)));    
    } catch (Exception e) {
      component.error(AbstractFieldInstancePanel.createErrorMessage(fieldInstanceModel, new Model<String>("Regexp validation error (value='" + value+ "', regex='" + regex + "')")));    
    }
  }

  protected abstract String getRegex();

  protected String resourceKeyInvalidValue() {
    return "validators.RegexValidator";
  }

  protected String resourceKeyInvalidRegex() {
    return "validators.RegexValidator.invalidRegex";
  }
  
}
