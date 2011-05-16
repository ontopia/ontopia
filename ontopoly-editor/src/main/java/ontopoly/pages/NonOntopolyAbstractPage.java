package ontopoly.pages;

import ontopoly.components.FooterPanel;
import ontopoly.components.HeaderPanel;
import ontopoly.models.TopicMapModel;

import org.apache.wicket.PageParameters;


public abstract class NonOntopolyAbstractPage extends AbstractProtectedOntopolyPage {

  protected TopicMapModel topicMapModel;
  
  public NonOntopolyAbstractPage() {	  
  }
  
  public NonOntopolyAbstractPage(PageParameters parameters) {
	super(parameters);
	
    this.topicMapModel = new TopicMapModel(parameters.getString("topicMapId"));
    
    add(new HeaderPanel("header"));
    add(new FooterPanel("footer"));    
  }

  public TopicMapModel getTopicMapModel() {
    return topicMapModel;
  }
  
}
