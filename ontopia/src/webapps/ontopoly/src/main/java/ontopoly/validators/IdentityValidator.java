package ontopoly.validators;

import java.io.Serializable;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.utils.ObjectUtils;
import ontopoly.components.AbstractFieldInstancePanel;
import ontopoly.model.OntopolyTopicIF;
import ontopoly.model.OntopolyTopicMapIF;
import ontopoly.models.FieldInstanceModel;

import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;

public class IdentityValidator extends AbstractValidator<String> {

  protected FieldInstanceModel fieldInstanceModel;
  private final Component component;
  
  public IdentityValidator(Component component, FieldInstanceModel fieldInstanceModel) {
    this.component = component;
    this.fieldInstanceModel = fieldInstanceModel;
  }
  
  @Override
  protected void onValidate(IValidatable<String> validatable) {
    String value = validatable.getValue();
    if (value == null) return;
    LocatorIF locator;
    try {
      locator = new URILocator(value);
    } catch (Exception e) {
      reportError("validators.IdentityValidator.invalidURI", value);
      return;
    }

    OntopolyTopicIF topic = fieldInstanceModel.getFieldInstance().getInstance();
    OntopolyTopicMapIF topicMap = topic.getTopicMap();
    TopicMapIF topicMapIf = topicMap.getTopicMapIF();
    TopicIF topicIf = topic.getTopicIF();
  
    TopicIF otopic = topicMapIf.getTopicBySubjectIdentifier(locator);
    if (otopic != null && ObjectUtils.different(topicIf, otopic))
      reportError("validators.IdentityValidator.subjectIdentifierClash", value);

    otopic = topicMapIf.getTopicBySubjectLocator(locator);
    if (otopic != null && ObjectUtils.different(topicIf, otopic))
      reportError("validators.IdentityValidator.subjectLocatorClash", value);
  }

  private void reportError(String resourceKey, final String identity) {    
    String message = Application.get().getResourceSettings().getLocalizer().getString(resourceKey, (Component)null, 
        new Model<Serializable>(new Serializable() {
          @SuppressWarnings("unused")
          public String getIdentity() {
            return identity;
          }
        }));
    component.error(AbstractFieldInstancePanel.createErrorMessage(fieldInstanceModel, new Model<String>(message)));    
  }

}
