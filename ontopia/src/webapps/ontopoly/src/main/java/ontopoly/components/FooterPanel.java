package ontopoly.components;

import net.ontopia.products.TopicMapEngine;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

public class FooterPanel extends Panel {
  public FooterPanel(String id) {
    super(id);
    add(new Label("version", TopicMapEngine.getInstance().getVersion()) {
      @Override
      protected void onComponentTag(ComponentTag tag) {
        tag.put("title", TopicMapEngine.getInstance().getBuild());
        super.onComponentTag(tag);
      }
    });
  }
}
