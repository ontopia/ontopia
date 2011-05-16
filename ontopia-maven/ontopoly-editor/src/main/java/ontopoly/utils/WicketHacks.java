package ontopoly.utils;

import org.apache.wicket.ajax.AjaxRequestTarget;

public final class WicketHacks {

  public static void disableWindowUnloadConfirmation(AjaxRequestTarget target) {
    // HACK: to prevent confirmation popup on IE and FF. The extra native 
    // confirmation box occurs when the page redirects to another page when 
    // the modal window closes.
    target.appendJavascript("Wicket.Window.unloadConfirmation = false;");
  }

}
