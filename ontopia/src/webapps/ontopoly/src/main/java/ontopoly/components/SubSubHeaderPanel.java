package ontopoly.components;

import java.util.List;

import ontopoly.models.TopicMapModel;

public class SubSubHeaderPanel extends SubHeaderPanel {

  public SubSubHeaderPanel(String id, TopicMapModel topicMapModel, int selectedTab, List tabMenuItem) {
    super(id, topicMapModel);

    add(new MenuPanel("tabMenu", tabMenuItem, selectedTab));
  }
  
}
