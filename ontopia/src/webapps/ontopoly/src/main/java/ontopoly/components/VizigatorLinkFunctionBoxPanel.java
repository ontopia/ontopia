package ontopoly.components;

import ontopoly.pages.VizigatorPage;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

public abstract class VizigatorLinkFunctionBoxPanel extends CustomLinkFunctionBoxPanel {

  public VizigatorLinkFunctionBoxPanel(String id) {
    super(id);
  }
  
  @Override
  protected IModel<String> getFirstResourceModel() {
    return new ResourceModel("vizigator.text1");
  }

  @Override
  protected IModel<String> getSecondResourceModel() {
    return new ResourceModel("vizigator.text2");    
  }

  @Override
  protected Component getLink(String id) {
    PageParameters pageParameters = new PageParameters();
    pageParameters.put("topicMapId", getTopicMapId());
    pageParameters.put("topicId", getTopicId());
    
    return new BookmarkablePageLink<Page>(id, VizigatorPage.class, pageParameters) {
      @Override
      protected void onComponentTag(ComponentTag tag) {
        tag.setName("a");
        //tag.put("target", "_blank");
        super.onComponentTag(tag);
      }
      @Override
      protected void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
        replaceComponentTagBody(markupStream, openTag, new ResourceModel("vizigator.text2").getObject().toString());
      }
    };
  }
  
  protected abstract String getTopicMapId();
  protected abstract String getTopicId();
  
}
