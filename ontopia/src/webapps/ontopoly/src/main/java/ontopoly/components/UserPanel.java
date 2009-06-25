package ontopoly.components;

import ontopoly.OntopolySession;
import ontopoly.pages.SignInPage;
import ontopoly.pages.SignOutPage;

import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

public class UserPanel extends Panel { 
  public UserPanel(String id, Class<? extends Page> logoutPageClass) { 
    super(id);
    add(new Label("fullname", new PropertyModel(this, "session.user.fullname"))); 
    PageParameters parameters = new PageParameters(); 
    parameters.add(SignOutPage.REDIRECTPAGE_PARAM, logoutPageClass.getName()); 
    add(new BookmarkablePageLink("signout", SignOutPage.class, parameters) { 
      @Override 
      public boolean isVisible() { 
        OntopolySession session = (OntopolySession)Session.get();
        return session.isAuthenticated(); 
      }
      @Override
      public boolean isEnabled() {
        OntopolySession session = (OntopolySession)Session.get();
        return !session.isAutoLogin();
      }
    }); 
    add(new Link("signin") { 
      @Override 
      public void onClick() { 
        throw new RestartResponseAtInterceptPageException(SignInPage.class); 
      } 
      @Override 
      public boolean isVisible() {
        OntopolySession session = (OntopolySession)Session.get();
        return !session.isAuthenticated(); 
      } 
    }); 
  }
  @Override
  public boolean isVisible() {
    OntopolySession session = (OntopolySession)Session.get();
    return session.isAccessStrategyEnabled();     
  }
  
}
