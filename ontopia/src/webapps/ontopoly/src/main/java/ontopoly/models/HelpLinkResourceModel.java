package ontopoly.models;

import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

public class HelpLinkResourceModel extends AbstractReadOnlyModel implements IModel {

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
