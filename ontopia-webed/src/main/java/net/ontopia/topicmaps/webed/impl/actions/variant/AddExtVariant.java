/*
 * #!
 * Ontopia Webed
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

package net.ontopia.topicmaps.webed.impl.actions.variant;

import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Iterator;

import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.core.ActionRuntimeException;
import net.ontopia.topicmaps.webed.impl.actions.topicmap.AbstractTopicMapAction;
import net.ontopia.topicmaps.webed.impl.utils.ActionSignature;

/**
 * PUBLIC: Action for adding an external variant to a topic. The
 * variant scope may optionally be specified.
 */
public class AddExtVariant extends AbstractTopicMapAction {

  @Override
  public void perform(ActionParametersIF params, ActionResponseIF response) {
    //test params
    ActionSignature paramsType = ActionSignature.getSignature("b t?");
    paramsType.validateArguments(params, this);


    TopicNameIF basename = (TopicNameIF) params.get(0);
    Collection themes = params.getCollection(1);
    String value = params.getStringValue().trim();
    
    TopicMapBuilderIF builder =
      basename.getTopicMap().getBuilder();
    
    // do not create variant with empty string value
    if (value == null || value.equals(""))
      return;
    
    // create new (external) variant for base name
    URILocator locator = null;
    try {
      locator = new URILocator(value);
    } catch (MalformedURLException e) {
      throw new ActionRuntimeException("Malformed URL for variant: '" + value + "'", false);
    }
    
    VariantNameIF variant = builder.makeVariantName(basename, locator);

    // set scope, if provided
    if (themes != null) {
      Iterator it = themes.iterator();
      while (it.hasNext()) 
        variant.addTheme((TopicIF) it.next());
    }
  }
}
