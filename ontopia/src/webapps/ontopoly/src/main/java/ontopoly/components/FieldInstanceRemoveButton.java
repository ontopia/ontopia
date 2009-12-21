package ontopoly.components;

import ontopoly.model.FieldInstance;
import ontopoly.models.FieldValueModel;
import ontopoly.pages.AbstractOntopolyPage;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

public class FieldInstanceRemoveButton extends OntopolyImageLink {

  FieldValueModel fieldValueModel;
  
  public FieldInstanceRemoveButton(String id, String image, FieldValueModel fieldValueModel) {
    super(id, image);
    this.fieldValueModel = fieldValueModel;
  }
  
  @Override
  public boolean isVisible() {
    return fieldValueModel.isExistingValue();
  }

  @Override
  public void onClick(AjaxRequestTarget target) {
    AbstractOntopolyPage page = (AbstractOntopolyPage)getPage();
    FieldInstance fieldInstance = fieldValueModel.getFieldInstanceModel().getFieldInstance();
    Object value = fieldValueModel.getObject();
    fieldInstance.removeValue(value, page.getListener());        
  }
  
  @Override
  public IModel getTitleModel() {
    return new ResourceModel("icon.remove.remove-value");
  }
  
  @Override
  protected void onDetach() {
    super.onDetach();
    fieldValueModel.detach();
  }

}
