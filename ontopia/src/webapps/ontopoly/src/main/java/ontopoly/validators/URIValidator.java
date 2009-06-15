package ontopoly.validators;

import java.util.Collections;

import net.ontopia.infoset.impl.basic.URILocator;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;

public class URIValidator extends AbstractValidator {

  @Override
  protected void onValidate(IValidatable validatable) {
    String value = (String)validatable.getValue();
    if (value == null) return;
    try {
      new URILocator(value);
    } catch (Exception e) {
      error(validatable, Collections.singletonMap("value", value));
    }
  }

  @Override
  protected String resourceKey() {
    return "validators.URIValidator";
  }
  
}
