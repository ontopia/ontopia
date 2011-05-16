package ontopoly.components;


import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.model.IModel;

/**
 * Subclass of Check that notifies an AjaxParentFormChoiceComponentUpdatingBehavior when 
 * it is being rendered. This is useful when the Check is rendered via an AJAX call.
 * @author grove
 * @see AjaxParentFormChoiceComponentUpdatingBehavior
 */
public class AjaxParentCheckChild extends Check<String> implements IHeaderContributor {

  private AjaxParentFormChoiceComponentUpdatingBehavior apfc;
  
  public AjaxParentCheckChild(String id, IModel<String> model, AjaxParentFormChoiceComponentUpdatingBehavior apfc) {
    super(id, model);
    setOutputMarkupId(true);
    this.apfc = apfc;
  }
  public void renderHead(IHeaderResponse response) {    
    response.renderOnLoadJavascript("attachChoiceHandler('" + getMarkupId() +
        "', function() {" + apfc.getCallbackFunction() + "});");    
  }

}
