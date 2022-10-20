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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import net.ontopia.persistence.proxy.AccessRegistrarIF;
import net.ontopia.persistence.proxy.CachesIF;
import net.ontopia.persistence.proxy.FieldInfoIF;
import net.ontopia.persistence.proxy.IdentityIF;
import net.ontopia.persistence.proxy.ObjectRelationalMappingIF;
import net.ontopia.persistence.proxy.PersistentIF;
import net.ontopia.persistence.proxy.RDBMSAccess;
import net.ontopia.persistence.proxy.RDBMSStorage;
import net.ontopia.persistence.proxy.SQLGenerator;
import net.ontopia.persistence.proxy.SQLObjectAccess;
import net.ontopia.persistence.proxy.StorageCacheIF;
import net.ontopia.persistence.proxy.TicketIF;
import net.ontopia.persistence.proxy.TransactionIF;
import net.ontopia.persistence.proxy.TransactionalLookupIndexIF;
import net.ontopia.persistence.proxy.TransactionalSoftHashMapIndex;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.impl.utils.TopicMapTransactionIF;
import net.ontopia.topicmaps.impl.utils.EventListenerIF;
import net.ontopia.topicmaps.impl.utils.EventManagerIF;
import net.ontopia.topicmaps.query.impl.utils.Prefetcher;
import net.ontopia.utils.OntopiaRuntimeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: 
 */

public class RoleTypeAssocTypeCache {
  
  // Define a logging category.
  private static final Logger log = LoggerFactory.getLogger(RoleTypeAssocTypeCache.class.getName());
  
  protected RDBMSAccess access;
  protected AccessRegistrarIF registrar;
  
  protected FieldInfoIF TopicIF_idfield;
  protected FieldInfoIF TopicMapIF_idfield;  
  protected FieldInfoIF AssociationIF_idfield;  
  protected FieldInfoIF AssociationRoleIF_idfield;  
  
  protected String sql;
  protected String sql_individual;
  protected int batchSize = SQLObjectAccess.batchSize;
  
  protected TopicMapIF tm;
  protected TopicMapTransactionIF txn;  
  protected TransactionIF ptxn;
  
  protected TransactionalLookupIndexIF<ParameterArray, Collection<IdentityIF>> rolesByType;
  protected boolean qlshared;
  
  protected Map<ParameterArray, Collection<AssociationRoleIF>> radd = new HashMap<ParameterArray, Collection<AssociationRoleIF>>();
  protected Map<ParameterArray, Collection<AssociationRoleIF>> rrem = new HashMap<ParameterArray, Collection<AssociationRoleIF>>();
  
  private final static int[] Prefetcher_RBT_fields = 
    new int[] { Prefetcher.AssociationIF_roles,
    Prefetcher.AssociationRoleIF_player };
  
  private final static boolean[] Prefetcher_RBT_traverse =
    new boolean[] { false, false };
  
  public RoleTypeAssocTypeCache(TopicMapTransactionIF txn, EventManagerIF emanager,
      EventManagerIF otree) {
    this.txn = txn;
    this.ptxn = ((RDBMSTopicMapTransaction)txn).getTransaction();
    
    RDBMSStorage storage = (RDBMSStorage)ptxn.getStorageAccess().getStorage();
    StorageCacheIF scache = storage.getStorageCache();
    
    this.access = (RDBMSAccess)ptxn.getStorageAccess();
    this.registrar = (scache == null ? ptxn.getAccessRegistrar() : scache.getRegistrar());
    
    // lookup query caches
    this.tm = txn.getTopicMap();
    IdentityIF tmid = ((PersistentIF)txn.getTopicMap())._p_getIdentity();
    if (storage.isSharedCache()) {
      this.rolesByType = (TransactionalLookupIndexIF<ParameterArray, Collection<IdentityIF>>)storage.getHelperObject(CachesIF.QUERY_CACHE_RT2, tmid);
      this.qlshared = true;
      
    } else {
      // ISSUE: need lru?
      this.rolesByType = new TransactionalSoftHashMapIndex<ParameterArray, Collection<IdentityIF>>();
      this.qlshared = false;
    }
      
    // register event handlers
    otree.addListener(new AssociationRoleAddedHandler(), AssociationRoleIF.EVENT_ADDED);
    otree.addListener(new AssociationRoleRemovedHandler(), AssociationRoleIF.EVENT_REMOVED);
    
    emanager.addListener(new EH01(), AssociationRoleIF.EVENT_SET_TYPE);
    emanager.addListener(new EH02(), AssociationRoleIF.EVENT_SET_PLAYER);
    emanager.addListener(new EH03(), AssociationIF.EVENT_SET_TYPE);
    
    // get mapping data
    ObjectRelationalMappingIF mapping = storage.getMapping();
    
    // get identity fields
    this.TopicIF_idfield = mapping.getClassInfo(Topic.class).getIdentityFieldInfo();
    this.TopicMapIF_idfield = mapping.getClassInfo(TopicMap.class).getIdentityFieldInfo();
    this.AssociationIF_idfield = mapping.getClassInfo(Association.class).getIdentityFieldInfo();
    this.AssociationRoleIF_idfield = mapping.getClassInfo(AssociationRole.class).getIdentityFieldInfo();
    
    // build query strings
    StringBuilder sb = new StringBuilder();
    sb.append("select r.player_id, r.id, r.assoc_id from TM_ASSOCIATION_ROLE r, TM_ASSOCIATION a where r.topicmap_id = ? and r.type_id = ? and r.assoc_id = a.id and a.topicmap_id = ? and a.type_id = ? and r.player_id in (");
    for (int i=0; i < batchSize; i++) {
      if (i > 0) { 
        sb.append(", ?");
      } else {
        sb.append('?');
      }
    }
    sb.append(')');
    this.sql = sb.toString();
    
    this.sql_individual = "select r.id, a.id from TM_ASSOCIATION_ROLE r, TM_ASSOCIATION a where r.topicmap_id = ? and r.type_id = ? and r.assoc_id = a.id and a.topicmap_id = ? and a.type_id = ? and r.player_id = ?";
  }
  
  // -----------------------------------------------------------------------------
  // transaction callbacks
  // -----------------------------------------------------------------------------
  
  public void commit() {    
    if (qlshared) {
      // invalidate shared query cache entries
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
  
  // -----------------------------------------------------------------------------
  // prefetch and lookup methods
  // -----------------------------------------------------------------------------
  
  public void prefetchRolesByType(Collection players, 
      TopicIF rtype, TopicIF atype) {
    // TODO: 
    //
    // - foreach player check query cache for key: new Object[] { player, rtype, atype }}
    // - for those players that don't have an entry in the query cache:
    //   - execute individual query
    //   - update shared cache
    //   - update query cache
    
    // ISSUES:
    //
    // - execute query through shared access
    // - notify shared cache registrar
    
    try {

      if (!ptxn.isClean()) {
        return;
      }
      
      // flush transaction if not shared
      if (!qlshared) {
        ptxn.flush();
      }

      // get identities
      IdentityIF rtypeid = i(rtype);
      IdentityIF atypeid = i(atype);
      IdentityIF tmid = i(tm);
      
      // check cache and prepare result collection
      IdentityIF[] key = new IdentityIF[] { null, rtypeid, atypeid };
      ParameterArray params = new ParameterArray(key);
      
      Map<IdentityIF, Collection<IdentityIF>> rbt = new HashMap<IdentityIF, Collection<IdentityIF>>(players.size());
      Iterator iter = players.iterator();
      while (iter.hasNext()) {
        key[0] = i(iter.next());
        // filter out parameters that are already in the cache
        if (key[0] != null && rolesByType.get(params) == null) {
          rbt.put(key[0], new HashSet<IdentityIF>());
        }
      }
      if (rbt.size() < 1) {
        return;
      }
      
      // collection associations for prefetching
      Collection<IdentityIF> assocs = new HashSet<IdentityIF>();

      // Get ticket
      TicketIF ticket = registrar.getTicket();
      
      // run batch query
      Connection conn = access.getConnection();
      PreparedStatement stm = conn.prepareStatement(sql);
      stm.setFetchSize(1000);
      
      Collection<IdentityIF> filteredPlayerIds = rbt.keySet();
      Iterator<IdentityIF> filteredPlayerIter = filteredPlayerIds.iterator();
      
      try {
        
        while (filteredPlayerIter.hasNext()) {
          
          int offset = 1;
          // bind: r.topicmap_id
          offset = bind(TopicMapIF_idfield, tmid, stm, offset);
          // bind: r.type_id
          offset = bind(TopicIF_idfield, rtypeid, stm, offset);
          // bind: a.topicmap_id
          offset = bind(TopicMapIF_idfield, tmid, stm, offset);
          // bind: a.type_id
          offset = bind(TopicIF_idfield, atypeid, stm, offset);
          // bind: r.player_id*
          SQLGenerator.bindMultipleParameters(filteredPlayerIter, 
              TopicIF_idfield, stm, offset, batchSize);
          
          // execute statement
          if (log.isDebugEnabled()) {
            log.debug("Executing: " + sql);
          }
          ResultSet rs = stm.executeQuery();
          
          // zero or more rows expected
          while (rs.next()) {
            offset = 1;
            // load player identity
            IdentityIF pid = (IdentityIF)TopicIF_idfield.load(registrar, ticket, rs, offset, false);
            offset += TopicIF_idfield.getColumnCount();
            // load association role identity
            IdentityIF rid = (IdentityIF)AssociationRoleIF_idfield.load(registrar, ticket, rs, offset, false);
            offset += AssociationRoleIF_idfield.getColumnCount();
            // load association identity
            IdentityIF aid = (IdentityIF)AssociationIF_idfield.load(registrar, ticket, rs, offset, false);
            offset += AssociationIF_idfield.getColumnCount();
            
            // update roles list
            Collection<IdentityIF> roles = rbt.get(pid);
            roles.add(rid);
            
            // collect association
            assocs.add(aid);
            
            // update storage cache
            registrar.registerField(ticket, rid, Prefetcher.AssociationRoleIF_topicmap, tmid);
            registrar.registerField(ticket, rid, Prefetcher.AssociationRoleIF_type, rtypeid);
            registrar.registerField(ticket, rid, Prefetcher.AssociationRoleIF_player, pid);
            registrar.registerField(ticket, rid, Prefetcher.AssociationRoleIF_association, aid);
            
            registrar.registerField(ticket, aid, Prefetcher.AssociationIF_topicmap, tmid);
            registrar.registerField(ticket, aid, Prefetcher.AssociationIF_type, atypeid);
          }
          // close result set
          rs.close();
        }
        
      } finally {
        if (stm != null) {
          stm.close();
        }
      }
      
      // update query cache
      filteredPlayerIter = filteredPlayerIds.iterator();
      while (filteredPlayerIter.hasNext()) {
        IdentityIF playerid = filteredPlayerIter.next();
        Collection<IdentityIF> r = rbt.get(playerid);
        ParameterArray k = new ParameterArray(new Object[] { playerid, rtypeid, atypeid });
        rolesByType.put(k, r);
      }
      
      // prefetch A.roles.player
      //! System.out.println("PF: " + rtypeid + " " + atypeid + " " + assocs.size());
      ptxn.prefetch(Association.class, 
          Prefetcher_RBT_fields, Prefetcher_RBT_traverse, 
          assocs);
      
    } catch (SQLException e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
  public Collection<AssociationRoleIF> getRolesByType(TopicIF player, 
      TopicIF rtype, TopicIF atype) {   
    // TODO: 
    //
    // - check query cache for key: new Object[] { player, rtype, atype }}
    //   - if match return to caller
    // - if player does not have a match execute the individual query
    //   - update shared cache
    //   - update query cache
    //   - sync with transaction (remove matches that no longer match)
    //     - no match if: R.player != player & R.type != rtype % R.assoc.type != atype
    //   - return to caller
    
    // ON COMMIT:
    //
    // - invalidate query entries where
    
    try {
      
      // flush transaction if not shared
      if (!qlshared) {
        ptxn.flush();
      }
      
      // get identities
      IdentityIF playerid = i(player);
      IdentityIF rtypeid = i(rtype);
      IdentityIF atypeid = i(atype);
      IdentityIF tmid = i(tm);
      
      // check query cache
      ParameterArray params = new ParameterArray(new Object[] { playerid, rtypeid, atypeid });
      Collection<IdentityIF> result = rolesByType.get(params);
      if (result != null) {
        return syncWithTransaction(result, params, playerid, rtypeid, atypeid, tmid);
      }
      //! System.out.println("CM: " + params);

      // Get ticket
      TicketIF ticket = registrar.getTicket();

      // run individual query
      Connection conn = access.getConnection();
      PreparedStatement stm = conn.prepareStatement(sql_individual);
      stm.setFetchSize(500);
      
      int offset = 1;
      // bind: r.topicmap_id
      offset = bind(TopicMapIF_idfield, tmid, stm, offset);
      // bind: r.type_id
      offset = bind(TopicIF_idfield, rtypeid, stm, offset);
      // bind: a.topicmap_id
      offset = bind(TopicMapIF_idfield, tmid, stm, offset);
      // bind: a.type_id
      offset = bind(TopicIF_idfield, atypeid, stm, offset);
      // bind: r.player_id*
      offset = bind(TopicIF_idfield, playerid, stm, offset);
      
      Collection<IdentityIF> roles = new HashSet<IdentityIF>();
      
      try {
        
        // execute statement
        if (log.isDebugEnabled()) {
          log.debug("Executing: " + sql_individual);
        }
        ResultSet rs = stm.executeQuery();
        
        // zero or more rows expected
        while (rs.next()) {
          offset = 1;
          // load association role identity
          IdentityIF rid = (IdentityIF)AssociationRoleIF_idfield.load(registrar, ticket, rs, offset, false);
          offset += AssociationRoleIF_idfield.getColumnCount();
          // load association identity
          IdentityIF aid = (IdentityIF)AssociationIF_idfield.load(registrar, ticket, rs, offset, false);
          offset += AssociationIF_idfield.getColumnCount();
          
          // update roles list
          roles.add(rid);
          
          // update storage cache
          registrar.registerField(ticket, rid, Prefetcher.AssociationRoleIF_topicmap, tmid);
          registrar.registerField(ticket, rid, Prefetcher.AssociationRoleIF_type, rtypeid);
          registrar.registerField(ticket, rid, Prefetcher.AssociationRoleIF_player, playerid);
          registrar.registerField(ticket, rid, Prefetcher.AssociationRoleIF_association, aid);
          
          registrar.registerField(ticket, aid, Prefetcher.AssociationIF_topicmap, tmid);
          registrar.registerField(ticket, aid, Prefetcher.AssociationIF_type, atypeid);
        }
        // close result set
        rs.close();
        
      } finally {
        if (stm != null) {
          stm.close();
        }
      }
      
      // update query cache
      rolesByType.put(params, roles);
      
      // sync changes with transaction      
      return syncWithTransaction(roles, params, playerid, rtypeid, atypeid, tmid);
      
    } catch (SQLException e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
  protected Collection<AssociationRoleIF> syncWithTransaction(Collection<IdentityIF> roles, ParameterArray params,
      IdentityIF playerid, IdentityIF rtypeid, 
      IdentityIF atypeid, IdentityIF tmid) {
    // filter out roles where appropriate
    Collection<AssociationRoleIF> result = new HashSet<AssociationRoleIF>(roles.size());
    
    // look up roles objects in transaction
    Iterator<IdentityIF> iter = roles.iterator();
    while (iter.hasNext()) {
      IdentityIF rid = iter.next();
      AssociationRoleIF r;
      try {
        r = (AssociationRoleIF)ptxn.getObject(rid);
      } catch (Throwable t) {
        r = null; // identity not found
      }
      // passed through filter so add to result
      if (r != null) {
        result.add(r);
      }
    }
    // add roles that have been introduced in this transaction
    Collection<AssociationRoleIF> ra = radd.get(params);
    if (ra != null) {
      Iterator<AssociationRoleIF> i = ra.iterator();
      while (i.hasNext()) {
        result.add(i.next());
      }
    }
    // remove roles that are no longer applicable
    Collection<AssociationRoleIF> rr = rrem.get(params);
    if (rr != null) {
      Iterator<AssociationRoleIF> i = rr.iterator();
      while (i.hasNext()) {
        result.remove(i.next());
      }
    }
    
    //! System.out.println("B: " + roles + " A: " + result);
    //! System.out.println("BA: " + radd.get(params));
    //! System.out.println("BR: " + rrem.get(params));
    return result;
  }
  
  protected int bind(FieldInfoIF finfo, Object value, PreparedStatement stm, int offset) throws SQLException {
    finfo.bind(value, stm, offset);
    return offset + finfo.getColumnCount();
  }
  
  
  // -----------------------------------------------------------------------------
  // Event handlers
  // -----------------------------------------------------------------------------
  
  protected IdentityIF i(Object o) {
    return (o == null ? null : ((PersistentIF)o)._p_getIdentity());
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
    public void processEvent(Object object, String event, Object new_value, Object old_value) {
      AssociationRoleIF added = (AssociationRoleIF)new_value;
      
      // ignore event if player is null
      TopicIF player = added.getPlayer(); 
      if (player == null) {
        return;
      }
      
      // get association type
      AssociationIF assoc = added.getAssociation();
      TopicIF atype = (assoc == null ? null : assoc.getType());
      
      // register entry
      addEntry(new ParameterArray(new Object[] { i(player), i(added.getType()), i(atype) }), added);
    }
  }
  
  /**
   * EventHandler: AssociationRoleIF.removed
   */
  class AssociationRoleRemovedHandler implements EventListenerIF {
    @Override
    public void processEvent(Object object, String event, Object new_value, Object old_value) {
      AssociationRoleIF removed = (AssociationRoleIF)old_value;
      
      // ignore event if player is null
      TopicIF player = removed.getPlayer(); 
      if (player == null) {
        return;
      }
      
      // get association type
      AssociationIF assoc = removed.getAssociation();
      TopicIF atype = (assoc == null ? null : assoc.getType());
      
      // unregister entry
      removeEntry(new ParameterArray(new Object[] { i(player), i(removed.getType()), i(atype) }), removed);
    }
  }
  
  /**
   * EventHandler: AssociationRoleIF.setType
   */
  class EH01 implements EventListenerIF {
    @Override
    public void processEvent(Object object, String event, Object new_value, Object old_value) {
      AssociationRoleIF arole = (AssociationRoleIF)object;
      // ignore event if player is null
      TopicIF player = arole.getPlayer(); 
      if (player == null) {
        return;
      }
      
      // get association type
      AssociationIF assoc = arole.getAssociation();
      TopicIF atype = (assoc == null ? null : assoc.getType());
      
      // unregister old entry
      removeEntry(new ParameterArray(new Object[] { i(player), i(old_value), i(atype) }), arole);
      // register new entry
      addEntry(new ParameterArray(new Object[] { i(player), i(new_value), i(atype) }), arole);
    }
  }
  
  /**
   * EventHandler: AssociationRoleIF.setPlayer
   */
  class EH02 implements EventListenerIF {
    @Override
    public void processEvent(Object object, String event, Object new_value, Object old_value) {
      AssociationRoleIF arole = (AssociationRoleIF)object;
      
      // get association type
      AssociationIF assoc = arole.getAssociation();
      TopicIF atype = (assoc == null ? null : assoc.getType());
      
      // unregister old entry
      if (old_value != null) {
        removeEntry(new ParameterArray(new Object[] { i(old_value), i(arole.getType()), i(atype) }), arole);
      }
      
      // register new entry
      if (new_value != null) {
        addEntry(new ParameterArray(new Object[] { i(new_value), i(arole.getType()), i(atype) }), arole);
      }
    }
  }
  
  /**
   * EventHandler: AssociationIF.setType
   */
  class EH03 implements EventListenerIF {
    @Override
    public void processEvent(Object object, String event, Object new_value, Object old_value) {
      AssociationIF assoc = (Association)object;
      // loop over roles
      Iterator<AssociationRoleIF> iter = assoc.getRoles().iterator();
      while (iter.hasNext()) {
        AssociationRoleIF arole = iter.next();
        
        // ignore event if player is null
        TopicIF player = arole.getPlayer(); 
        if (player == null) {
          continue;
        }
        
        // unregister old entry
        removeEntry(new ParameterArray(new Object[] { i(player), i(arole.getType()), i(old_value) }), arole);
        
        // register new entry
        addEntry(new ParameterArray(new Object[] { i(player), i(arole.getType()), i(new_value) }), arole);
      }
    }
  }

}
