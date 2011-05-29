
package net.ontopia.topicmaps.utils.rdf;

import net.ontopia.utils.OntopiaRuntimeException;

import com.hp.hpl.jena.rdf.arp.ALiteral;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.shared.JenaException;

/**
 * INTERNAL: This class is used to wrap Jena Literal objects in the
 * ARP ALiteral interface so that they can be streamed through the ARP
 * StatementHandler interface without requiring new objects to be
 * created.
 */
public class ALiteralWrapper implements ALiteral {
  public Literal literal;

  public boolean isWellFormedXML() {
    return literal.isWellFormedXML();
  }

  public String getParseType() {
    return null;
  }

  public String toString() {
    try {
      return literal.getString();
    } catch (JenaException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  public String getLang() {
    return literal.getLanguage();
  }

  public String getDatatypeURI() {
    return null;
  }

  private boolean tainted;

  public void taint() {
    tainted = true;
  }

  public boolean isTainted() {
    return tainted;
  }

}

