package ontopoly.components;

import org.apache.wicket.Component;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

public abstract class OmnigatorLinkFunctionBoxPanel extends CustomLinkFunctionBoxPanel {
  
  public OmnigatorLinkFunctionBoxPanel(String id) {
    super(id);
  }
  
  @Override
  protected IModel getFirstResourceModel() {
    return new ResourceModel("omnigator.text1");
  }

  @Override
  protected IModel getSecondResourceModel() {
    return new ResourceModel("omnigator.text2");    
  }
 
  @Override
  protected Component getLink(String id) {
    String url = new ResourceModel("omnigator.url").getObject().toString()+"?tm="+getTopicMapId()+"&id="+getTopicId();
    
    return new ExternalLink(id, url, new ResourceModel("omnigator.link.label").getObject().toString()) {
      @Override
      protected void onComponentTag(ComponentTag tag) {
        tag.setName("a");
        tag.put("target", "_blank");
        super.onComponentTag(tag);
      }
    };
  }
  
  protected abstract String getTopicMapId();
  protected abstract String getTopicId();
}
