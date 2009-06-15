package ontopoly.components;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;


public abstract class CheckLabelPanel extends Panel {

  public CheckLabelPanel(String id) {
    super(id);

    // add check box or radio button
    Component check = newCheck("check");
    add(check);
    
    // add
    add(newLabel("label"));
  }
  
  protected abstract Component newCheck(String id);

  protected abstract Label newLabel(String id);
  
}
