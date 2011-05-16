package ontopoly.models;

import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.model.AbstractReadOnlyModel;

public class HelpLinkResourceModel extends AbstractReadOnlyModel<String> {

  private String resourceKey;
  
  public HelpLinkResourceModel(String resourceKey) {
    this.resourceKey = resourceKey;
  }

  @Override
  public String getObject() {
    return RequestCycle.get().getRequest().getRelativePathPrefixToContextRoot() + "doc/" +
      Application.get().getResourceSettings().getLocalizer().getString(resourceKey, (Component)null, (String)null);
  }
}
