
package net.ontopia.topicmaps.impl.utils;

import java.util.Collection;
import java.util.HashSet;

import net.ontopia.topicmaps.core.TopicMapStoreFactoryIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: A commons-pool PoolableObjectFactory that uses a
 * TopicMapStoreFactoryIF to create TopicMapStoreIF objects. This
 * class must be used with a commons-pool pool instance.
 *
 * @since 2.1
 */

public class StorePoolableObjectFactory 
  implements org.apache.commons.pool.PoolableObjectFactory {

  // define a logging category.
  static Logger log = LoggerFactory.getLogger(StorePoolableObjectFactory.class.getName());
  
  // topic map store factory
  protected TopicMapStoreFactoryIF sfactory;

  // track all open stores
  public Collection<TopicMapStoreIF> stores = new HashSet<TopicMapStoreIF>();
  
  public StorePoolableObjectFactory(TopicMapStoreFactoryIF sfactory) {
    this.sfactory = sfactory;
  }

  public Object makeObject()
    throws Exception {
    // tell store factory to create a new store instance
    TopicMapStoreIF o = sfactory.createStore();
    log.debug("makeObject " + o);
    stores.add(o);
    return o;
  }
  
  public void destroyObject(Object o)
    throws Exception {
    log.debug("destroyObject " + o);
    AbstractTopicMapStore s = (AbstractTopicMapStore)o;
    stores.remove(s);
    // close topic map store
    if (s.isOpen()) s.close(false);    
  }

  public boolean validateObject(Object o) {
    log.debug("validateObject " + o);
    // ask store to validate itself
    AbstractTopicMapStore store = (AbstractTopicMapStore)o;
    boolean valid = store.validate();
    log.debug("validate: " + valid);
    return valid;
  }

  public void activateObject(Object o)
    throws Exception {
    log.debug("activateObject " + o);
  }

  public void passivateObject(Object o)
    throws Exception {
    log.debug("passivateObject " + o);
  }

}
