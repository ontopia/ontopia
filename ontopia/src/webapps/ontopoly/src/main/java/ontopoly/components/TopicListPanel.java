package ontopoly.components;

import java.util.List;

import ontopoly.model.Topic;
import ontopoly.pages.InstancePage;
import ontopoly.models.TopicModel;

import org.apache.wicket.model.IModel;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.panel.Panel;

// this looks like the ideal model to copy from:
// http://www.javalobby.org/java/forums/t60926.html

public class TopicListPanel extends Panel {
  
  public TopicListPanel(String id, IModel<List<Topic>> topics) {
    super(id);
    add(new TopicListView("topicList", topics));
  }

  // --- TopicListView

  public static class TopicListView extends ListView {
    public TopicListView(String id, IModel<List<Topic>> list) {
      super(id, list);
    }
    
    protected void populateItem(ListItem item) {
      Topic topic = (Topic) item.getModelObject();
      // FIXME: upgrade to TopicLink
      item.add(new TopicInstanceLink("topicLink", new TopicModel<Topic>(topic)));
      //item.add(new org.apache.wicket.markup.html.basic.Label("topicName", topic.getName()));
    }
  }

  // --- TopicInstanceLink

  // this overuse of inheritance in Wicket is a damn pain,
  // particularly when, like me, you HATE the Java syntax for
  // anonymous classes.

  public static class TopicInstanceLink extends TopicLink {
    public TopicInstanceLink(String id, IModel<? extends Topic> topicModel) {
      super(id, topicModel);
    }

    public Class getPageClass() {
      return InstancePage.class;
    }
  }
}
