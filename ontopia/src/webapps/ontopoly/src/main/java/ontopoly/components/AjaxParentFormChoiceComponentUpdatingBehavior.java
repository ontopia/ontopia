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
