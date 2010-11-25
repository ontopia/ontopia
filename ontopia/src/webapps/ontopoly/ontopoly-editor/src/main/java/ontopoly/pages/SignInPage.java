package ontopoly.pages;

import ontopoly.OntopolySession;
import ontopoly.components.FooterPanel;
import ontopoly.components.StartPageHeaderPanel;

import org.apache.wicket.PageParameters;
import org.apache.wicket.Session;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.ResourceModel;

public class SignInPage extends AbstractOntopolyPage {
  
  public SignInPage(PageParameters params) {
	super(params);
	    
	add(new StartPageHeaderPanel("header"));
	add(new FooterPanel("footer"));

    add(new Label("title", new ResourceModel("page.title.signin")));

    add(new Label("message", new AbstractReadOnlyModel<String>() {
        @Override
        public String getObject() {
          OntopolySession session = (OntopolySession)Session.findOrCreate();
          return session.getSignInMessage();
        }
      }));
    add(new SignInForm("form"));
  }
  
  private static class SignInForm extends StatelessForm<Object> {   
    private String username; 
    private String password; 
    
    public SignInForm(String id) { 
      super(id); 
      setDefaultModel(new CompoundPropertyModel<SignInForm>(this)); 
      
      add(new FeedbackPanel("feedback", new ContainerFeedbackMessageFilter(this)));
      
      add(new TextField<String>("username")); 
      add(new PasswordTextField("password"));   
    } 
    @Override 
    public final void onSubmit() { 
      if (signIn(username, password)) { 
        if (!continueToOriginalDestination()) { 
          setResponsePage(getApplication().getHomePage()); 
        } 
      } else { 
        error("Unknown username/ password"); 
      } 
    } 
    private boolean signIn(String username, String password) {
      OntopolySession session = (OntopolySession)Session.findOrCreate();
      return session.authenticate(username, password);
    } 
  }

} 
