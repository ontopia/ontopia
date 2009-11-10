package ontopoly.components;

import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.TopicMap;
import ontopoly.pages.TopicTypesPage;

import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;


public class NavigationLinksPanel extends Panel {

  private static final long serialVersionUID = -4144156475956872763L;

  public NavigationLinksPanel(String id, IModel model, Class<? extends Page> linkDestination,
      IModel typeLinkLabelModel, IModel typeConfigLabelModel) {
    super(id);

    add(new BookmarkablePageLink<Page>("ontopolyLink", TopicTypesPage.class,
        new PageParameters("topicMapId="
            + ((TopicMap) model.getObject()).getId())));

    BookmarkablePageLink typeLink = new BookmarkablePageLink<Page>("typeLink",
        linkDestination, new PageParameters("topicMapId="
            + ((TopicMap) model.getObject()).getId()));
    add(typeLink);
    typeLink.add(new Label("typeLinkLabel", typeLinkLabelModel));

    add(new Label("config", typeConfigLabelModel));
  }
}
