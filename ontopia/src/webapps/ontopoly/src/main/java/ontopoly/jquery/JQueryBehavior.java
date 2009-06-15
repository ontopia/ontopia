package ontopoly.jquery;

import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.resources.JavascriptResourceReference;

public abstract class JQueryBehavior extends AbstractDefaultAjaxBehavior {

  public static final JavascriptResourceReference JS_JQUERY = new JavascriptResourceReference(JQueryBehavior.class, "jquery.js"); 
  public static final JavascriptResourceReference JS_JQUERY_UI = new JavascriptResourceReference(JQueryBehavior.class, "jquery.ui.js"); 
  
  public void renderHead(IHeaderResponse response) {
    super.renderHead(response);
    response.renderJavascriptReference(JS_JQUERY);
    response.renderJavascriptReference(JS_JQUERY_UI);
  }
  
}
