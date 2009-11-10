package ontopoly.validators;

import java.util.Collections;

import net.ontopia.infoset.impl.basic.URILocator;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;

public class URIValidator extends AbstractValidator<String> {

  @Override
  protected void onValidate(IValidatable<String> validatable) {
    String value = validatable.getValue();
    if (value == null) return;
    try {
      new URILocator(value);
    } catch (Exception e) {
      error(validatable, Collections.singletonMap("value", (Object)value));
    }
  }

  @Override
  protected String resourceKey() {
    return "validators.URIValidator";
  }
  
}
