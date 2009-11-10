package ontopoly.validators;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;

public class DateTimeValidator extends AbstractValidator<String> {

  private static final DateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  
  @Override
  protected void onValidate(IValidatable<String> validatable) {
    String value = validatable.getValue();
    if (value == null) return;
    try {
      synchronized (FORMAT) {
        FORMAT.parse(value);
      }
    } catch (ParseException e) {
      error(validatable, Collections.singletonMap("value", (Object)value));
    }
  }

  @Override
  protected String resourceKey() {
    return "validators.DateTimeValidator";
  }
  
}
