package ontopoly.pages;

import ontopoly.components.OntopolyBookmarkablePageLink;
import ontopoly.components.TitleHelpPanel;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.ResourceModel;

public class PageExpiredErrorPage extends AbstractOntopolyErrorPage {

  public PageExpiredErrorPage() {	  
  }
  
  public PageExpiredErrorPage(PageParameters parameters) {
    this(null, parameters);
  }
  
  public PageExpiredErrorPage(final Class previousPage, PageParameters parameters) {	
    super(parameters);
    
    createTitle();
    
    add(new OntopolyBookmarkablePageLink("startPage", StartPage.class, new ResourceModel("topic.map.index.page").getObject().toString()));
    
    // The bookmarkablePageLink class demands that the page argument is not equal to null, so
    // it has to be set to a concrete page if previousPage is null.
    Class page = StartPage.class;
    if(previousPage != null) {
      page = previousPage;
    }
    
    WebMarkupContainer previousPageLinkContainer = new WebMarkupContainer("previousPageLinkContainer") {
      public boolean isVisible() {
        return previousPage != null ? true : false;
      }  
    };
    previousPageLinkContainer.setOutputMarkupPlaceholderTag(true);
    add(previousPageLinkContainer);
    
    String label = "previous page";
    previousPageLinkContainer.add(new OntopolyBookmarkablePageLink("previousPage", page, parameters, label));
  }
  
  private void createTitle() {
    add(new TitleHelpPanel("titlePartPanel", 
          new ResourceModel("page.expired.title"), new ResourceModel("help.link.startpage")));
  }
  
}
