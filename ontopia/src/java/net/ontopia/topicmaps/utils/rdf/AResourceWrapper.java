
// $Id: AResourceWrapper.java,v 1.5 2005/06/13 09:47:52 larsga Exp $

package net.ontopia.topicmaps.utils.rdf;

import com.hp.hpl.jena.rdf.arp.AResource;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * INTERNAL: This class is used to wrap Jena Resource objects in the
 * ARP AResource interface so that they can be streamed through the ARP
 * StatementHandler interface without requiring new objects to be
 * created.
 */
public class AResourceWrapper implements AResource {
  public Resource resource;

  public boolean isAnonymous() {
    return resource.isAnon();
  }

  public String getAnonymousID() {
    return null;
  }

  public String getURI() {
    return resource.toString();
  }

  public Object getUserData() {
    return null;
  }

  public void setUserData(Object d) {
  }

  public int hashCode() {
    return resource.hashCode();
  }

  public boolean equals(Object obj) {
    return resource.equals(obj);
  }

  public String toString() {
    return "<" + resource.toString() + ">";
  }

  public boolean hasNodeID() {
    return false;
  }

}
