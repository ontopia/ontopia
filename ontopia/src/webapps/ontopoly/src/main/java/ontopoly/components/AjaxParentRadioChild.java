package ontopoly.components;


import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.model.IModel;

/**
 * Subclass of Radio that notifies an AjaxParentFormChoiceComponentUpdatingBehavior when 
 * it is being rendered. This is useful when the Radio is rendered via an AJAX call.
 * @author grove
 * @see AjaxParentFormChoiceComponentUpdatingBehavior
 */
public class AjaxParentRadioChild extends Radio implements IHeaderContributor {

  private AjaxParentFormChoiceComponentUpdatingBehavior apfc;
  
  public AjaxParentRadioChild(String id, IModel model, AjaxParentFormChoiceComponentUpdatingBehavior apfc) {
    super(id, model);
    setOutputMarkupId(true);
    this.apfc = apfc;
  }
  public void renderHead(IHeaderResponse response) {    
    response.renderOnLoadJavascript("attachChoiceHandler('" + getMarkupId() +
        "', function() {" + apfc.getCallbackFunction() + "});");    
  }

}
