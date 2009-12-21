package ontopoly.components;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.utils.ObjectUtils;
import ontopoly.model.FieldInstance;
import ontopoly.models.FieldValueModel;
import ontopoly.pages.AbstractOntopolyPage;
import ontopoly.validators.ExternalValidation;
import ontopoly.validators.IdentityValidator;
import ontopoly.validators.URIValidator;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.PopupSettings;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.Model;


public class FieldInstanceURIField extends Panel {

  protected FieldValueModel fieldValueModel;
  protected String oldValue;
  protected TextField<String> textField;
  protected String cols = "60";
  protected ExternalLink button;
  
  public FieldInstanceURIField(String id, FieldValueModel _fieldValueModel) {
    super(id);
    this.fieldValueModel = _fieldValueModel;
    
    if (!fieldValueModel.isExistingValue()) {
      this.oldValue = null;
    } else {
      Object value = fieldValueModel.getObject();
      if (value instanceof OccurrenceIF) {
        OccurrenceIF occ = (OccurrenceIF)value;
        this.oldValue = occ.getValue();
      } else if (value instanceof LocatorIF) {      
        LocatorIF identity = (LocatorIF)value;
        this.oldValue = identity.getAddress();      
      } else {
        throw new RuntimeException("Unsupported field value: " + value);
      }
    }

    this.textField = new TextField<String>("input", new Model<String>(oldValue)) {      
      @Override
      public boolean isEnabled() {
        return FieldInstanceURIField.this.isEnabled();
      }
      @Override
      protected void onComponentTag(ComponentTag tag) {
        tag.setName("input");
        tag.put("type", "text");
        tag.put("size", cols);
        super.onComponentTag(tag);
      }
      
      @Override
      protected void onModelChanged() {
        super.onModelChanged();
        String newValue = (String)getModelObject();
        if (ObjectUtils.equals(newValue, oldValue)) return;
        AbstractOntopolyPage page = (AbstractOntopolyPage)getPage();
        FieldInstance fieldInstance = fieldValueModel.getFieldInstanceModel().getFieldInstance();
        if (fieldValueModel.isExistingValue() && oldValue != null)
          fieldInstance.removeValue(oldValue, page.getListener());
        if (newValue != null && !newValue.equals("")) {
          fieldInstance.addValue(newValue, page.getListener());
          fieldValueModel.setExistingValue(newValue);
        }
        oldValue = newValue;
      }
      
    };
    textField.add(new URIValidator());
    textField.add(new IdentityValidator(fieldValueModel.getFieldInstanceModel()));
    add(textField);
    
    this.button = new ExternalLink("button", new AbstractReadOnlyModel<String>() {
          @Override
          public String getObject() {
            return textField.getModelObject();
          }      
        }) {

      @Override
      public boolean isVisible() {
        return textField.getModelObject() != null;
      }      
    };
    button.setOutputMarkupId(true);
    button.setPopupSettings(new PopupSettings(PopupSettings.LOCATION_BAR | PopupSettings.MENU_BAR | 
                                              PopupSettings.RESIZABLE | PopupSettings.SCROLLBARS | 
                                              PopupSettings.STATUS_BAR | PopupSettings.TOOL_BAR));
    add(button);

    // validate field using registered validators
    ExternalValidation.validate(textField, oldValue);
  }

  public TextField getTextField() {
    return textField;
  }
  
  public ExternalLink getLinkButton() {
    return button;
  }
  
  public void setCols(int cols) {
    this.cols = Integer.toString(cols);
  }
  
}
