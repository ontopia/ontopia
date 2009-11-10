package ontopoly.components;

import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.FieldInstance;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.RoleField;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.Topic;
import ontopoly.models.FieldInstanceModel;
import ontopoly.models.FieldValueModel;
import ontopoly.pages.AbstractOntopolyPage;

import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class FieldInstanceAssociationUnaryField extends Panel {

  private CheckBox checkbox;
  
  public FieldInstanceAssociationUnaryField(String id, final FieldValueModel fieldValueModel, final boolean readonly) {
    super(id);

    IModel<Boolean> selectedModel = new Model<Boolean>(Boolean.valueOf(fieldValueModel.isExistingValue()));
      
    this.checkbox = new CheckBox("player", selectedModel) {        
      @Override
      protected void onModelChanged() {
        super.onModelChanged();        
        
        FieldInstanceModel fieldInstanceModel = fieldValueModel.getFieldInstanceModel();
        FieldInstance fieldInstance = fieldInstanceModel.getFieldInstance();
        Topic currentTopic = fieldInstance.getInstance();
        
        RoleField currentField = (RoleField)fieldInstance.getFieldAssignment().getFieldDefinition();
        Boolean state = (Boolean)getModelObject();
        
        AbstractOntopolyPage page = (AbstractOntopolyPage)getPage();
        if (state.booleanValue()) {
          if (page.isAddAllowed(currentTopic, currentField)) {
            RoleField.ValueIF value = RoleField.createValue(1);
            value.addPlayer(currentField, currentTopic);          
            fieldInstance.addValue(value, page.getListener());
          }
        } else {
          if (page.isRemoveAllowed(currentTopic, currentField)) {
            RoleField.ValueIF value = RoleField.createValue(1);
            value.addPlayer(currentField, currentTopic);          
            fieldInstance.removeValue(value, page.getListener());
          }
        }
      }
    };
    checkbox.setEnabled(!readonly);
    add(checkbox);
  }
  
  public CheckBox getCheckBox() {
    return checkbox;
  }

}
