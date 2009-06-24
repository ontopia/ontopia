// $Id:$

package net.ontopia.topicmaps.impl.tmapi2;

import net.ontopia.infoset.core.LocatorIF;

/**
 * INTERNAL: OKS->TMAPI 2 object wrapper.
 */

public class LocatorImpl implements org.tmapi.core.Locator {

  private LocatorIF wrapped;

  public LocatorImpl(LocatorIF delegate) {
    this.wrapped = delegate;
  }

  LocatorIF getWrapped() {
    return wrapped;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Locator#getReference()
   */
  
  public String getReference() {
    return wrapped.getAddress();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Locator#resolve(java.lang.String)
   */
  
  public org.tmapi.core.Locator resolve(String ref) {
    return new LocatorImpl(wrapped.resolveAbsolute(ref));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Locator#toExternalForm()
   */
  
  public String toExternalForm() {
    return wrapped.getExternalForm();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  
  public boolean equals(Object obj) {
    return obj instanceof LocatorImpl
        && getReference().equals(((LocatorImpl) obj).getReference());
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  
  public int hashCode() {
    return wrapped.hashCode();
  }

}
