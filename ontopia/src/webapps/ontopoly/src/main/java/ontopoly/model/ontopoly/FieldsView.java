
// $Id: FieldsView.java,v 1.2 2009/04/08 08:59:04 geir.gronmo Exp $

package ontopoly.model;

import java.util.Iterator;

import ontopoly.utils.OntopolyModelUtils;

import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.utils.ObjectUtils;

/**
 * Represents a fields view.
 */
public class FieldsView extends Topic {

  private int isEmbeddedView = -1;
  
  public FieldsView(TopicIF topic, TopicMap tm) {
    super(topic, tm);
  }

  public boolean equals(Object obj) {
    if (!(obj instanceof FieldsView))
      return false;

    FieldsView other = (FieldsView) obj;
    return (getTopicIF().equals(other.getTopicIF()));
  }

  public boolean isEmbeddedView() {
    // NOTE: value is cached
    if (isEmbeddedView == 0)
      return false;
    else if (isEmbeddedView == 1)
      return true;
    
    // view is embedded is part of on:is-embedded-view(x : on:fields-view)
    TopicIF associationType = OntopolyModelUtils.getTopicIF(getTopicMap(), PSI.ON_IS_EMBEDDED_VIEW);    
    Iterator iter = getTopicIF().getRoles().iterator();
    while (iter.hasNext()) {
      AssociationRoleIF role = (AssociationRoleIF)iter.next();
      AssociationIF assoc = role.getAssociation();
      if (ObjectUtils.equals(assoc.getType(), associationType)) {
        isEmbeddedView = 1;
        return true;
      }
    }
    isEmbeddedView = 0;
    return false;
  }
  
  public static FieldsView getDefaultFieldsView(TopicMap tm) {
    return new FieldsView(OntopolyModelUtils.getTopicIF(tm, PSI.ON_DEFAULT_FIELDS_VIEW), tm);
  }

  public boolean isDefaultView() {
    return getTopicIF().getSubjectIdentifiers().contains(PSI.ON_DEFAULT_FIELDS_VIEW);
  }

}
