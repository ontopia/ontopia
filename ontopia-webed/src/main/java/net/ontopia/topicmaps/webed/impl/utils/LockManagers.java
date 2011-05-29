
package net.ontopia.topicmaps.webed.impl.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.ResourceBundle;

import javax.servlet.http.HttpSession;

import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.nav2.core.UserIF;
import net.ontopia.utils.ObjectUtils;
import net.ontopia.utils.CollectionUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Factory class for getting hold of lock managers.
 */
public final class LockManagers {

  private static Map lockManagers = CollectionUtils.createConcurrentMap();

  public static NamedLockManager getLockManager(String identifier) {
    synchronized (lockManagers) {
      NamedLockManager lockManager = (NamedLockManager)lockManagers.get(identifier);
      if (lockManager != null)
        return lockManager;
      else
        return createLockManager(identifier);
    }
  }
  
  public static NamedLockManager createLockManager(String identifier) {
    synchronized (lockManagers) {
      NamedLockManager lockManager = new NamedLockManager();
      lockManagers.put(identifier, lockManager);
      return lockManager;
    }
  }
    
}
