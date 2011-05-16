// $Id: ScopeSupportIF.java,v 1.4 2004/11/12 11:25:23 grove Exp $

package net.ontopia.topicmaps.nav2.core;

/**
 * INTERNAL: Interface which defines basic properties (like constants)
 * needed by classes that implement scope access.
 */
public interface ScopeSupportIF {

  // --- constants
  // for specifying the scope type
  final int SCOPE_BASENAMES    = 1;
  final int SCOPE_VARIANTS     = 2;
  final int SCOPE_OCCURRENCES  = 3;
  final int SCOPE_ASSOCIATIONS = 4;

  // for specifying the decider type
  final String DEC_INTERSECTION  = "intersection";
  final String DEC_APPLICABLE_IN = "applicableIn";
  final String DEC_WITHIN        = "within";
  final String DEC_SUPERSET      = "superset";
  final String DEC_SUBSET        = "subset";

}





