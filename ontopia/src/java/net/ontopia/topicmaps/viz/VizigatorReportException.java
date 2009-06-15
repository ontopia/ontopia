
// $Id: VizigatorReportException.java,v 1.1 2007/09/12 09:25:54 eirik.opland Exp $

package net.ontopia.topicmaps.viz;

import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: Reports known error conditions, that can typically be corrected by
 * the user.
 */
public class VizigatorReportException extends OntopiaRuntimeException {
  public VizigatorReportException(String message) {
    super(message);
  }
}
