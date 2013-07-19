/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

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
