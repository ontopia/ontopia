/*
 * #!
 * Ontopoly Editor
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */
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
  
  public static void validate(final FormComponent<String> component, final String value) {
    List<IValidator<String>> validators = component.getValidators();
    final IValidatable<String> validatable = new IValidatable<String>() {
      @Override
      public void error(IValidationError error) {
        component.error(error);
      }
      @Override
      public String getValue() {
        return value;
      }
      @Override
      public boolean isValid() {
        return component.isValid();
      }
    };
    
    IValidator<String> validator = null;
    boolean isNull = value == null;

    try {
      Iterator<IValidator<String>> iter = validators.iterator();
      while (iter.hasNext()) {
        validator = iter.next();
        if (isNull == false || validator instanceof INullAcceptingValidator) {
          validator.validate(validatable);
        }
        if (!component.isValid()) {
          break;
        }
      }
    }
    catch (Exception e) {
      throw new WicketRuntimeException("Exception '" + e + "' occurred during validation " +
        validator.getClass().getName() + " on component " + component.getPath(), e);
    }
  }

}
