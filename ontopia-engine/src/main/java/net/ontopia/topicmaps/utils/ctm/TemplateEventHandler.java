/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

package net.ontopia.topicmaps.utils.ctm;

import java.util.List;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.utils.OntopiaRuntimeException;
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
  
  @Override
  public void startTopicItemIdentifier(ValueGeneratorIF loc) {
    template.addEvent(new GenericParseEvent("startTopicItemIdentifier", loc));
  }
  
  @Override
  public void startTopicSubjectIdentifier(ValueGeneratorIF loc) {
    template.addEvent(new GenericParseEvent("startTopicSubjectIdentifier", loc));
  }
  
  @Override
  public void startTopicSubjectLocator(ValueGeneratorIF loc) {
    template.addEvent(new GenericParseEvent("startTopicSubjectLocator", loc));
  }

  @Override
  public void startTopic(ValueGeneratorIF topicgen) {
    template.addEvent(new GenericParseEvent("startTopic", topicgen));
  }
  
  @Override
  public void addItemIdentifier(ValueGeneratorIF locator) {
    template.addEvent(new GenericParseEvent("addItemIdentifier", locator));
  }
  
  @Override
  public void addSubjectIdentifier(ValueGeneratorIF locator) {
    template.addEvent(new GenericParseEvent("addSubjectIdentifier", locator));
  }
  
  @Override
  public void addSubjectLocator(ValueGeneratorIF locator) {
    template.addEvent(new GenericParseEvent("addSubjectLocator", locator));
  }

  @Override
  public void addTopicType(ValueGeneratorIF type) {
    template.addEvent(new GenericParseEvent("addTopicType", type));
  }

  @Override
  public void addSubtype(ValueGeneratorIF subtype) {
    template.addEvent(new GenericParseEvent("addSubtype", subtype));
  }
  
  @Override
  public void startName(ValueGeneratorIF type, ValueGeneratorIF value) {
    template.addEvent(new GenericParseEvent("startName", type, value));
  }
  
  @Override
  public void addScopingTopic(ValueGeneratorIF topic) {
    template.addEvent(new GenericParseEvent("addScopingTopic", topic));
  }
  
  @Override
  public void addReifier(ValueGeneratorIF topic) {
    template.addEvent(new GenericParseEvent("addReifier", topic));
  }

  @Override
  public void startVariant(ValueGeneratorIF value) {
    template.addEvent(new GenericParseEvent("startVariant", value));
  }
  
  @Override
  public void endName() {
    template.addEvent(new GenericParseEvent("endName"));
  }

  @Override
  public void startOccurrence(ValueGeneratorIF type, ValueGeneratorIF value) {
    template.addEvent(new GenericParseEvent("startOccurrence", type, value));
  }

  @Override
  public void endOccurrence() {
    template.addEvent(new GenericParseEvent("endOccurrence"));
  }
  
  @Override
  public void endTopic() {
    template.addEvent(new GenericParseEvent("endTopic"));
  }

  @Override
  public void startAssociation(ValueGeneratorIF type) {
    template.addEvent(new GenericParseEvent("startAssociation", type));
  }
  
  @Override
  public void addRole(ValueGeneratorIF type, ValueGeneratorIF player) {
    template.addEvent(new GenericParseEvent("addRole", type, player));
  }

  @Override
  public void endRoles() {
    template.addEvent(new GenericParseEvent("endRoles"));
  }
  
  @Override
  public void endAssociation() {
    template.addEvent(new GenericParseEvent("endAssociation"));
  }

  @Override
  public void startEmbeddedTopic() {
    template.addEvent(new GenericParseEvent("startEmbeddedTopic"));
  }

  @Override
  public ValueGeneratorIF endEmbeddedTopic() {
    GenericParseEvent event = new GenericParseEvent("endEmbeddedTopic");
    template.addEvent(event);
    return new EventTopicGenerator(event);
  }

  @Override
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

    @Override
    public void replay(ParseEventHandlerIF handler) throws InvalidTopicMapException {
      try {
        value = method.invoke(handler, parameters);
      } catch (IllegalAccessException e) {
        throw new OntopiaRuntimeException(e);
      } catch (InvocationTargetException e) {
        if (e.getCause() instanceof InvalidTopicMapException) {
          throw (InvalidTopicMapException) e.getCause();
        }
        throw new OntopiaRuntimeException(e.getCause());
      }
    }

    public Object getValue() {
      return value;
    }

    private Method getMethod(String name) {
      Method[] methods = ParseEventHandlerIF.class.getMethods();
      for (int ix = 0; ix < methods.length; ix++) {
        if (methods[ix].getName().equals(name)) {
          return methods[ix];
        }
      }
      return null;
    }

    private Object copy(Object parameter) {
      if (parameter instanceof ValueGeneratorIF) {
        return ((ValueGeneratorIF) parameter).copy();
      } else {
        return parameter;
      }
    }
  }

  // --- EventTopicGenerator

  class EventTopicGenerator extends AbstractTopicGenerator {
    private GenericParseEvent event;

    public EventTopicGenerator(GenericParseEvent event) {
      this.event = event;
    }
    
    @Override
    public TopicIF getTopic() {
      ValueGeneratorIF gen = (ValueGeneratorIF) event.getValue();
      return gen.getTopic();
    }

    @Override
    public ValueGeneratorIF copy() {
      return this;
    }
  }
}
