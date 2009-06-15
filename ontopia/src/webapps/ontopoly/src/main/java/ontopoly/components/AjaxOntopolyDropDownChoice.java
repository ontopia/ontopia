package ontopoly.components;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;

public class AjaxOntopolyDropDownChoice extends DropDownChoice {
   
  public AjaxOntopolyDropDownChoice(String id, IModel model, IModel choices, IChoiceRenderer renderer) {
    super(id, model, choices, renderer);

    setOutputMarkupId(true);
  
    add(new AjaxFormComponentUpdatingBehavior("onchange") {
      protected void onUpdate(AjaxRequestTarget target) {
        AjaxOntopolyDropDownChoice.this.onUpdate(target);
      }
    });
  }

  @Override
  protected void onComponentTag(ComponentTag tag) {
    tag.setName("select");
    super.onComponentTag(tag);
  }

  protected void onUpdate(AjaxRequestTarget target) {}
  
}
