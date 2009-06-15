
// $Id: AbstractTMAPIIndex.java,v 1.2 2008/06/11 17:14:48 geir.gronmo Exp $

package net.ontopia.topicmaps.impl.oks2tmapi.index;

import net.ontopia.topicmaps.core.index.IndexIF;

/**
 * INTERNAL: OKS->TMAPI object wrapper.
 */

public abstract class AbstractTMAPIIndex implements org.tmapi.index.Index, org.tmapi.index.IndexFlags {

  protected abstract IndexIF getOther();

  public void open()
    throws org.tmapi.index.TMAPIIndexException {
    // no-op
  }
   
  public void close()
    throws org.tmapi.index.TMAPIIndexException {
    // no-op
  }

  public boolean isOpen()
    throws org.tmapi.index.TMAPIIndexException {
    // always true
    return true;
  }
   
  public void reindex()
    throws org.tmapi.index.TMAPIIndexException {
  }

  public org.tmapi.index.IndexFlags getFlags()
    throws org.tmapi.index.TMAPIIndexException {
    return this;
  }
  
  public boolean isAutoUpdated() {
    return true;
  }

}
