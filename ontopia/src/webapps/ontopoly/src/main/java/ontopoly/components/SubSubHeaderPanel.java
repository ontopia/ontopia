package ontopoly.components;

import java.util.List;

import ontopoly.models.TopicMapModel;
import ontopoly.pojos.MenuItem;

public class SubSubHeaderPanel extends SubHeaderPanel {

  public SubSubHeaderPanel(String id, TopicMapModel topicMapModel, int selectedTab, List<MenuItem> tabMenuItem) {
    super(id, topicMapModel);

    add(new MenuPanel("tabMenu", tabMenuItem, selectedTab));
  }
  
}
