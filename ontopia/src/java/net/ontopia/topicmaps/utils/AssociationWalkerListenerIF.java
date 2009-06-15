// $Id: AssociationWalkerListenerIF.java,v 1.6 2002/05/29 13:38:43 hca Exp $

package net.ontopia.topicmaps.utils;

import net.ontopia.topicmaps.core.*;

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
     * @param assoc           The association in the triple; an object implementing AssociationIF.
     * @param rightRolePlayer The second topic in the triple; an object implementing TopicIF.
     */
    public void  walkAssociation(TopicIF leftRolePlayer, AssociationIF association, TopicIF rightRolePlayer);

}





