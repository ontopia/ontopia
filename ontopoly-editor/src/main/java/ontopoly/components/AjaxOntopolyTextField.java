package ontopoly.components;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

public class AjaxOntopolyTextField extends TextField<String> {

  private String cols;

  public AjaxOntopolyTextField(String id, IModel<String> model) {
    super(id, model);
    
    setOutputMarkupId(true);

    add(new AjaxFormComponentUpdatingBehavior("onchange") {
      protected void onUpdate(AjaxRequestTarget target) {
        AjaxOntopolyTextField.this.onUpdate(target);
      }
    });
  }

  public void setCols(int cols) {
    this.cols = Integer.toString(cols);
  }
  
  @Override
  protected void onComponentTag(ComponentTag tag) {
    tag.setName("input");
    tag.put("type", "text");
    tag.put("size", (cols != null ? cols : new ResourceModel("textfield.default.size").getObject().toString()));
    super.onComponentTag(tag);
  }
  
  protected void onUpdate(AjaxRequestTarget target) {}
  
}
