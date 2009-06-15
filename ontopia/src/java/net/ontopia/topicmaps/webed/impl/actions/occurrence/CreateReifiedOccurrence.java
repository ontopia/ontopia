
// $Id: CreateReifiedOccurrence.java,v 1.12 2008/06/13 08:17:56 geir.gronmo Exp $

package net.ontopia.topicmaps.webed.impl.actions.occurrence;

import java.net.MalformedURLException;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.core.ActionRuntimeException;
import net.ontopia.topicmaps.webed.impl.basic.Constants;

/**
 * PUBLIC: Action for adding an occurrence to a topic. The type
 * of the occurrence can also be provided.  
 * In addition the occurrence created is reified by a new topic.
 *
 * @since 2.0
 */
public class CreateReifiedOccurrence implements ActionIF {

  public void perform(ActionParametersIF params, ActionResponseIF response) {

    TopicIF topic = (TopicIF) params.get(0);
    TopicIF occtype = (TopicIF) params.get(1);
    
    TopicMapBuilderIF builder = topic.getTopicMap().getBuilder();

    // create new (external) occurrence for topic
    URILocator locator = null;
    try {
      locator = new URILocator(Constants.DUMMY_LOCATOR);
    } catch (MalformedURLException e) {
      throw new ActionRuntimeException("Malformed URL for occurrence: " + e);
    }

    OccurrenceIF occurrence = builder.makeOccurrence(topic, occtype, locator);

    // refify the topic and assign a basic name
    TopicIF reifier = builder.makeTopic();
    builder.makeTopicName(reifier, "Navn");

    // create src locator for occurrence
    LocatorIF srcloc;
    try {
			srcloc= new URILocator("http://net.ontopia.identity/occur#" + occurrence.getObjectId());
    } catch (MalformedURLException e) {
      throw new ActionRuntimeException("Malformed URL for occurrence source locator: " + e);
    }

    occurrence.addItemIdentifier(srcloc);

    // reify the occurrnce by hand
    reifier.addSubjectIdentifier(srcloc);

    System.out.println("store occurid : " + occurrence.getObjectId());

    response.addParameter("occurid", occurrence.getObjectId());
  }
}
