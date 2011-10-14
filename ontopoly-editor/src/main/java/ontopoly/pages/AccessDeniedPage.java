package ontopoly.pages;

import ontopoly.components.TitleHelpPanel;
import ontopoly.models.HelpLinkResourceModel;
import org.apache.wicket.PageParameters;
import org.apache.wicket.model.ResourceModel;

public class AccessDeniedPage extends OntopolyAbstractPage {

  private TitleHelpPanel titlePartPanel;
  private boolean isOntologyPage;

  public AccessDeniedPage(PageParameters parameters) {
    super(parameters);
    
    this.isOntologyPage = (parameters.get("ontology") != null);
    
    // Adding part containing title and help link
    createTitle();

    // initialize parent components
    initParentComponents();    
  }

  @Override
  protected int getMainMenuIndex() {
    return isOntologyPage ? ONTOLOGY_INDEX_IN_MAINMENU : INSTANCES_PAGE_INDEX_IN_MAINMENU; 
  }

  private void createTitle() {

    // Adding part containing title and help link
    this.titlePartPanel = new TitleHelpPanel("titlePartPanel",
        new ResourceModel("access.denied"),
        new HelpLinkResourceModel("help.link.accessdeniedpage"));
    this.titlePartPanel.setOutputMarkupId(true);
    add(titlePartPanel);
  }

}
