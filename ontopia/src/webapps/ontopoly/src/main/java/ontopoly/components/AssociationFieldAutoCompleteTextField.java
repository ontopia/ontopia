package ontopoly.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.Topic;
import ontopoly.models.RoleFieldModel;
import ontopoly.models.TopicModel;
import ontopoly.utils.TopicComparator;

import org.apache.wicket.Response;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AbstractAutoCompleteRenderer;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteSettings;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public abstract class AssociationFieldAutoCompleteTextField extends Panel {
  
  private static String PREFIX = "id:";
  
  protected AutoCompleteTextField textField;
  
  public AssociationFieldAutoCompleteTextField(String id, IModel model, final RoleFieldModel valueFieldModel) {
    super(id);
        
    AutoCompleteSettings opts = new AutoCompleteSettings();
    opts.setCssClassName("ontopoly-autocompleter");
    opts.setAdjustInputWidth(false);
    
    this.textField = new AutoCompleteTextField("autoComplete", model, (Class)null, new AbstractAutoCompleteRenderer() {
        @Override
        protected String getTextValue(Object o) {
          return PREFIX + ((Topic)o).getId();
        }
        @Override
        protected void renderChoice(Object o, Response response, String criteria) {
          response.write(((Topic)o).getName());
        }}, opts) {

      @Override
      protected Iterator getChoices(String input) {
        List result = new ArrayList(valueFieldModel.getRoleField().searchAllowedPlayers(input));
        filterPlayers(result);
        Collections.sort(result, TopicComparator.INSTANCE);
        return result.iterator();
      }
      
      @Override
      protected void onModelChanged() {
        String modelValue = (String)getTextField().getModelObject();
        if (modelValue != null && modelValue.startsWith(PREFIX)) {
          String topicId = modelValue.substring(PREFIX.length());
          String topicMapId = valueFieldModel.getRoleField().getTopicMap().getId();
          Topic topic = new TopicModel(topicMapId, topicId).getTopic();
          AssociationFieldAutoCompleteTextField.this.onTopicSelected(topic);
        }
      }
    };    
    add(textField);
  }

  protected abstract void filterPlayers(List players);
  
  protected AutoCompleteTextField getTextField() {
    return textField;
  }

  protected abstract void onTopicSelected(Topic topic);   
    
}
