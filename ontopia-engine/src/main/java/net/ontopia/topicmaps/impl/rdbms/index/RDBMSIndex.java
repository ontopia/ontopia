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

import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.index.IndexIF;
import net.ontopia.topicmaps.impl.utils.IndexManagerIF;
import net.ontopia.topicmaps.impl.rdbms.RDBMSTopicMapTransaction;
import net.ontopia.topicmaps.impl.utils.AbstractIndex;

/**
 * INTERNAL: An abstract super class used by the rdbms indexes.
 */
public abstract class RDBMSIndex extends AbstractIndex implements IndexIF {
  protected IndexManagerIF imanager;
  protected RDBMSTopicMapTransaction transaction;
  
  public RDBMSIndex(IndexManagerIF imanager) {
    this.imanager = imanager;
    transaction = (RDBMSTopicMapTransaction)imanager.getTransaction();
  }

  public IndexIF getIndex() {
    return this;
  }
  
  // ---------------------------------------------------------------------------
  // Query
  // ---------------------------------------------------------------------------

  TopicMapIF getTopicMap() {
    return transaction.getTopicMap();
  }

  protected Object executeQuery(String name, Object[] params) {
    return transaction.getTransaction().executeQuery(name, params);
  }
  
}





