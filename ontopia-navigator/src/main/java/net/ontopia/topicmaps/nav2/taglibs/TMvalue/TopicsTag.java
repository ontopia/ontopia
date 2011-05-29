
package net.ontopia.topicmaps.nav2.taglibs.TMvalue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Collection;
import java.util.Iterator;
import javax.servlet.jsp.JspTagException;

import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.nav2.taglibs.value.BaseValueProducingAndAcceptingTag;

/**
 * INTERNAL: Value Producing Tag for finding all the topics of all the
 * topic map objects (allowed instances are TopicMapIF, TopicIF,
 * AssociationRoleIF, AssociationIF, TopicNameIF, VariantNameIF and
 * OccurrenceIF) in a collection.
 */
public class TopicsTag extends BaseValueProducingAndAcceptingTag {

  public Collection process(Collection tmObjs) throws JspTagException {
    // find all topics of all topic map objects in collection
    Set topics = new HashSet();
    if (tmObjs != null) {
      Iterator iter = tmObjs.iterator();
      while (iter.hasNext()) {
        Object obj = iter.next();
        if (obj instanceof AssociationRoleIF) {
          AssociationRoleIF role = (AssociationRoleIF) obj;
          if (role.getPlayer() != null)
            topics.add( role.getPlayer() );
        } else if (obj instanceof AssociationIF) {
          Collection roles = ((AssociationIF) obj).getRoles();
          Iterator it = roles.iterator();
          while (it.hasNext()) {
            AssociationRoleIF role = (AssociationRoleIF) it.next();
            topics.add( role.getPlayer() );
          }
        } else if (obj instanceof TopicNameIF) {
          topics.add( ((TopicNameIF) obj).getTopic() );
        } else if (obj instanceof VariantNameIF) {
          topics.add( ((VariantNameIF) obj).getTopic() );
        } else if (obj instanceof OccurrenceIF) {
          topics.add( ((OccurrenceIF) obj).getTopic() );
        } else if (obj instanceof TopicMapIF) {
          topics.addAll( ((TopicMapIF) obj).getTopics() );
        } else if (obj instanceof TopicIF) {
          topics.add( (TopicIF) obj );
        }
          
      } // while
    }

    return new ArrayList(topics);
  }

}
