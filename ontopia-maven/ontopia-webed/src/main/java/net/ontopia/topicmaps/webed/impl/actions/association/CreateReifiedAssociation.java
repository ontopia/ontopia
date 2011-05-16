
// $Id: CreateReifiedAssociation.java,v 1.12 2008/06/13 08:17:56 geir.gronmo Exp $

package net.ontopia.topicmaps.webed.impl.actions.association;

import java.net.MalformedURLException;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.core.ActionRuntimeException;

/**
 * PUBLIC: Action for creating a new association with one topic playing
 * a given role. In addition the association is automatically reified.
 *
 * @since 2.0
 */
public class CreateReifiedAssociation implements ActionIF {

  public void perform(ActionParametersIF params, ActionResponseIF response) {

    TopicIF srctopic = (TopicIF) params.get(0);
    TopicIF roleType = (TopicIF) params.get(1);
		TopicIF atype = (TopicIF) params.get(2);

    TopicMapBuilderIF builder = srctopic.getTopicMap().getBuilder();

		if (atype == null)
			atype = builder.makeTopic();

    AssociationIF assoc = builder.makeAssociation(atype);
    AssociationRoleIF role = builder.makeAssociationRole(assoc, roleType, srctopic);

    // refify the topic and assign a default name
    TopicIF reifier = builder.makeTopic();
    builder.makeTopicName(reifier, "New Association");

    // create src locator for assoc
    LocatorIF srcloc;
    try {
      srcloc= new URILocator("http://net.ontopia.identity/assoc#" + assoc.getObjectId());
    } catch (MalformedURLException e) {
      throw new ActionRuntimeException("Malformed URL for occurrence source locator: " + e);
    }

    assoc.addItemIdentifier(srcloc);

    // reify the assoc
    reifier.addSubjectIdentifier(srcloc);

    response.addParameter("associd", assoc.getObjectId());
  }
}
