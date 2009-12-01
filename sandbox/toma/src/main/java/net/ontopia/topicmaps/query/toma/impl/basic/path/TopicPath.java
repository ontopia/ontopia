/**
 * Copyright (C) 2009 Space Applications Services
 *   <thomas.neidhart@spaceapplications.com>
 *
 * This file is part of the Ontopia project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.ontopia.topicmaps.query.toma.impl.basic.path;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.core.index.NameIndexIF;
import net.ontopia.topicmaps.query.toma.impl.basic.BasicPathElementIF;
import net.ontopia.topicmaps.query.toma.impl.basic.LocalContext;
import net.ontopia.topicmaps.query.toma.parser.ast.AbstractTopic;
import net.ontopia.topicmaps.query.toma.parser.ast.PathElementIF;
import net.ontopia.utils.CompactHashSet;

/**
 * INTERNAL: Represents a topic literal.
 */
@SuppressWarnings("unchecked")
public class TopicPath extends AbstractTopic implements BasicPathElementIF {
  static final Set<TYPE> inputSet;
  
  static {
    inputSet = new CompactHashSet();
    inputSet.add(TYPE.NONE);
  }

  private static final String[] columns = new String[] { "TOPIC" };
  
  public TopicPath(AbstractTopic.IDTYPE type, String id) {
    super(type, id);
  }
  
  public void initResultSet(LocalContext context) {}

  public String[] getColumnNames() {
    return columns;
  }

  public int getResultSize() {
    return 1;
  }

  @Override
  protected boolean isChildAllowed() {
    return false;
  }

  @Override
  protected boolean isLevelAllowed() {
    return false;
  }

  @Override
  protected boolean isScopeAllowed() {
    return false;
  }

  @Override
  protected boolean isTypeAllowed() {
    return false;
  }

  public TYPE output() {
    return PathElementIF.TYPE.TOPIC;
  }

  public Set<TYPE> validInput() {
    return inputSet;
  }

  /**
   * Evaluate a topic literal and return the real topics that are identified by
   * the literal.
   * 
   * @return a Collection of {@link TopicIF} objects.
   */
  public Collection<TopicIF> evaluate(LocalContext context, Object input) {
    TopicMapIF topicmap = context.getTopicMap();

    List<TopicIF> result = new ArrayList<TopicIF>();
    
    switch (getIDType()) {

    case IID:
      getTopicsByIID(topicmap, result);
      break;

    case SI:
      getTopicsBySI(topicmap, result);
      break;

    case SL:
      getTopicsBySL(topicmap, result);
      break;

    case NAME:
      getTopicsByName(topicmap, result);
      break;

    case VAR:
      getTopicsByVar(topicmap, result);
      break;
    }

    // should not be reached anyways
    return result;
  }

  private void getTopicsByIID(TopicMapIF topicmap, List<TopicIF> coll) {
    LocatorIF locator = topicmap.getStore().getBaseAddress().resolveAbsolute(
        "#" + getIdentifier());
    TMObjectIF obj = topicmap.getObjectByItemIdentifier(locator);
    if (obj != null && obj instanceof TopicIF) {
      coll.add((TopicIF) obj);
    }
  }

  private void getTopicsBySI(TopicMapIF topicmap, List<TopicIF> coll) {
    LocatorIF locator = URILocator.create(getIdentifier());
    TopicIF topic = topicmap.getTopicBySubjectIdentifier(locator);
    if (topic != null) {
      coll.add(topic);
    }
  }

  private void getTopicsBySL(TopicMapIF topicmap, List<TopicIF> coll) {
    LocatorIF locator = URILocator.create(getIdentifier());
    TopicIF topic = topicmap.getTopicBySubjectLocator(locator);
    if (topic != null) {
      coll.add(topic);
    }
  }

  private void getTopicsByName(TopicMapIF topicmap, List<TopicIF> coll) {
    NameIndexIF index = (NameIndexIF) topicmap
        .getIndex("net.ontopia.topicmaps.core.index.NameIndexIF");

    Collection<TopicNameIF> names = index.getTopicNames(getIdentifier());
    for (TopicNameIF name : names) {
      coll.add(name.getTopic());
    }
  }

  private void getTopicsByVar(TopicMapIF topicmap, List<TopicIF> coll) {
    NameIndexIF index = (NameIndexIF) topicmap
        .getIndex("net.ontopia.topicmaps.core.index.NameIndexIF");

    Collection<VariantNameIF> variants = index.getVariants(getIdentifier());
    for (VariantNameIF name : variants) {
      coll.add(name.getTopic());
    }
  }
}
