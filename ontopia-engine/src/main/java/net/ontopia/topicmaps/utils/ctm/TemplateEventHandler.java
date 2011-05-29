
package net.ontopia.topicmaps.utils.ctm;

import java.util.List;
import java.util.Stack;
import java.net.MalformedURLException;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.xml.InvalidTopicMapException;

/**
 * INTERNAL: An event handler which produces a template object,
 * containing recorded events ready to be replayed when the template
 * is invoked.
 */
public class TemplateEventHandler implements ParseEventHandlerIF {
  private Template template;
  
  public TemplateEventHandler(String name, List<String> parameters,
                              ParseEventHandlerIF real_handler) {
    this.template = new Template(name, parameters);
  }

  // --- ParseEventHandlerIF implementation
  
  public void startTopicItemIdentifier(ValueGeneratorIF loc) {
    template.addEvent(new GenericParseEvent("startTopicItemIdentifier", loc));
  }
  
  public void startTopicSubjectIdentifier(ValueGeneratorIF loc) {
    template.addEvent(new GenericParseEvent("startTopicSubjectIdentifier", loc));
  }
  
  public void startTopicSubjectLocator(ValueGeneratorIF loc) {
    template.addEvent(new GenericParseEvent("startTopicSubjectLocator", loc));
  }

  public void startTopic(ValueGeneratorIF topicgen) {
    template.addEvent(new GenericParseEvent("startTopic", topicgen));
  }
  
  public void addItemIdentifier(ValueGeneratorIF locator) {
    template.addEvent(new GenericParseEvent("addItemIdentifier", locator));
  }
  
  public void addSubjectIdentifier(ValueGeneratorIF locator) {
    template.addEvent(new GenericParseEvent("addSubjectIdentifier", locator));
  }
  
  public void addSubjectLocator(ValueGeneratorIF locator) {
    template.addEvent(new GenericParseEvent("addSubjectLocator", locator));
  }

  public void addTopicType(ValueGeneratorIF type) {
    template.addEvent(new GenericParseEvent("addTopicType", type));
  }

  public void addSubtype(ValueGeneratorIF subtype) {
    template.addEvent(new GenericParseEvent("addSubtype", subtype));
  }
  
  public void startName(ValueGeneratorIF type, ValueGeneratorIF value) {
    template.addEvent(new GenericParseEvent("startName", type, value));
  }
  
  public void addScopingTopic(ValueGeneratorIF topic) {
    template.addEvent(new GenericParseEvent("addScopingTopic", topic));
  }
  
  public void addReifier(ValueGeneratorIF topic) {
    template.addEvent(new GenericParseEvent("addReifier", topic));
  }

  public void startVariant(ValueGeneratorIF value) {
    template.addEvent(new GenericParseEvent("startVariant", value));
  }
  
  public void endName() {
    template.addEvent(new GenericParseEvent("endName"));
  }

  public void startOccurrence(ValueGeneratorIF type, ValueGeneratorIF value) {
    template.addEvent(new GenericParseEvent("startOccurrence", type, value));
  }

  public void endOccurrence() {
    template.addEvent(new GenericParseEvent("endOccurrence"));
  }
  
  public void endTopic() {
    template.addEvent(new GenericParseEvent("endTopic"));
  }

  public void startAssociation(ValueGeneratorIF type) {
    template.addEvent(new GenericParseEvent("startAssociation", type));
  }
  
  public void addRole(ValueGeneratorIF type, ValueGeneratorIF player) {
    template.addEvent(new GenericParseEvent("addRole", type, player));
  }

  public void endRoles() {
    template.addEvent(new GenericParseEvent("endRoles"));
  }
  
  public void endAssociation() {
    template.addEvent(new GenericParseEvent("endAssociation"));
  }

  public void startEmbeddedTopic() {
    template.addEvent(new GenericParseEvent("startEmbeddedTopic"));
  }

  public ValueGeneratorIF endEmbeddedTopic() {
    GenericParseEvent event = new GenericParseEvent("endEmbeddedTopic");
    template.addEvent(event);
    return new EventTopicGenerator(event);
  }

  public void templateInvocation(String name, List arguments) {
    template.addEvent(new GenericParseEvent("templateInvocation",
                                            name, arguments));
  }

  // --- Own methods

  public Template getTemplate() {
    return template;
  }

  // --- Event class

  class GenericParseEvent implements ParseEventIF {
    private Method method;
    private Object[] parameters;
    private Object value;

    public GenericParseEvent(String method_name) {
      this.method = getMethod(method_name);
      this.parameters = null;
    }
    
    public GenericParseEvent(String method_name, Object parameter) {
      this.method = getMethod(method_name);
      this.parameters = new Object[1];
      parameters[0] = copy(parameter);
    }

    public GenericParseEvent(String method_name, Object p1, Object p2) {
      this.method = getMethod(method_name);
      this.parameters = new Object[2];
      parameters[0] = copy(p1);
      parameters[1] = copy(p2);
    }

    public void replay(ParseEventHandlerIF handler) throws InvalidTopicMapException {
      try {
        value = method.invoke(handler, parameters);
      } catch (IllegalAccessException e) {
        throw new OntopiaRuntimeException(e);
      } catch (InvocationTargetException e) {
        if (e.getCause() instanceof InvalidTopicMapException)
          throw (InvalidTopicMapException) e.getCause();
        throw new OntopiaRuntimeException(e.getCause());
      }
    }

    public Object getValue() {
      return value;
    }

    private Method getMethod(String name) {
      Method[] methods = ParseEventHandlerIF.class.getMethods();
      for (int ix = 0; ix < methods.length; ix++)
        if (methods[ix].getName().equals(name))
          return methods[ix];
      return null;
    }

    private Object copy(Object parameter) {
      if (parameter instanceof ValueGeneratorIF)
        return ((ValueGeneratorIF) parameter).copy();
      else
        return parameter;
    }
  }

  // --- EventTopicGenerator

  class EventTopicGenerator extends AbstractTopicGenerator {
    private GenericParseEvent event;

    public EventTopicGenerator(GenericParseEvent event) {
      this.event = event;
    }
    
    public TopicIF getTopic() {
      ValueGeneratorIF gen = (ValueGeneratorIF) event.getValue();
      return gen.getTopic();
    }

    public ValueGeneratorIF copy() {
      return this;
    }
  }
}
