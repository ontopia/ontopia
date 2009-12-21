package ontopoly.validators;

import java.util.Collections;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.utils.ObjectUtils;
import ontopoly.model.Topic;
import ontopoly.model.TopicMap;
import ontopoly.models.FieldInstanceModel;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;

public class IdentityValidator extends AbstractValidator<String> {

  protected FieldInstanceModel fieldInstanceModel;
  
  public IdentityValidator(FieldInstanceModel fieldInstanceModel) {
    this.fieldInstanceModel = fieldInstanceModel;
  }
  
  @Override
  protected void onValidate(IValidatable<String> validatable) {
    String value = validatable.getValue();
    if (value == null) return;
    LocatorIF locator;
    try {
      locator = URILocator.create(value);
    } catch (Exception e) {
      return; // ignore this as it will be caught by another validator
    }

    Topic topic = fieldInstanceModel.getFieldInstance().getInstance();
    TopicMap topicMap = topic.getTopicMap();
    TopicMapIF topicMapIf = topicMap.getTopicMapIF();
    TopicIF topicIf = topic.getTopicIF();
    
    TopicIF otopic = topicMapIf.getTopicBySubjectIdentifier(locator);
    if (otopic != null && ObjectUtils.different(topicIf, otopic))
      error(validatable, "validators.IdentityValidator.subjectIdentifierClash", Collections.singletonMap("identity", (Object)value));

    otopic = topicMapIf.getTopicBySubjectLocator(locator);
    if (otopic != null && ObjectUtils.different(topicIf, otopic))
      error(validatable, "validators.IdentityValidator.subjectLocatorClash", Collections.singletonMap("identity", (Object)value));
  }

}
