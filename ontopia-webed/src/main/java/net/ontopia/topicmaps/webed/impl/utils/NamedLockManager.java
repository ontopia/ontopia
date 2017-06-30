/*
 * #!
 * Ontopia Webed
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

package net.ontopia.topicmaps.webed.impl.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.servlet.http.HttpSession;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.nav2.core.UserIF;
import net.ontopia.utils.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Class to handle basic lock controlling for objects in the web
 * editor framework.
 */
public class NamedLockManager {

  // initialization of logging facility
  private static Logger logger = LoggerFactory.getLogger(NamedLockManager.class
      .getName());

  private Map locked;  // key: Object(locked object), value: UserIF
  private Map nameLocks; // key: String(name), value: NamedLock
  private Map userLocks; // key: UserIF(user), value: NamedLock[]
  private Map objectLocks; // key: Object(locked object), value: NamedLock

  private UniqueStringCreator suffixCreator = new UniqueStringCreator();
  
  public NamedLockManager() {
    locked = CollectionUtils.createConcurrentMap();
    nameLocks = CollectionUtils.createConcurrentMap();
    userLocks = CollectionUtils.createConcurrentMap();
    objectLocks = CollectionUtils.createConcurrentMap();
    logger.info("NamedLockManager initialised");
  }
  
  /**
   * INTERNAL: Attempts to lock all the objects in the input collection and
   * assigns name to the collection for later retrieval.
   */
  public synchronized LockResult attemptToLock(UserIF user, Collection objects,
                                               String nameBase, 
                                               HttpSession session) {
    if (nameBase == null)
      throw new IllegalArgumentException("Lock name must be specified.");
    String name = nameBase + "$" + suffixCreator.getNextUniqueString();

    objects = makeSerializable(objects);
    
    // Remove expired locks
    expireNamedLocks(session);

    // Attempt to lock objects 
    logger.debug("locking '" + name + "' for " + user);
    Collection unlockable = _attemptToLock(user, objects);
    if (!unlockable.isEmpty())
      // Let the client know that this lock was never given in the first place.
      name += "-unlocked";
    
    if (unlockable.isEmpty()) {
      // Update named lock map
      NamedLock nlock = createNamedLock(name, user, objects, session);
      nameLocks.put(name, nlock);
      if (logger.isDebugEnabled())
        logger.debug("--> (lock) " + nameLocks);
      logger.info("Registered locked objects under '" + name + "'.");
      
      // Update list of user's locks
      Collection nlocks = (Collection)userLocks.get(user);
      if (nlocks == null) {
        nlocks = CollectionUtils.createConcurrentSet();
        userLocks.put(user, nlocks);
      }
      nlocks.add(nlock);
    }

    Iterator unlockableIt = unlockable.iterator();
    Object firstUnlockable = unlockableIt.hasNext() ?
        unlockableIt.next() : null;
    NamedLock unlockableLock = (firstUnlockable == null ? null : (NamedLock)objectLocks.get(firstUnlockable));
    return new LockResult(unlockable, unlockableLock, name);
  }

  private synchronized Collection _attemptToLock(Object user, Collection objects) {
    
    Collection unlockable = null;
    Object lockednow[] = new Object[objects.size()];
    int lockedCount = 0;
    Iterator it = objects.iterator();

    while (it.hasNext()) {
      Object object = it.next();

      Object _user = locked.get(object);
      if (_user != null && !_user.equals(user)) {
        if (unlockable == null)
          unlockable = new HashSet();
        unlockable.add(object);
      } else {
        logger.debug("Locking " + object + " for " + user);
        locked.put(object, user);
        lockednow[lockedCount++] = object;
      }
    } // while

    // unlock locked objects
    if (unlockable != null) {
      for (int ix = 0; ix < lockedCount; ix++) {
        logger.debug("Unlocking " + lockednow[ix] + " for " + user);
        locked.remove(lockednow[ix]);
      }
    }

    return (unlockable == null ? Collections.EMPTY_SET : unlockable);
  }
  
  private NamedLock createNamedLock(String name, UserIF user,
      Collection objects, HttpSession session) {
    NamedLock namedLock = new NamedLock(name, user, objects);
    namedLock.setExpiry(session);
    
    Iterator objectsIt = objects.iterator();
    while (objectsIt.hasNext()) {
      Object currentObject = objectsIt.next();
      objectLocks.put(currentObject, namedLock);
    }
      
    return namedLock;
  }
  
  /**
   * INTERNAL:
   * If forced is false, unlocks the lock with the given 'name' for the given 
   * user.
   * If forced is true, unlocks all locks starting with the stem of 'name', the
   * stem being defined as everything before and including the last '$'
   * character. These locks are unlocked independent on which user locked them. 
   */
  public synchronized void unlock(UserIF user, String name, boolean forced) {
    if (forced) {
      logger.warn("Forced unlock of " + name);
      Collection allNames = namesWithSameStem(name);
      Iterator allNamesIt = allNames.iterator();
      while (allNamesIt.hasNext()) {
        String currentName = (String)allNamesIt.next();
        NamedLock namedLock = (NamedLock)nameLocks.get(currentName);
        _unlock(namedLock.getUser(), currentName);
      }
      logger.warn("Forced unlock completed.");
    } else
      _unlock(user, name);
  }
  
  /**
   * INTERNAL:
   * Finds all names in the nameLocks.keySet() that start with the stem of
   * 'name'. The stem is defined as everything before and including the last '$'
   * character in 'name'.
   * @param name The source name.
   * @return All names having the same stem as 'name'.
   */
  private Collection namesWithSameStem(String name) {
    String stemmedName = name.substring(0, name.lastIndexOf("$") + 1);
    
    Collection retVal = new HashSet();
    Iterator namesIt = nameLocks.keySet().iterator();
    while (namesIt.hasNext()) {
      String currentName = (String)namesIt.next();
      if (currentName.startsWith(stemmedName));
        retVal.add(currentName);
    }
    return retVal;
  }

  private void _unlock(UserIF user, String name) {
    logger.debug("unlocking '" + name + "' for " + user);
    NamedLock nlock = (NamedLock) nameLocks.get(name);
    
    if (logger.isDebugEnabled())
      logger.debug("--> (unlock) " + nameLocks);

    if (nlock != null) {
      // update named lock map
      logger.debug("Trying to unlock objects from '" + name + "'.");
      
      // Update list of user's locks
      // IMPORTANT: This needs to happen before the canUnlock() call below.
      Collection ulocks = (Collection)userLocks.get(user);
      ulocks.remove(nlock);

      // Unlock objects
      _unlock(user, canUnlock(user, nlock.objects));

      nameLocks.remove(name);
      
      Iterator lockedIt = nlock.objects.iterator();
      while (lockedIt.hasNext()) {
        Object currentObject = lockedIt.next();
        if (objectLocks.get(currentObject) == nlock)
          objectLocks.remove(currentObject);
      }
    } else
      logger.warn("Unlocking not possible, '" + name + "' not known.");
  }

  private void _unlock(Object user, Collection objects) {
    
    Set unlocked = new HashSet();
    
    Iterator it = objects.iterator();
    while (it.hasNext()) {
      Object object = it.next();

      if (unlocked.contains(object))
        continue;

      // unlock individual object
      try {
        logger.debug("Unlocking " + object + " for " + user);
        Object _user = locked.get(object);
    
        if (_user == null) {
          logger.warn("User " + user + " attempted to unlock object '" + object
                      + "' which was not locked");
        } else if (!_user.equals(user)) {
          logger.warn("Attempted to unlock object '" + object
                      + "' which was locked by a " + "different user");
        } else {
          locked.remove(object);
        }
      } catch (Throwable e) {
        e.printStackTrace();
      }
      unlocked.add(object);
    }
  }

  private Collection canUnlock(UserIF user, Collection unlockCandidates) {
    Collection canUnlock = new HashSet(unlockCandidates);
    
    // Remove from canUnlock, candidates that should remain locked by this user.
    Collection userNamedLocks = (Collection)userLocks.get(user);
    Iterator userNamedLocksIt = userNamedLocks.iterator();
    while (userNamedLocksIt.hasNext()) {
      NamedLock lock = (NamedLock)userNamedLocksIt.next();
      canUnlock.removeAll(lock.objects);
    }
    
    return canUnlock;
  }

  /**
   * INTERNAL: Returns true if the user owns the given lock.
   */
  public synchronized boolean ownsLock(UserIF user, String name) {
    NamedLock lock = (NamedLock) nameLocks.get(name);
    if (lock == null) 
      return false;
    else
      return lock.user.equals(user);
  }

  /**
   * INTERNAL: Releases all the locks that the user owns.
   */
  public synchronized void releaseLocksFor(UserIF user) {
    logger.debug("unlocking objects held by " + user);
    
    Collection releaseableLocks = new ArrayList();
    
    Iterator lockedIterator = nameLocks.keySet().iterator();
    while (lockedIterator.hasNext()) {
      String name = (String)lockedIterator.next();
      NamedLock lock = (NamedLock)nameLocks.get(name);
      if (user.equals(lock.user))
        releaseableLocks.add(name);
    }

    Iterator iter = releaseableLocks.iterator(); 
    while (iter.hasNext()) {
      String name = (String) iter.next();
      unlock(user, name, false);
    }
  }
  
  // --- Internal methods

  /**
   * INTERNAL: Returns true if the session has support for time-based
   * lock expiry. For internal and testing purposes only.
   */
  public static boolean usesTimedLockExpiry(HttpSession session) {
    // Lock expiration is only carried out in containers
    // that do not support servlets 2.3, i.e. J2EE 1.2
    return session.getServletContext().getMajorVersion() <= 2
        && session.getServletContext().getMinorVersion() < 3;
  }

  /**
   * INTERNAL: Returns the number of locks held by the user. For
   * testing purposes only.
   */
  public synchronized int lockCountFor(Object user) {
    Collection ulocks = (Collection)userLocks.get(user);
    if (ulocks == null)
      return 0;
    else
      return ulocks.size();
  }

  private void expireNamedLocks(HttpSession session) {
    if (!NamedLockManager.usesTimedLockExpiry(session))
      return;

    Iterator it = nameLocks.keySet().iterator();
    while (it.hasNext()) {
      String name = (String)it.next();
      NamedLock lock = (NamedLock) nameLocks.get(name);

      if (lock.hasExpired()) {
        logger.debug("Lock '" + name + "' expired");
        unlock(lock.user, name, false);
      }
    }
  }

  private Collection makeSerializable(Collection objects) {
    Collection result = new ArrayList(objects.size());
    Iterator iter = objects.iterator();
    while (iter.hasNext()) {
      Object object = iter.next();
      if (object instanceof TMObjectIF) {
        result.add(new TMObjectIFHandle((TMObjectIF)object));
      } else {
        result.add(object);
      }
    }
    return result;
  }

  static class TMObjectIFHandle {
    
    private String objectId;
    private String topicmapId;
    private String referenceId;
    
    public TMObjectIFHandle(TMObjectIF o) {
      this.objectId = o.getObjectId();
      TopicMapIF tm = o.getTopicMap();
      this.topicmapId = tm.getObjectId();
      TopicMapReferenceIF ref = tm.getStore().getReference();
      if (ref != null)
        this.referenceId = ref.getId();
    }
    
    public boolean equals(Object o) {
      if (!(o instanceof TMObjectIFHandle)) return false;
      TMObjectIFHandle other = (TMObjectIFHandle)o;
      return (Objects.equals(this.objectId, other.objectId) &&
              Objects.equals(this.topicmapId, other.topicmapId) &&
              Objects.equals(this.referenceId, other.referenceId));
    }

    public int hashCode() {
      return objectId.hashCode();
    }
  }

  /**
   * INTERNAL: For testing purposes only.
   */
  public void clear() {
    locked.clear();
    nameLocks.clear();
    userLocks.clear();
    objectLocks.clear();
  }
  
}
