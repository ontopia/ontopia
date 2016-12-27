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

package net.ontopia.topicmaps.utils;

import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.TopicIF;

/**
 * PUBLIC: This interface defines a listener to the
 * AssociationWalker. The listener is invoked once for each triple of
 * topic, association, associated-topic found by the walker.  The
 * event processing allows the handler to prematurely terminate the
 * walk.
 * (NB these triples are NOT "triples" in the RDF sense.)  */
public interface AssociationWalkerListenerIF
{
    /**
     * PUBLIC: The function invoked by the AssociationWalker.
     *
     * @param leftRolePlayer The first topic in the triple; an object implementing TopicIF.
     * @param association The association in the triple; an object implementing AssociationIF.
     * @param rightRolePlayer The second topic in the triple; an object implementing TopicIF.
     */
    void  walkAssociation(TopicIF leftRolePlayer, AssociationIF association, TopicIF rightRolePlayer);

}





