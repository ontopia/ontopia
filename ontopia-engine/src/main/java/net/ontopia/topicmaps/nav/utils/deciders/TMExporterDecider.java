
// $Id: TMExporterDecider.java,v 1.7 2008/11/03 12:24:58 lars.garshol Exp $

package net.ontopia.topicmaps.nav.utils.deciders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.ScopedIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.utils.DeciderIF;

/**
 * INTERNAL: Accepts or rejects topic map constructs based on their
 * relations to other topic map constructs and a filter that makes
 * decisions on an individual basis.  Used by the topic map exporters
 * to support topic filtering.
 */
public class TMExporterDecider implements DeciderIF {

  // Decides whether individual TMObjectIFs should be accepted or rejected.
  private DeciderIF filter;

  /**
   * Creates a new TMExporterDecider.
   * @param filter accepts or rejects an individual object
   */
  public TMExporterDecider(DeciderIF filter) {
    if (filter == null)
      throw new NullPointerException("Filter cannot be null.");
    this.filter = filter;
  }

  /**
   * Accepts or rejects AssociationIFs, TopicNameIFs, Collections, OccurrenceIFs,
   * TopicIFs and VariantNameIFs, base on the filter and their relations to
   * other objects. @param object The object to accept or reject.
   */
  public boolean ok(Object object) {
    // Check that none of the scoping topics are disallowed.
    if (object instanceof ScopedIF) {
      ScopedIF scoped = (ScopedIF)object;
      Iterator scopeIt = scoped.getScope().iterator();
      while (scopeIt.hasNext())
        if (!ok(scopeIt.next()))
          return false;
    }

    if (object instanceof AssociationIF)
      return ok((AssociationIF)object);
    if (object instanceof TopicNameIF)
      return ok((TopicNameIF)object);
    if (object instanceof Collection)
      return ok((Collection)object);
    if (object instanceof OccurrenceIF)
      return ok((OccurrenceIF)object);
    if (object instanceof TopicIF)
      return ok((TopicIF)object);
    if (object instanceof VariantNameIF)
      return ok((VariantNameIF)object);
    return true;
  }

  /**
   * Accepts or rejects a TopicNameIF
   * @param baseName to be accepted/rejected.
   * @return true iff baseName is accepted by the filter and its scope is also
   *         accepted by the filter.
   */
  public boolean ok(TopicNameIF baseName) {
    return filter.ok(baseName) && filter.ok(baseName.getScope());
  }

  /**
   * Accepts or rejects a VariantNameIF
   * @param variantName to be accepted/rejected.
   * @return true iff variantName is accepted by the filter and its scope is
   *         also accepted by the filter.
   */
  public boolean ok(VariantNameIF variantName) {
    return filter.ok(variantName) && filter.ok(variantName.getScope());
  }

  /**
   * Return true iff the association type, each role player and each role type
   * are accepted.
   * @param association the association to test for acceptance.
   * @return true iff the association is accepted.
   */
  public boolean ok(AssociationIF association) {
    boolean retVal = ok(association.getType());
    Iterator rolesIt = association.getRoles().iterator();
    while (rolesIt.hasNext()) {
      AssociationRoleIF role = (AssociationRoleIF)rolesIt.next();
      retVal &= ok(role.getType()) && filter.ok(role.getPlayer())
          && filter.ok(role);
    }
    return retVal;
  }

  /**
   * Return true iff the type of the occurrence is accepted
   * @param occurrence The occurence to test for acceptance. It is assumed that
   *        occurrence will only be tested for acceptance if its parent topic
   *        has already been tested and passed.
   * @return true iff occurrence is accepted.
   */
  public boolean ok(OccurrenceIF occurrence) {
    return filter.ok(occurrence) && ok(occurrence.getType())
        && ok(occurrence.getScope()) && ok(occurrence.getTopic());
  }

  /**
   * Return true iff the given topic, all of it's types and all types of the
   * types (etc. recursively) are accepted by the filter that was given in the
   * constructor.
   * @param topic The topic to test for acceptance.
   * @return true iff the topic is accepted.
   */
  public boolean ok(TopicIF topic) {
    return ok(topic, new ArrayList());
  }

  /**
   * Accepts or rejects a collection of TopicsIFs.
   * @param coll The collection to test (search)
   * @return true iff whole collection of topics are accepted by filter.
   */
  private boolean ok(Collection coll) {
    Iterator it = coll.iterator();
    while (it.hasNext())
      if (!ok((TopicIF)it.next()))
        return false;
    return true;
  }

  /**
   * Return true iff the given topic, all of it's types and all types of the
   * types (etc. recursively) are accepted by the filter that was given in the
   * constructor.
   * @param topic The topic to test for acceptance.
   * @param checked Topics that have already been checked (passed).
   * @return true iff the topic is accepted.
   */
  private boolean ok(TopicIF topic, Collection checked) {
    // Only check each topic once.
    if (checked.contains(topic))
      return true;
    if (topic == null)
      return true;

    if (filter.ok(topic))
      checked.add(topic);
    else
      return false;

    return true;
  }
}
