package ontopoly.components;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

public abstract class ContextMenuPanel extends Panel implements IHeaderContributor {

  private ResourceReference jsReference = new ResourceReference(ContextMenuPanel.class, "ContextMenuPanel.js");
  private ResourceReference cssReference = new ResourceReference(ContextMenuPanel.class, "ContextMenuPanel.css");
  
  public ContextMenuPanel(String id, final String menuId) {
    super(id);
    WebMarkupContainer container = new WebMarkupContainer("contextMenu") {      
      @Override
      protected void onComponentTag(ComponentTag tag) {
        tag.put("id", "m" + menuId);
        super.onComponentTag(tag);
      }      
    };
    add(container);    
    container.add(createListView("menu", "menuitem"));
  }

  protected abstract ListView createListView(String menuId, String menuItemId);
  
  public void renderHead(IHeaderResponse response) {
    // import script    
    response.renderJavascriptReference(jsReference);
    response.renderCSSReference(cssReference);
  }

}
