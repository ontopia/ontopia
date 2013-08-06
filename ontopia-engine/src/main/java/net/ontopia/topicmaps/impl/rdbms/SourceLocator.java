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

package net.ontopia.topicmaps.impl.rdbms;

import net.ontopia.infoset.core.LocatorIF;

/**
 * INTERNAL: A locator class used for representing topic map object
 * source locators.<p>
 *
 * No normalization or absolutization is done.<p>
 */

public class SourceLocator extends RDBMSLocator {
  
  // ---------------------------------------------------------------------------
  // Data members
  // ---------------------------------------------------------------------------
  
  protected String indicator;
  protected long tmobject;
  protected long topicmap;
  
  public SourceLocator() {
  }

  public SourceLocator(LocatorIF locator) {
    super(locator);
  }

  public long _getTMObject() {
    return tmobject;
  }

  public void _setTMObject(long tmobject) {
    this.tmobject = tmobject;
  }

  public long _getTopicMap() {
    return topicmap;
  }

  public void _setTopicMap(long topicmap) {
    this.topicmap = topicmap;
  }

  public String _getClassIndicator() {
    return indicator;
  }

  public void _setClassIndicator(String indicator) {
    this.indicator = indicator;
  }
  
}
