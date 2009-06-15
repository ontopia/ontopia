package ontopoly.pages;

import org.apache.wicket.PageParameters;

import ontopoly.components.FooterPanel;
import ontopoly.components.SubHeaderPanel;
import ontopoly.models.TopicMapModel;


public abstract class NonOntopolyAbstractPage extends AbstractProtectedOntopolyPage {

  protected TopicMapModel topicMapModel;
  
  public NonOntopolyAbstractPage() {	  
  }
  
  public NonOntopolyAbstractPage(PageParameters parameters) {
	super(parameters);
	
    this.topicMapModel = new TopicMapModel(parameters.getString("topicMapId"));
    
    add(new SubHeaderPanel("header", topicMapModel));
    add(new FooterPanel("footer"));    
  }

  public TopicMapModel getTopicMapModel() {
    return topicMapModel;
  }
  
}
