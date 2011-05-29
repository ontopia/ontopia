
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
