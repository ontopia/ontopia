package ontopoly.pages;

import ontopoly.OntopolySession;

import org.apache.wicket.PageParameters;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.ResourceModel;

public class SignInPage extends WebPage {
  
  public SignInPage(PageParameters params) {
    add(new Label("title", new ResourceModel("page.title.signin")));
    add(new SignInForm("form"));
  }
  
  private static class SignInForm extends StatelessForm {   
    private String username; 
    private String password; 
    
    public SignInForm(String id) { 
      super(id); 
      setDefaultModel(new CompoundPropertyModel<SignInForm>(this)); 
      add(new TextField("username")); 
      add(new PasswordTextField("password"));   
    } 
    @Override 
    public final void onSubmit() { 
//      System.out.println("U: " + username + " P: " + password);
      if (signIn(username, password)) { 
//        System.out.println("A" + continueToOriginalDestination());
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
