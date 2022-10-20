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

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.xml.InvalidTopicMapException;

/**
 * INTERNAL: Represents a CTM template. Contains a recorded list of
 * parse events which is replayed when the template is invoked.
 */
public class Template {
  private String name;
  private List<String> parameters; // just the names, in declared order
  private List events;
  private Map<String, NamedWildcardTopicGenerator> named_wildcards;
  /**
   * Each variable used in the template has a corresponding generator
   * stored here. On invocation, the invoke() method sets the passed
   * arguments in the generators stored here. The map is populated
   * from the declared list of parameters.
   */
  private Map<String, ParameterGenerator> generators;
  /**
   * Each variable actually occurring in the template is listed here.
   * The map is populated by the getGenerator() method.
   */
  private Set<String> used_parameters;
  
  public Template(String name, List<String> parameters) {
    this.name = name;
    this.parameters = parameters;
    this.events = new ArrayList();
    this.generators = new HashMap<String, ParameterGenerator>();
    this.named_wildcards = new HashMap<String, NamedWildcardTopicGenerator>();
    this.used_parameters = new HashSet<String>();

    for (String param : parameters) {
      generators.put(param, new ParameterGenerator());
    }
  }

  public String getName() {
    return name;
  }

  public int getParameterCount() {
    return parameters.size();
  }

  public Set<String> getUsedParameters() {
    return used_parameters;
  }

  public void addEvent(ParseEventIF event) {
    events.add(event);
  }

  public ValueGeneratorIF getGenerator(String name) {
    ValueGeneratorIF gen = generators.get(name);
    if (gen == null) {
      throw new InvalidTopicMapException("No such parameter: " + name);
    }
    used_parameters.add(name);
    return gen;
  }

  public Map<String, NamedWildcardTopicGenerator> getWildcardMap() {
    return named_wildcards;
  }

  /**
   * Invokes the template.
   * @param arguments a list of generator objects producing the values
   *                  for the arguments, in the same order as the
   *                  parameters list
   */
  public void invoke(List arguments, ParseEventHandlerIF handler) {
    if (parameters.size() != arguments.size()) {
      throw new InvalidTopicMapException("Incorrect number of arguments to " +
                                         "template " + name + ", got " +
                                         arguments.size() + ", expected " +
                                         parameters.size());
    }

    for (int ix = 0; ix < parameters.size(); ix++) {
      // name of parameter
      String name = parameters.get(ix);
      // generator producing passed argument value
      ValueGeneratorIF value = (ValueGeneratorIF) arguments.get(ix);
      // generator producing variable value inside invoked template
      ParameterGenerator generator = generators.get(name);
      // connecting parameter with its value
      generator.setGenerator(value);
    }

    for (int ix = 0; ix < events.size(); ix++) {
      ParseEventIF event = (ParseEventIF) events.get(ix);
      event.replay(handler);
    }

    // release all topics created by named wildcards so that on next
    // invocation we create new ones
    for (NamedWildcardTopicGenerator gen : named_wildcards.values()) {
      gen.contextEnd();
    }
  }

  /**
   * This method is only used by the tolog INSERT statement. It is
   * <em>not</em> meant to be called during normal operation. If you
   * do, don't complain if it breaks.
   */
  public void setParameters(List<String> parameters) {
    this.parameters = parameters;
  }

  // --- Parameter generator

  static class ParameterGenerator implements ValueGeneratorIF {
    private ValueGeneratorIF gen;

    public void setGenerator(ValueGeneratorIF gen) {
      this.gen = gen;
    }
    
    @Override
    public boolean isTopic() {
      return gen.isTopic();
    }
  
    @Override
    public String getLiteral() {
      return gen.getLiteral();
    }
  
    @Override
    public LocatorIF getDatatype() {
      return gen.getDatatype();
    }

    @Override
    public LocatorIF getLocator() {
      return gen.getLocator();
    }
  
    @Override
    public ValueGeneratorIF copy() {
      return this;
    }

    @Override
    public TopicIF getTopic() {
      return gen.getTopic();
    }

    @Override
    public String toString() {
      return "[ParameterGenerator: " + gen + "]";
    }
  }
}
