package ontopoly.components;

import ontopoly.pages.SignInPage;

import org.apache.wicket.markup.html.panel.Panel;

public class HeaderPanel extends Panel {
  public HeaderPanel(String id) {
    super(id);
    
    add(new UserPanel("userPanel", SignInPage.class));
    // add(new Image("indicator", AbstractDefaultAjaxBehavior.INDICATOR).setOutputMarkupId(true)); 
  }
}
