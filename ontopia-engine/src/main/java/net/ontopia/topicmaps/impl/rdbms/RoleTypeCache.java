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

package net.ontopia.topicmaps.impl.rdbms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import net.ontopia.persistence.proxy.CachesIF;
import net.ontopia.persistence.proxy.IdentityCollectionWrapper;
import net.ontopia.persistence.proxy.IdentityIF;
import net.ontopia.persistence.proxy.PersistentIF;
import net.ontopia.persistence.proxy.QueryCache;
import net.ontopia.persistence.proxy.RDBMSStorage;
import net.ontopia.persistence.proxy.TransactionIF;
import net.ontopia.persistence.proxy.TransactionalLookupIndexIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.impl.utils.TopicMapTransactionIF;
import net.ontopia.topicmaps.impl.utils.EventListenerIF;
import net.ontopia.topicmaps.impl.utils.EventManagerIF;
import net.ontopia.utils.PropertyUtils;

/**
 * INTERNAL:
 */
public class RoleTypeCache {

  protected TopicMapIF tm;

  protected TopicMapTransactionIF txn;

  protected TransactionIF ptxn;

  protected TransactionalLookupIndexIF<ParameterArray, Collection<AssociationRoleIF>> rolesByType;

  protected boolean qlshared;

  protected Map<ParameterArray, Collection<AssociationRoleIF>> radd = new HashMap<ParameterArray, Collection<AssociationRoleIF>>();
  protected Map<ParameterArray, Collection<AssociationRoleIF>> rrem = new HashMap<ParameterArray, Collection<AssociationRoleIF>>();

  public RoleTypeCache(TopicMapTransactionIF txn, EventManagerIF emanager,
      EventManagerIF otree) {
    this.txn = txn;
    this.ptxn = ((RDBMSTopicMapTransaction) txn).getTransaction();

    // lookup caches
    this.tm = txn.getTopicMap();
    IdentityIF tmid = ((PersistentIF) tm)._p_getIdentity();
    RDBMSStorage storage = (RDBMSStorage) ptxn.getStorageAccess().getStorage();

    if (storage.isSharedCache()) {
      this.rolesByType = new SharedQueryLookup<Collection<AssociationRoleIF>>(ptxn.getStorageAccess(),
          (QueryCache<ParameterArray, Collection<AssociationRoleIF>>) storage.getHelperObject(CachesIF.QUERY_CACHE_RT1, tmid));
      this.qlshared = true;

      // register event handlers (only needed with shared query cache)
      otree.addListener(new AssociationRoleAddedHandler(),
          AssociationRoleIF.EVENT_ADDED);
      otree.addListener(new AssociationRoleRemovedHandler(),
          AssociationRoleIF.EVENT_REMOVED);

      emanager.addListener(new EH01(), AssociationRoleIF.EVENT_SET_TYPE);
      emanager.addListener(new EH02(), AssociationRoleIF.EVENT_SET_PLAYER);

    } else {
      int lrusize = PropertyUtils
          .getInt(
              getProperty("net.ontopia.topicmaps.impl.rdbms.Cache.rolesbytype.lru"),
              1000);
      this.rolesByType = new QueryLookup<Collection<AssociationRoleIF>>("TopicIF.getRolesByType", ptxn,
          lrusize, Collections.<AssociationRoleIF>emptySet());
      this.qlshared = false;
    }
  }

  // ---------------------------------------------------------------------------
  // Other
  // ---------------------------------------------------------------------------

  protected String getProperty(String name) {
    return ptxn.getStorageAccess().getProperty(name);
  }

  // ---------------------------------------------------------------------------
  // transaction callbacks
  // ---------------------------------------------------------------------------

  public void commit() {
    if (qlshared) {
      if (!radd.isEmpty()) {
        try {
          rolesByType.removeAll(new ArrayList<ParameterArray>(radd.keySet()));
        } finally {
          radd = new HashMap<ParameterArray, Collection<AssociationRoleIF>>();
        }
      }
      if (!rrem.isEmpty()) {
        try {
          rolesByType.removeAll(new ArrayList<ParameterArray>(rrem.keySet()));
        } finally {
          rrem = new HashMap<ParameterArray, Collection<AssociationRoleIF>>();
        }
      }
    }
    rolesByType.commit();
  }

  public void abort() {
    if (qlshared) {
      radd.clear();
      rrem.clear();
    }
    rolesByType.abort();
  }

  // ---------------------------------------------------------------------------
  // Lookup methods
  // ---------------------------------------------------------------------------

  // Delegated to by TopicIF.getRolesByType(TopicIF roletype)

  public Collection<AssociationRoleIF> getRolesByType(TopicIF player, TopicIF roletype) {
    // IMPORTANT: this method will *never* be called if the topic's
    // roles property is dirty. Thus there is no need to sync the
    // query result with the txn changes, i.e. the radd and rrem maps.

    // construct query key
    ParameterArray key = new ParameterArray(new Object[] { i(tm), i(player), i(roletype) });
    Collection<AssociationRoleIF> result = rolesByType.get(key);
    return new IdentityCollectionWrapper<AssociationRoleIF>(ptxn, result);
  }

  // -----------------------------------------------------------------------------
  // Event handlers
  // -----------------------------------------------------------------------------

  protected Object i(Object value) {
    if (value instanceof PersistentIF) {
      return ((PersistentIF) value)._p_getIdentity();
    } else {
      return value;
    }
  }

  protected void addEntry(ParameterArray key, AssociationRoleIF added) {
    // + added
    Collection<AssociationRoleIF> avals = radd.get(key);
    if (avals == null) {
      avals = new HashSet<AssociationRoleIF>();
      radd.put(key, avals);
    }
    avals.add(added);
    // - removed
    Collection<AssociationRoleIF> rvals = rrem.get(key);
    if (rvals != null) {
      rvals.remove(added);
    }
  }

  protected void removeEntry(ParameterArray key, AssociationRoleIF removed) {
    // + removed
    Collection<AssociationRoleIF> rvals = rrem.get(key);
    if (rvals == null) {
      rvals = new HashSet<AssociationRoleIF>();
      rrem.put(key, rvals);
    }
    rvals.add(removed);
    // - added
    Collection<AssociationRoleIF> avals = radd.get(key);
    if (avals != null) {
      avals.remove(removed);
    }
  }

  /**
   * EventHandler: AssociationRoleIF.added
   */
  class AssociationRoleAddedHandler implements EventListenerIF {
    @Override
    public void processEvent(Object object, String event, Object new_value,
        Object old_value) {
      AssociationRoleIF added = (AssociationRoleIF) new_value;

      // ignore event if player is null
      TopicIF player = added.getPlayer();
      if (player == null) {
        return;
      }

      // register entry
      addEntry(new ParameterArray(new Object[] { i(tm), i(player), i(added.getType()) }), added);
    }
  }

  /**
   * EventHandler: AssociationRoleIF.removed
   */
  class AssociationRoleRemovedHandler implements EventListenerIF {
    @Override
    public void processEvent(Object object, String event, Object new_value,
        Object old_value) {
      AssociationRoleIF removed = (AssociationRoleIF) old_value;

      // ignore event if player is null
      TopicIF player = removed.getPlayer();
      if (player == null) {
        return;
      }

      // unregister entry
      removeEntry(new ParameterArray(new Object[] { i(tm), i(player), i(removed.getType()) }), removed);
    }
  }

  /**
   * EventHandler: AssociationRoleIF.setType
   */
  class EH01 implements EventListenerIF {
    @Override
    public void processEvent(Object object, String event, Object new_value,
        Object old_value) {
      AssociationRoleIF arole = (AssociationRoleIF) object;
      // ignore event if player is null
      TopicIF player = arole.getPlayer();
      if (player == null) {
        return;
      }

      // unregister old entry
      removeEntry(new ParameterArray(new Object[] { i(tm), i(player), i(old_value) }), arole);
      // register new entry
      addEntry(new ParameterArray(new Object[] { i(tm), i(player), i(new_value) }), arole);
    }
  }

  /**
   * EventHandler: AssociationRoleIF.setPlayer
   */
  class EH02 implements EventListenerIF {
    @Override
    public void processEvent(Object object, String event, Object new_value,
        Object old_value) {
      AssociationRoleIF arole = (AssociationRoleIF) object;

      // unregister old entry
      if (old_value != null) {
        removeEntry(new ParameterArray(new Object[] { i(tm), i(old_value), i(arole.getType()) }), arole);
      }

      // register new entry
      if (new_value != null) {
        addEntry(new ParameterArray(new Object[] { i(tm), i(new_value), i(arole.getType()) }), arole);
      }
    }
  }

}
