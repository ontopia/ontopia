
// $Id: JGroupsEvent.java,v 1.3 2007/09/27 12:20:22 geir.gronmo Exp $

package net.ontopia.persistence.proxy;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * INTERNAL: 
 */

public class JGroupsEvent implements java.io.Externalizable {

  public int eventType;
  public Object value;
  public IdentityIF namespace;
  public int field;
  
  public String toString() {
    return eventType + " " + namespace + " " + value + " " + field;
  }

  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeInt(eventType);
    out.writeObject(namespace);
    out.writeObject(value);
    out.writeInt(field);
  }

  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    eventType = in.readInt();
    namespace = (IdentityIF)in.readObject();
    value = in.readObject();
    field = in.readInt();
  }
  
}
