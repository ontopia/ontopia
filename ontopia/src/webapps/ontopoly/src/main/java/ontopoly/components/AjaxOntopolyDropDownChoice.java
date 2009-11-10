package ontopoly.components;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;

public class AjaxOntopolyDropDownChoice<T> extends DropDownChoice<T> {
   
  public AjaxOntopolyDropDownChoice(String id, IModel<T> model, IModel<List<T>> choices, IChoiceRenderer<? super T> renderer) {
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
