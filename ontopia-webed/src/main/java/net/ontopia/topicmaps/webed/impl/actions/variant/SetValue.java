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

import java.util.Collections;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.impl.utils.ActionSignature;

/**
 * PUBLIC: Action for setting the value of an internal variant.
 */
public class SetValue implements ActionIF {

  @Override
  public void perform(ActionParametersIF params, ActionResponseIF response) {
    //test params
    ActionSignature paramsType = ActionSignature.getSignature("v b?");
    paramsType.validateArguments(params, this);

    VariantNameIF variant = (VariantNameIF) params.get(0);
    TopicNameIF basename = (TopicNameIF) params.get(1);
    String value = params.getStringValue();

    if (variant == null) {
      // check if new variant should be created, otherwise do nothing
      if (value.trim().length() == 0)
        return;
      
      // create new occurrence
      TopicMapBuilderIF builder = basename.getTopicMap().getBuilder();
      variant = builder.makeVariantName(basename, "", Collections.emptySet());
    }
    
    variant.setValue(value);

  }
  
}
