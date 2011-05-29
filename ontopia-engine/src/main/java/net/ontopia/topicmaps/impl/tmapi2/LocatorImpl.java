
package net.ontopia.topicmaps.impl.tmapi2;

import org.tmapi.core.Locator;

import net.ontopia.infoset.core.LocatorIF;

/**
 * INTERNAL: OKS->TMAPI 2 object wrapper.
 */

public class LocatorImpl implements Locator {

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
  
  public Locator resolve(String ref) {
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
    return obj instanceof Locator
        && getReference().equals(((Locator) obj).getReference());
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
