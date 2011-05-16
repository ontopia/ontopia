package ontopoly.utils;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.markup.html.IHeaderResponse;

/**
 * Behaviour that uses JavaScript to assign focus to component on page load.
 * 
 * @author grove
 *
 */
public class FocusOnLoadBehaviour extends AbstractBehavior { 
  private Component component; 

  public void bind(Component component) { 
    this.component = component; 
    component.setOutputMarkupId(true); 
  } 

  public void renderHead(IHeaderResponse iHeaderResponse) { 
    super.renderHead(iHeaderResponse); 
    iHeaderResponse.renderOnLoadJavascript("var focusElement = document.getElementById('"+ component.getMarkupId() + "'); if (focusElement != null) focusElement.focus();"); 
  } 
 
  public boolean isTemporary() {
    // remove the behavior after component has been rendered       
    return true;
  }

}
