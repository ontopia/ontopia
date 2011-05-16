package ontopoly.components;

import net.ontopia.Ontopia;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

public class FooterPanel extends Panel {
  public FooterPanel(String id) {
    super(id);
    add(new Label("version", Ontopia.getVersion()) {
      @Override
      protected void onComponentTag(ComponentTag tag) {
        tag.put("title", Ontopia.getBuild());
        super.onComponentTag(tag);
      }
    });
  }
}
