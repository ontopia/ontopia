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

import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.ReifiableIF;
import net.ontopia.topicmaps.core.ScopedIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;

/**
 * INTERNAL: Carrier for variables used during parsing to keep track
 * of context such as current topic, current topic name, current
 * statement (scoped/reifiable) etc. Moved out to a separate class
 * since the existence of embedded topics makes it necessary to be
 * able to stack these frames.
 */
public class ParseFrame {

  // the topic of the current topic block, *or* the current embedded topic
  protected TopicIF topic;

  // the topic name currently being parsed (needed for variant names)  
  protected TopicNameIF name;

  // the statement currently being parsed (used by the scope parsing code)
  protected ScopedIF scoped;

  // the statement currently being parsed (used by the reifier parsing code)
  protected ReifiableIF reifiable;

  // the association currently being parsed (needed for roles)
  protected AssociationIF association;

  // constructor
  public ParseFrame(TopicIF topic,
                    TopicNameIF name,
                    ScopedIF scoped,
                    ReifiableIF reifiable,
                    AssociationIF association) {
    this.topic = topic;
    this.name = name;
    this.scoped = scoped;
    this.reifiable = reifiable;
    this.association = association;
  }
}
