package ontopoly.validators;

import java.util.Iterator;
import java.util.List;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.validation.INullAcceptingValidator;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidationError;
import org.apache.wicket.validation.IValidator;

public class ExternalValidation {
  
  public static void validate(final FormComponent component, final String value) {
    List validators = component.getValidators();
    final IValidatable validatable = new IValidatable() {
      public void error(IValidationError error) {
        component.error(error);
      }
      public Object getValue() {
        return value;
      }
      public boolean isValid() {
        return component.isValid();
      }
    };
    
    IValidator validator = null;
    boolean isNull = value == null;

    try {
      Iterator iter = validators.iterator();
      while (iter.hasNext()) {
        validator = (IValidator)iter.next();
        if (isNull == false || validator instanceof INullAcceptingValidator)
          validator.validate(validatable);
        if (!component.isValid())
          break;
      }
    }
    catch (Exception e) {
      throw new WicketRuntimeException("Exception '" + e + "' occurred during validation " +
        validator.getClass().getName() + " on component " + component.getPath(), e);
    }
  }

}
