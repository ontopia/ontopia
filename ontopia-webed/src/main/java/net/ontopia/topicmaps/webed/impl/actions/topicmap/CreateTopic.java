
package net.ontopia.topicmaps.webed.impl.actions.topicmap;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.schema.impl.osl.TopicNameConstraint;
import net.ontopia.topicmaps.schema.impl.osl.OSLSchema;
import net.ontopia.topicmaps.schema.impl.osl.ScopeSpecification;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.impl.basic.Constants;
import net.ontopia.topicmaps.webed.impl.utils.ActionSignature;
import net.ontopia.topicmaps.webed.impl.utils.SchemaUtils;

/**
 * PUBLIC: Action for creating a new topic. Topic type may be specified
 * using a parameter.
 */
public class CreateTopic extends AbstractTopicMapAction {

  public void perform(ActionParametersIF params, ActionResponseIF response) {
    // create new topic
    
    ActionSignature paramsType = ActionSignature.getSignature("m t?");
    paramsType.validateArguments(params, this);
    
    TopicMapIF topicmap = (TopicMapIF) params.get(0);
    TopicMapBuilderIF builder = topicmap.getBuilder();
    TopicIF topic = builder.makeTopic();

    // retrieve (optional) topic type from context parameter
    Collection types = params.getCollection(1);
    if (types == null) {
      TopicIF type = (TopicIF) params.getTMObjectValue();
      if (type != null)
        types = Collections.singleton(type);
    }
    if (types != null) {
      Iterator it = types.iterator();
      while (it.hasNext()) {
        TopicIF type = (TopicIF) it.next();
        topic.addType(type);
      }
    }

    // modify this topic based on the schema information
    createTopicNames(topicmap, topic, builder, getSchema());
    // FIXME: need occurrences and association roles here as well
    
    response.addParameter(Constants.RP_TOPIC_ID, topic.getObjectId());
  }

  // --------------------------------------------------------------
  // internal method
  // --------------------------------------------------------------

  /**
   * INTERNAL: Get information about topic name constraints from schema,
   * and create topic names in proper scope in accordance to the schema.
   */
  private void createTopicNames(TopicMapIF topicmap, TopicIF topic,
                               TopicMapBuilderIF builder, OSLSchema schema) {
    if (schema != null) {
      SchemaUtils su = new SchemaUtils();
      Collection bnC = su.getAllTopicNameConstraints(schema, topic);
      if (bnC.size() > 0) {
        Iterator itC = bnC.iterator();
        // loop over all topic name constraints
        while (itC.hasNext()) {
          TopicNameConstraint constraint = (TopicNameConstraint) itC.next();
          int min = constraint.getMinimum();
          //! int max = constraint.getMaximum();
          ScopeSpecification scsp = constraint.getScopeSpecification();
          Collection topicThemes = su.getMatchingTopics(topicmap, scsp);
          // create a new topic name if at least one is need for this context
          if (min > 0) {
            TopicNameIF name = builder.makeTopicName(topic, "");
            // set the scope of this name
            Iterator itT = topicThemes.iterator();
            while (itT.hasNext())
              name.addTheme((TopicIF) itT.next());
          }
        } // while itC
      }
    } // if schema
  }
  
}
