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

package net.ontopia.topicmaps.impl.rdbms.index;

import java.util.Collection;

import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.impl.utils.IndexManagerIF;
import net.ontopia.topicmaps.core.index.NameIndexIF;
import net.ontopia.infoset.core.LocatorIF;

/**
 * INTERNAL: The rdbms name index implementation.
 */
public class NameIndex extends RDBMSIndex implements NameIndexIF {

  NameIndex(IndexManagerIF imanager) {
    super(imanager);
  }
  
  // ---------------------------------------------------------------------------
  // NameIndexIF
  // ---------------------------------------------------------------------------
  
  public Collection<TopicNameIF> getTopicNames(String value) {
    return (Collection<TopicNameIF>)executeQuery("NameIndexIF.getTopicNames",
                                    new Object[] { getTopicMap(), value });
  }

  public Collection<VariantNameIF> getVariants(String value) {
    return (Collection<VariantNameIF>)executeQuery("NameIndexIF.getVariants",
                                         new Object[] { getTopicMap(), value });
  }

  public Collection<VariantNameIF> getVariants(String value, LocatorIF datatype) {
    return (Collection<VariantNameIF>)executeQuery("NameIndexIF.getVariantsByDataType", new Object[] { getTopicMap(), value, datatype.getAddress() });
	}

}





