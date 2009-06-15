
// $Id: PageParameter.java,v 1.12 2003/09/16 15:07:50 larsga Exp $

package net.ontopia.topicmaps.nav.taglibs.template;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectInput;

/**
 * INTERNAL: Data carrier class used to pass data between tags.
 */
public class PageParameter implements Externalizable { 

  private String content;
  private boolean direct;

  public PageParameter(String content, boolean direct) {
    setContent(content);
    setDirect(direct);
  }

  public void setContent(String s) {
    content = s;
  }
  
  public void setDirect(boolean b) {
    direct = b;
  }

  public String getContent() {
    return content;
  }
  
  public boolean isDirect() {
    return direct;
  }
  
  // --------------------------------------------------------------------------
  // Externalization
  // --------------------------------------------------------------------------
  
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeUTF(content);
    out.writeBoolean(direct);
  }

  public void readExternal(ObjectInput in) throws IOException {
    content = in.readUTF();
    direct = in.readBoolean();
  }

}
