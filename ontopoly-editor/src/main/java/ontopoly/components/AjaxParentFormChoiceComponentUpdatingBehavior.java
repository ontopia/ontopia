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
package ontopoly.components;

import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.util.string.AppendingStringBuffer;

/**
 * This is variant of AjaxFormChoiceComponentUpdatingBehavior that allows 
 * nested AjaxParentRadioChild and AjaxParentCheckChild instances to update 
 * the parent RadioGroup or CheckGroup. This is neccessary when any 
 * children of the group gets replaced using AJAX. This is done to work 
 * around a limitation of the AjaxFormChoiceComponentUpdatingBehavior 
 * class, which does not allow form choice children components to be 
 * replaced/added via AJAX.
 * @author grove
 * @see AjaxParentRadioChild and AjaxParentCheckChild
 */
public abstract class AjaxParentFormChoiceComponentUpdatingBehavior extends
    org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior {

  @Override
  public void renderHead(IHeaderResponse response) {
    super.renderHead(response);

    AppendingStringBuffer asb = new AppendingStringBuffer();
    asb.append("function attachChoiceHandler(markupId, callbackScript) {\n");
    asb.append(" var inputNode = wicketGet(markupId);\n");
    asb.append(" var inputType = inputNode.type.toLowerCase();\n");
    asb.append(" if (inputType == 'checkbox' || inputType == 'radio') {\n");
    asb.append(" Wicket.Event.add(inputNode, 'click', callbackScript);\n");
    asb.append(" }\n");
    asb.append("}\n");

    response.renderJavascript(asb, "attachChoiceParent");
  }

  public CharSequence getCallbackFunction() {
    return getEventHandler();
  }
  
}
