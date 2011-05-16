package ontopoly.pages;

import ontopoly.components.FooterPanel;
import ontopoly.components.HeaderPanel;

import org.apache.wicket.PageParameters;

public abstract class AbstractOntopolyErrorPage extends AbstractOntopolyPage {
  
  public AbstractOntopolyErrorPage() {
  }
  
  public AbstractOntopolyErrorPage(PageParameters parameters) {
    super(parameters);
    add(new HeaderPanel("header"));
    add(new FooterPanel("footer"));    	  
  }
  
  public boolean isErrorPage() {
    return true;
  }

}
