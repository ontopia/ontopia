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

package net.ontopia.persistence.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.persistence.query.jdo.JDOQuery;
import net.ontopia.persistence.query.sql.DetachedQueryIF;
import net.ontopia.persistence.query.sql.EqualsSQLOptimizer;
import net.ontopia.persistence.query.sql.GenericSQLGenerator;
import net.ontopia.persistence.query.sql.RDBMSMatrixQuery;
import net.ontopia.persistence.query.sql.RDBMSQuery;
import net.ontopia.persistence.query.sql.RedundantTablesSQLOptimizer;
import net.ontopia.persistence.query.sql.SQLBuilder;
import net.ontopia.persistence.query.sql.SQLGeneratorIF;
import net.ontopia.persistence.query.sql.SQLQuery;
import net.ontopia.persistence.query.sql.SQLStatementIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.impl.rdbms.ParameterArray;
import net.ontopia.topicmaps.impl.rdbms.RDBMSTopicMapReference;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.PropertyUtils;
import net.ontopia.utils.StreamUtils;
import org.apache.commons.lang3.StringUtils;
import org.jgroups.util.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: A storage definition implementation for relational
 * databases.
 */

public class RDBMSStorage implements StorageIF {
  
  // Define a logging category.
  private static final Logger log = LoggerFactory.getLogger(RDBMSStorage.class.getName());
  private static final ThreadFactory tFactory = new DefaultThreadFactory("nonTransactionalReadConnectionTimer-", true, true);

  public static final Set<String> known_properties;
  static {
    known_properties = new HashSet<String>();
    known_properties.add("net.ontopia.topicmaps.impl.rdbms.BatchUpdates");
    known_properties.add("net.ontopia.topicmaps.impl.rdbms.Cache.identitymap.lru");
    known_properties.add("net.ontopia.topicmaps.impl.rdbms.Cache.local.debug");
    known_properties.add("net.ontopia.topicmaps.impl.rdbms.Cache.rolesbytype.lru");
    known_properties.add("net.ontopia.topicmaps.impl.rdbms.Cache.rolesbytype2.lru");
    known_properties.add("net.ontopia.topicmaps.impl.rdbms.Cache.shared");
    known_properties.add("net.ontopia.topicmaps.impl.rdbms.Cache.shared.debug");
    known_properties.add("net.ontopia.topicmaps.impl.rdbms.Cache.shared.identitymap.lru");
    known_properties.add("net.ontopia.topicmaps.impl.rdbms.Cache.subjectidentity.srcloc.lru");
    known_properties.add("net.ontopia.topicmaps.impl.rdbms.Cache.subjectidentity.subind.lru");
    known_properties.add("net.ontopia.topicmaps.impl.rdbms.Cache.subjectidentity.subloc.lru");
    known_properties.add("net.ontopia.topicmaps.impl.rdbms.Cluster.id");
    known_properties.add("net.ontopia.topicmaps.impl.rdbms.Cluster.properties");
    known_properties.add("net.ontopia.topicmaps.impl.rdbms.ConnectionPool");
    known_properties.add("net.ontopia.topicmaps.impl.rdbms.ConnectionPool.JNDIDataSource");
    known_properties.add("net.ontopia.topicmaps.impl.rdbms.ConnectionPool.MaximumSize");
    known_properties.add("net.ontopia.topicmaps.impl.rdbms.ConnectionPool.MinimumSize");
    known_properties.add("net.ontopia.topicmaps.impl.rdbms.ConnectionPool.PoolStatements");
    known_properties.add("net.ontopia.topicmaps.impl.rdbms.ConnectionPool.SoftMaximum");
    known_properties.add("net.ontopia.topicmaps.impl.rdbms.ConnectionPool.UserTimeout");
    known_properties.add("net.ontopia.topicmaps.impl.rdbms.ConnectionPool.ValidationQuery");
    known_properties.add("net.ontopia.topicmaps.impl.rdbms.ConnectionString");
    known_properties.add("net.ontopia.topicmaps.impl.rdbms.Database");
    known_properties.add("net.ontopia.topicmaps.impl.rdbms.DriverClass");
    known_properties.add("net.ontopia.topicmaps.impl.rdbms.GlobalEntry");
    known_properties.add("net.ontopia.topicmaps.impl.rdbms.HighLowKeyGenerator.SelectSuffix");
    known_properties.add("net.ontopia.topicmaps.impl.rdbms.KeyBlockSize");
    known_properties.add("net.ontopia.topicmaps.impl.rdbms.MappingFile");
    known_properties.add("net.ontopia.topicmaps.impl.rdbms.Password");
    known_properties.add("net.ontopia.topicmaps.impl.rdbms.Platforms");
    known_properties.add("net.ontopia.topicmaps.impl.rdbms.QueriesFile");
    known_properties.add("net.ontopia.topicmaps.impl.rdbms.StorePool");
    known_properties.add("net.ontopia.topicmaps.impl.rdbms.StorePool.MaximumSize");
    known_properties.add("net.ontopia.topicmaps.impl.rdbms.StorePool.MinimumSize");
    known_properties.add("net.ontopia.topicmaps.impl.rdbms.StorePool.SoftMaximum");
    known_properties.add("net.ontopia.topicmaps.impl.rdbms.UserName");
    known_properties.add("net.ontopia.topicmaps.impl.rdbms.NonTransactionalRead");
    known_properties.add("net.ontopia.topicmaps.query.core.QueryProcessorIF");
    known_properties.add("net.ontopia.topicmaps.query.core.QueryProcessorIF.locale");
    known_properties.add("net.ontopia.topicmaps.query.impl.rdbms.ValuePredicate.function");
    known_properties.add("net.ontopia.topicmaps.query.impl.rdbms.ValuePredicate.function.type");
    known_properties.add("net.ontopia.topicmaps.query.impl.rdbms.ValueLikePredicate.function");
    known_properties.add("net.ontopia.topicmaps.query.impl.rdbms.ValueLikePredicate.function.type");
    known_properties.add("net.ontopia.infoset.fulltext.impl.rdbms.RDBMSSearcher.type");
  }
  
  private Map<String, String> properties;
  
  private RDBMSMapping mapping;
  private QueryDeclarations queries;
  
  private StorageCacheIF scache;
  private StorageAccessIF saccess;
  
  private ConnectionFactoryIF rw_connfactory;  
  private ConnectionFactoryIF ro_connfactory;  
  private KeyGeneratorIF keygen;
  
  private String database;
  private String[] platforms;
  
  private int access_counter;
  
  private SQLBuilder sqlbuilder;
  private SQLGeneratorIF sqlgen;

  private CachesIF caches;
  private ClusterIF cluster;

  private final Set<AbstractTransaction> transactions = Collections.newSetFromMap(new WeakHashMap<AbstractTransaction, Boolean>());

  private boolean nonTransactionalReadAllowed = true;
  private int nonTransactionalReadConnectionTimeout = 10;
  private final ScheduledExecutorService nonTransactionalReadConnectionTimer = Executors.newSingleThreadScheduledExecutor(tFactory);
  private final Map<Thread, NonTransactionalReadConnection> nonTransactionalReadConnections = new WeakHashMap<>();

  private final IdentityIF NULL_OBJECT_IDENTITY = new IdentityIF() {
    @Override
    public Class<?> getType() { throw new UnsupportedOperationException(); }
    @Override
    public int getWidth() { throw new UnsupportedOperationException(); }
    @Override
    public Object getKey(int index) { throw new UnsupportedOperationException(); }
    @Override
    public Object createInstance() throws Exception { throw new UnsupportedOperationException(); }
    @Override
    public Object clone() { throw new UnsupportedOperationException(); }
  };
  
  protected Map<IdentityIF, Map<String, EvictableIF>> qcmap = new HashMap<IdentityIF, Map<String, EvictableIF>>();

  /**
   * INTERNAL: Creates a storage definition which gets its settings
   * from system variables.
   */
  public RDBMSStorage() throws IOException {
    this(System.getProperty("net.ontopia.topicmaps.impl.rdbms.PropertyFile"));
  }
  
  /**
   * INTERNAL: Creates a storage definition that reads its settings
   * from the specified property file.
   */
  public RDBMSStorage(String propfile) throws IOException {    
    // TODO: attempt to load props from CLASSPATH (file: oks.rdbms.props)
    Objects.requireNonNull(propfile, "Property file cannot be null. Please set the 'net.ontopia.topicmaps.impl.rdbms.PropertyFile' property.");
    
    // Load properties from file
    InputStream istream = StreamUtils.getInputStream(propfile);
    if (istream == null) {
      throw new OntopiaRuntimeException("Property file '" + propfile + "' was not found.");
    }
    if (log.isDebugEnabled()) {
      log.info("Loading properties file from: "  + propfile);
    }
    init(PropertyUtils.toMap(PropertyUtils.loadProperties(istream)));
  }
  
  /**
   * INTERNAL: Creates a storage definition that reads its settings
   * from the specified properties.
   *
   * @since 1.2.4
   */
  public RDBMSStorage(Map<String, String> properties) throws IOException {
    // Pass on user current directory
    init(properties);
  }
  
  protected InputStream getInputStream(String property, String filename)
    throws IOException {
    
    String _filename = properties.get(property);
    if (_filename == null) {
      _filename = "classpath:net/ontopia/topicmaps/impl/rdbms/config/" + filename;
    } else {
      log.debug(filename + ": using file '" + _filename + "'");
    }
    return StreamUtils.getInputStream(_filename);
  }
  
  /**
   * INTERNAL: Method shared by constructors to properly initialize members.
   */
  protected void init(Map<String, String> properties) throws IOException {
    // Set storage properties
    this.properties = properties;
    
    // Get mapping.xml file 
    InputStream mstream = getInputStream("net.ontopia.topicmaps.impl.rdbms.MappingFile", 
        "mapping.xml");    
    if (mstream == null) {
      throw new OntopiaRuntimeException("Object-relational mapping file 'mapping.xml' cannot be found.");
    }
    
    // Get queries.xml file
    InputStream qstream = getInputStream("net.ontopia.topicmaps.impl.rdbms.QueriesFile", 
        "queries.xml");
    if (qstream == null) {
      throw new OntopiaRuntimeException("Built-in queries file 'queries.xml' cannot be found.");
    }
    
    // Read configuration files
    this.mapping = new RDBMSMapping(new ObjectRelationalMapping(mstream));
    this.queries = new QueryDeclarations(qstream);
    
    // Set up connection factory
    String cptype = properties.get("net.ontopia.topicmaps.impl.rdbms.ConnectionPool");
    
    if (cptype == null || "true".equals(cptype) || "yes".equals(cptype) || "dbcp".equals(cptype)) {
      log.debug("Using DBCP connection pool.");
      this.rw_connfactory = new DBCPConnectionFactory(properties, false); // default
      this.ro_connfactory = new DBCPConnectionFactory(properties, true); // default
      
    } else if ("jndi".equals(cptype)) {
      log.debug("Using JNDI connection pool.");
      this.rw_connfactory = new JNDIConnectionFactory(properties);
      this.ro_connfactory = new JNDIConnectionFactory(properties);
      
    }  else {
      log.debug("Using default connection factory (i.e. no pool).");
      this.rw_connfactory = new DefaultConnectionFactory(properties, false);
      this.ro_connfactory = new DefaultConnectionFactory(properties, true);
    }
    
    nonTransactionalReadAllowed = PropertyUtils.isTrue(properties.get("net.ontopia.topicmaps.impl.rdbms.NonTransactionalRead"), true);
    log.debug((nonTransactionalReadAllowed ? "Allowing" : "Not allowing") + " non-transactional reads");
    nonTransactionalReadConnectionTimeout = PropertyUtils.getInt(properties.get("net.ontopia.topicmaps.impl.rdbms.NonTransactionalReadTimeout"), 10); // 10s

    // Get database
    this.database = getProperty("net.ontopia.topicmaps.impl.rdbms.Database");
    if (this.database == null) {
      throw new OntopiaRuntimeException("The property 'net.ontopia.topicmaps.impl.rdbms.Database' is not set.");
    }
    
    // Get platforms
    String _platforms = getProperty("net.ontopia.topicmaps.impl.rdbms.Platforms");
    if (_platforms == null) {
      switch (database) {
        case "oracle":
        case "oracle8":
        case "oracle8i":
          _platforms = "oracle8,oracle,generic";
          break;
        case "oracle9":
        case "oracle9i":
          _platforms = "oracle9i,oracle,generic";
          break;
        case "oracle10":
        case "oracle10g":
          _platforms = "oracle10g,oracle,generic";
          break;
        case "postgresql":
          _platforms = "postgresql,generic";
          break;
        case "mysql":
          _platforms = "mysql,generic";
          break;
        case "db2":
          _platforms = "db2,generic";
          break;
        case "sapdb":
          _platforms = "sabdb,generic";
          break;
        case "firebird":
          _platforms = "firebird,generic";
          break;
        case "derby":
          _platforms = "derby,generic";
          break;
        case "sqlserver":
          _platforms = "sqlserver,generic";
          break;
        case "h2":
          _platforms = "h2,generic";
          break;
        case "generic":
          _platforms = "generic";
          break;
        default:
          throw new OntopiaRuntimeException("The datatype type is unknown and the property 'net.ontopia.topicmaps.impl.rdbms.Platforms' is not set.");
      }
    }    
    this.platforms = StringUtils.split(_platforms, ",");
    
    // Initialize key generator
    String kbprop = getProperty("net.ontopia.topicmaps.impl.rdbms.KeyBlockSize", "200");
    
    String global_entry = getProperty("net.ontopia.topicmaps.impl.rdbms.GlobalEntry", "<GLOBAL>");
    
    // Initialize key generator
    // TODO: Remove dependency on hardcoded key generator schema names.
    this.keygen = new HighLowKeyGenerator(this.rw_connfactory,
        "TM_ADMIN_SEQUENCE", "seq_name", "seq_count",
        global_entry, Integer.parseInt(kbprop), database, properties);
    
    // Create query builders
    this.sqlbuilder = new SQLBuilder(getMapping(), Boolean.parseBoolean(getProperty("net.ontopia.persistence.query.sql.SQLBuilder.debug")));
    this.sqlgen = GenericSQLGenerator.getSQLGenerator(getPlatforms(), properties);
    
    // Register jdbcspy driver
    try {
      Class.forName("net.ontopia.persistence.jdbcspy.SpyDriver");
    } catch (ClassNotFoundException e) {
      // ignore if not exists
    }
    
    // initialize cluster
    String clusterId = getProperty("net.ontopia.topicmaps.impl.rdbms.Cluster.id");
    if (clusterId != null) {
      if (clusterId.startsWith("jgroups:")) {
        String clusterProps = getProperty("net.ontopia.topicmaps.impl.rdbms.Cluster.properties");
        this.cluster = new JGroupsCluster(clusterId, clusterProps, this);
        this.caches = new JGroupsCaches(cluster);
      } else {
        throw new OntopiaRuntimeException("Not able to figure out cluster type from cluster id: " + clusterId);
      }
    }
    if (this.caches == null) {
      this.caches = new DefaultCaches();
    }
    
    
    // initialize shared cache
    if (PropertyUtils.isTrue(getProperty("net.ontopia.topicmaps.impl.rdbms.Cache.shared"), true)) {
      
      // default shared cache
      this.scache = new SharedCache(this, caches.<IdentityIF, CacheEntry>createDataCache());
      ((SharedCache)this.scache).setCluster(cluster);              
      
      // instrument shared cache
      int dinterval = PropertyUtils.getInt(getProperty("net.ontopia.topicmaps.impl.rdbms.Cache.shared.debug"), -1);
      if (dinterval > 0) {
        log.info("Instrumenting shared cache.");
        this.scache = new StatisticsCache("scache", scache, dinterval);
      }      
    } else if (this.cluster != null) {
      log.warn("");
      log.warn("");
      log.warn("  /vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv\\");
      log.warn("  >                                                                                  <");
      log.warn("  >         Joining a cluster without a shared storage cache will not work !         <");
      log.warn("  >                                                                                  <");
      log.warn("  >  Don't set property 'net.ontopia.topicmaps.impl.rdbms.Cache.shared' to 'false'   <");
      log.warn("  >   in combination with property 'net.ontopia.topicmaps.impl.rdbms.Cluster.id'.    <");
      log.warn("  >                                                                                  <");
      log.warn("  \\^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^/");
      log.warn("");
      log.warn("");
    }

    // join cluster
    if (this.cluster != null) {
      this.cluster.join();
    }
  }
  
  @Override
  public RDBMSMapping getMapping() {
    return mapping;
  }
  
  public QueryDeclarations getQueryDeclarations() {
    return queries;
  }
  
  public IdentityIF generateIdentity(Class<?> type) {
    return keygen.generateKey(type);
  }
  
  @Override
  public Map<String, String> getProperties() {
    return properties;
  }
  
  @Override
  public String getProperty(String property) {
    return properties.get(property);
  }
  
  public String getProperty(String property, String default_value) {
    String propval = properties.get(property);
    if (propval == null) {
      return default_value;
    } else {
      return propval;
    }
  }
  
  public StorageAccessIF createAccess(boolean readonly) {
    // TODO: Always return same read-only storage access?
    String id = "SA" + (++access_counter);
    return new RDBMSAccess(id, this, readonly);
  }

  @Override
  public TransactionIF createTransaction(boolean readonly) {
    AbstractTransaction transaction;
    if (readonly) {
      transaction = new ROTransaction(createAccess(readonly));
    } else {
      transaction= new RWTransaction(createAccess(readonly));
    }

    synchronized (transactions) {
      transactions.add(transaction);
    }

    return transaction;
  }
  
  @Override
  public boolean isSharedCache() {
    return (scache != null);
  }
  
  @Override
  public StorageCacheIF getStorageCache() {
    return scache;
  }
  
  /**
   * INTERNAL: Returns the database type.
   */
  public String getDatabase() {
    return database;
  }
  
  /**
   * INTERNAL: Returns the database platforms.
   */
  public String[] getPlatforms() {
    return platforms;
  }
  
  @Override
  public synchronized void close() {
    nonTransactionalReadConnectionTimer.shutdownNow();
    synchronized (nonTransactionalReadConnections) {
      nonTransactionalReadConnections.values().forEach(connection -> {
        try {
          if (!connection.isClosed()) {
              connection.close();
          }
        } catch (SQLException e) {
          throw new OntopiaRuntimeException(e);
        }
      });
      nonTransactionalReadConnections.clear();
    }

    if (cluster != null) {
      try {
        cluster.leave();
      } catch (Throwable t) {
        log.error("Could not deregister from cluster.", t);
      }
    }
    if (scache != null) {
      scache.close();
    }
    if (saccess != null) {
      saccess.close();
    }
    if (rw_connfactory != null) {
      rw_connfactory.close();
    }
    if (ro_connfactory != null) {
      ro_connfactory.close();
    }
  }
  
  // -----------------------------------------------------------------------------
  // Cluster
  // -----------------------------------------------------------------------------

  @Override
  public void notifyCluster() {
    if (cluster != null) {
      cluster.flush();
    }
  }
  
  // -----------------------------------------------------------------------------
  // Query cache
  // -----------------------------------------------------------------------------
  
  // NOTE: works only with queries with return type object. The return
  // values are not transactional object instances, but might instead
  // be the identity of such objects.
  
  // NOTE: query caches are indexed by name spaces. There is typically
  // one query cache instance per topicmap id + query name.
  
  @Override
  public synchronized EvictableIF getHelperObject(int identifier, IdentityIF namespace) {
    if (isSharedCache()) {
      // get query cache map for namespace 
      Map<String, EvictableIF> qcm = qcmap.get(namespace);
      if (qcm == null) {
        qcm = new HashMap<String, EvictableIF>();
        qcmap.put(namespace, qcm);
      }

      switch (identifier) {
      case CachesIF.QUERY_CACHE_SRCLOC: {
        final String name = "TopicMapIF.getObjectByItemIdentifier";
        EvictableIF qc = qcm.get(name);
        if (qc == null) {
          int lrusize_srcloc = PropertyUtils.getInt(getProperty("net.ontopia.topicmaps.impl.rdbms.Cache.subjectidentity.srcloc.lru"), 2000);
          CacheIF<LocatorIF, IdentityIF> cache = caches.<LocatorIF, IdentityIF>createCache(CachesIF.QUERY_CACHE_SRCLOC, namespace);
          qc = new QueryCache<LocatorIF, IdentityIF>(createDetachedQuery(name), cache, lrusize_srcloc, NULL_OBJECT_IDENTITY);
          qcm.put(name, qc);
        }
        return qc;
      }
      case CachesIF.QUERY_CACHE_SUBIND: {
        final String name = "TopicMapIF.getTopicBySubjectIdentifier";
        EvictableIF qc = qcm.get(name);
        if (qc == null) {
          int lrusize_subind = PropertyUtils.getInt(getProperty("net.ontopia.topicmaps.impl.rdbms.Cache.subjectidentity.subind.lru"), 1000);
          CacheIF<LocatorIF, IdentityIF> cache = caches.<LocatorIF, IdentityIF>createCache(CachesIF.QUERY_CACHE_SUBIND, namespace);
          qc = new QueryCache<LocatorIF, IdentityIF>(createDetachedQuery(name), cache, lrusize_subind, NULL_OBJECT_IDENTITY);
          qcm.put(name, qc);
        }
        return qc;
      }
      case CachesIF.QUERY_CACHE_SUBLOC: {
        final String name = "TopicMapIF.getTopicBySubject";
        EvictableIF qc = qcm.get(name);
        if (qc == null) {
          int lrusize_subloc = PropertyUtils.getInt(getProperty("net.ontopia.topicmaps.impl.rdbms.Cache.subjectidentity.subloc.lru"), 100);    
          CacheIF<LocatorIF, IdentityIF> cache = caches.<LocatorIF, IdentityIF>createCache(CachesIF.QUERY_CACHE_SUBLOC, namespace);
          qc = new QueryCache<LocatorIF, IdentityIF>(createDetachedQuery(name), cache, lrusize_subloc, NULL_OBJECT_IDENTITY);
          qcm.put(name, qc);
        }
        return qc;
      }
      case CachesIF.QUERY_CACHE_RT1: {
        final String name = "TopicIF.getRolesByType";
        EvictableIF qc = qcm.get(name);
        if (qc == null) {
          int lrusize = PropertyUtils.getInt(getProperty("net.ontopia.topicmaps.impl.rdbms.Cache.rolesbytype.lru"), 1000);
          CacheIF<ParameterArray, Collection<AssociationRoleIF>> cache = caches.<ParameterArray, Collection<AssociationRoleIF>>createCache(CachesIF.QUERY_CACHE_RT1, namespace);
          qc = new QueryCache<ParameterArray, Collection<AssociationRoleIF>>(createDetachedQuery(name), cache, lrusize, Collections.<AssociationRoleIF>emptySet());
          qcm.put(name, qc);
        }
        return qc;
      }
      case CachesIF.QUERY_CACHE_RT2: {
        final String name = "TopicIF.getRolesByType2";
        EvictableIF li = qcm.get(name);
        if (li == null) {
          int lrusize = PropertyUtils.getInt(getProperty("net.ontopia.topicmaps.impl.rdbms.Cache.rolesbytype2.lru"), 1000);
          CacheIF cache = caches.createCache(CachesIF.QUERY_CACHE_RT2, namespace);
          // ISSUE: why do we do this differently?
          li = new TransactionalLRULookupIndex(cache, lrusize);
          qcm.put(name, li);
        }
        return li;
      }
      default:
        throw new OntopiaRuntimeException("No helper object with identifier " + identifier + " found.");
      }
    } else {
      throw new OntopiaRuntimeException("Cannot create helper objects when shared cache is disabled.");      
    }
  }
  
  private DetachedQueryIF createDetachedQuery(String name) {
    StorageCacheIF scache = getStorageCache();
    
    // Generate query from query descriptor.
    QueryDescriptor qdesc = getQueryDescriptor(name);
    return qdesc.createSharedQuery(this, scache.getRegistrar(), platforms);
  }
  
  // -----------------------------------------------------------------------------
  // Queries
  // -----------------------------------------------------------------------------

  public String getQueryString(String name) {
    QueryDescriptor qdesc = getQueryDescriptor(name);
    return qdesc.getStatement(platforms);
  }
  
  protected QueryDescriptor getQueryDescriptor(String name) {
    // Lookup query descriptor
    QueryDescriptor qdesc = queries.getQueryDescriptor(name);
    if (qdesc == null) {
      throw new OntopiaRuntimeException("No query with the name " + name + " found.");
    }
    
    if (log.isDebugEnabled()) {
     log.debug("Generating query '" + name + "' from descriptor.");
    }
    
    return qdesc;
  }
  
  public QueryIF createQuery(String name, RDBMSAccess access, 
      ObjectAccessIF oaccess, AccessRegistrarIF registrar) {
    
    // Generate query from query descriptor.
    QueryDescriptor qdesc = getQueryDescriptor(name);
    return qdesc.createQuery(access, oaccess, registrar, platforms);
  }
  
  public QueryIF createQuery(JDOQuery jdoquery, RDBMSAccess access, 
      ObjectAccessIF oaccess, AccessRegistrarIF registrar,
      boolean lookup_identities) {
    //! System.out.println("JDO: " + jdoquery);
    SQLQuery sqlquery = sqlbuilder.makeQuery(jdoquery, oaccess);
    
    boolean debug = log.isDebugEnabled();
    if (debug) {
      log.debug("SQL1: " + sqlquery + " [width=" + sqlquery.getWidth() + "]");
    }
    //! System.out.println("SQL1: " + sqlquery + " [width=" + sqlquery.getWidth() + "]");
    
    sqlquery = new RedundantTablesSQLOptimizer().optimize(sqlquery);
    sqlquery = new EqualsSQLOptimizer().optimize(sqlquery);
    
    SQLStatementIF stm = sqlgen.createSQLStatement(sqlquery);
    if (debug) {
      log.debug("SQL2: " + stm + " [width=" + stm.getWidth() + "]");
    }
    //! System.out.println("SQL2: " + stm + " [width=" + stm.getWidth() + "]");
    
    stm.setObjectAccess(oaccess);
    stm.setAccessRegistrar(registrar);
    
    return new RDBMSQuery(access, new RDBMSMatrixQuery(stm, lookup_identities));
  }
  
  public SQLGeneratorIF getSQLGenerator() {
    return sqlgen;
  }
  
  public ConnectionFactoryIF getConnectionFactory(boolean readonly) {
    return (readonly ? ro_connfactory : rw_connfactory);
  }
  
  // -----------------------------------------------------------------------------
  // Cache reset
  // -----------------------------------------------------------------------------
  
  public synchronized void clearCache() {
    if (scache != null) {
      // clear shared cache
      ((SharedCache)scache).clear(true);
      // clear helper objects
      this.qcmap = new HashMap<IdentityIF, Map<String, EvictableIF>>();
    }
  }
  
  public void clearCache(IdentityIF namespace) {
    if (isSharedCache()) {
      EvictableIF ho;
      
      ho = getHelperObject(CachesIF.QUERY_CACHE_SRCLOC, namespace);
      ho.clear(true);
      
      ho = getHelperObject(CachesIF.QUERY_CACHE_SUBIND, namespace);
      ho.clear(true);
      
      ho = getHelperObject(CachesIF.QUERY_CACHE_SUBLOC, namespace);
      ho.clear(true);
      
      ho = getHelperObject(CachesIF.QUERY_CACHE_RT1, namespace);
      ho.clear(true);
      
      ho = getHelperObject(CachesIF.QUERY_CACHE_RT2, namespace);
      ho.clear(true);
    }
  }
  
  public void writeReport(java.io.Writer out, TopicMapReferenceIF reference, IdentityIF namespace, 
      boolean dumpCaches) throws java.io.IOException {

    out.write("<h1>OKS statistics</h1>\n");

    //! Runtime runtime = Runtime.getRuntime();
    //! out.write("<p>");
    //! out.write("Processors: ");
    //! out.write(Integer.toString(runtime.availableProcessors()));
    //! out.write(", Max memory: ");
    //! out.write(Long.toString(runtime.maxMemory()));
    //! out.write(", Used memory: ");
    //! out.write(Long.toString(runtime.totalMemory() - runtime.freeMemory()));
    //! out.write(", Total memory: ");
    //! out.write(Long.toString(runtime.totalMemory()));
    //! out.write(", Free memory: ");
    //! out.write(Long.toString(runtime.freeMemory()));
    //! out.write("</p>");
    
    // output shared cache statistics
    if (scache != null) {
      out.write("<h3>Shared cache</h3>\n");
      ((SharedCache)scache).writeReport(out, dumpCaches);
    }
    
    // output query cache statistics
    if (isSharedCache()) {
      QueryCache qc;
      
      out.write("<h3>TopicMapIF.getObjectByItemIdentifier</h3>\n");
      qc = (QueryCache)getHelperObject(CachesIF.QUERY_CACHE_SRCLOC, namespace);
      qc.writeReport(out, dumpCaches);
      
      out.write("<h3>TopicMapIF.getTopicBySubjectIdentifier</h3>\n");
      qc = (QueryCache)getHelperObject(CachesIF.QUERY_CACHE_SUBIND, namespace);
      qc.writeReport(out, dumpCaches);
      
      out.write("<h3>TopicMapIF.getTopicBySubject</h3>\n");
      qc = (QueryCache)getHelperObject(CachesIF.QUERY_CACHE_SUBLOC, namespace);
      qc.writeReport(out, dumpCaches);
      
      out.write("<h3>TopicIF.getRolesByType</h3>\n");
      qc = (QueryCache)getHelperObject(CachesIF.QUERY_CACHE_RT1, namespace);
      qc.writeReport(out, dumpCaches);
      
      out.write("<h3>TopicIF.getRolesByType2</h3>\n");
      TransactionalLRULookupIndex tl = (TransactionalLRULookupIndex)getHelperObject(CachesIF.QUERY_CACHE_RT2, namespace);
      tl.writeReport(out, dumpCaches);
    }
    
    // output reference statistics
    if (reference != null && reference instanceof RDBMSTopicMapReference) {
      out.write("<h3>Transactions</h3>\n");
      ((RDBMSTopicMapReference)reference).writeReport(out, dumpCaches);
    }

    // output dbcp statistics
    if (rw_connfactory instanceof DBCPConnectionFactory) {
      out.write("<h3>DBCP rw connection pool</h3>\n");
      ((DBCPConnectionFactory)rw_connfactory).writeReport(out);
    }    
    if (ro_connfactory instanceof DBCPConnectionFactory) {
      out.write("<h3>DBCP ro connection pool</h3>\n");
      ((DBCPConnectionFactory)ro_connfactory).writeReport(out);
    }    

    // output storage properties
    out.write("<h3>Database properties</h3>\n");
    out.write("<p><i>The properties given in bold face are those actually recognized by the OKS.</i></p>\n");
    List<String> props = new ArrayList<String>(properties.keySet());
    Collections.sort(props);
    for (String prop : props) {
      if ("net.ontopia.topicmaps.impl.rdbms.Password".equals(prop)) {
        out.write("<b>" + prop + "</b>=(<i>hidden for security reasons</i>)<br>\n");
      } else {
        if (known_properties.contains(prop)) { 
          out.write("<b>" + prop + "</b>=" + properties.get(prop) + "<br>\n");
        } else {
          out.write(prop + "=" + properties.get(prop) + "<br>\n");
        }
      }
    }
  }

  /**
   * INTERNAL: Propagates committed merges to other active transactions.
   * @param source Identity of the merge source object
   * @param target Identity of the merge target object
   * @param cause The transaction that committed the merge
   * @since 5.4.0
   */
  public synchronized void objectMerged(IdentityIF source, IdentityIF target, AbstractTransaction cause) {
    // block other transactons until we have processed the merge
      for (AbstractTransaction transaction : transactions) {
        if (!transaction.equals(cause)) {
          transaction.objectMerged(source, target);
        }
      }
  }

  public int getActiveTransactionCount() {
    return transactions.size();
  }

  protected void transactionClosed(AbstractTransaction transaction) {
    transactions.remove(transaction);
  }

  /**
   * INTERNAL: exposes the set of active transactions.
   * @since 5.4.0
   */
  public Set<AbstractTransaction> getTransactions() {
    return transactions;
  }

  protected Connection getNonTransactionalReadConnection() {
    if (!nonTransactionalReadAllowed) {
      throw new TransactionNotActiveException();
    }

    final Thread thread = Thread.currentThread();
    NonTransactionalReadConnection connection;
    try {
      synchronized (nonTransactionalReadConnections) {
        connection = nonTransactionalReadConnections.get(thread);
        if (connection != null) {
          if (connection.isClosed()) {
            nonTransactionalReadConnections.remove(thread);
          } else {
            connection.touch();
            return connection.connection;
          }
        }
        connection = new NonTransactionalReadConnection(getConnectionFactory(true).requestConnection());

        nonTransactionalReadConnections.put(thread, connection);

        new NonTransactionalReadConnectionCleanup(connection, thread);
      }
    } catch (SQLException e) {
      throw new OntopiaRuntimeException(e);
    }
    log.debug("Requested NonTransactionalRead connection " + connection.id + " for " + thread);
    return connection.connection;
  }

  protected void touch(Connection connection) {
    if (connection instanceof NonTransactionalReadConnection) {
      ((NonTransactionalReadConnection) connection).touch();
    }
  }

  private class NonTransactionalReadConnectionCleanup implements Callable<NonTransactionalReadConnection> {

    private final NonTransactionalReadConnection connection;
    private final Thread thread;

    public NonTransactionalReadConnectionCleanup(NonTransactionalReadConnection connection, Thread thread) {
      this.connection = connection;
      this.thread = thread;
      nonTransactionalReadConnectionTimer.schedule(this, nonTransactionalReadConnectionTimeout, TimeUnit.SECONDS);
    }

    @Override
    public NonTransactionalReadConnection call() throws Exception {
      if (!connection.isClosed() && thread.isAlive() && connection.getLastUsed() > 0) {
        long timeout = connection.getLastUsed() + TimeUnit.SECONDS.toMillis(nonTransactionalReadConnectionTimeout);
        if (timeout > System.currentTimeMillis()) {
          log.debug("NonTransactionalRead connection {} was used, resetting timeout for {}", connection.id, thread);
          connection.resetLastUsed();
          nonTransactionalReadConnectionTimer.schedule(this, nonTransactionalReadConnectionTimeout, TimeUnit.SECONDS);
          return connection;
        }
      }
      clean();
      return connection;
    }

    private void clean() {
      synchronized (nonTransactionalReadConnections) {
        log.debug("Closing NonTransactionalRead connection {} for {}", connection.id, thread);
        try {
            connection.close();
        } catch (SQLException e) {
          throw new OntopiaRuntimeException(e);
        } finally {
          nonTransactionalReadConnections.remove(thread);
        }
      }
    }
  }

  private class NonTransactionalReadConnection {
    private final Connection connection;
    private final String id;
    private long lastUsed = 0;

    public NonTransactionalReadConnection(Connection connection) {
      this.connection = connection;
      this.id = Integer.toHexString(connection.hashCode());
    }

    private void touch() {
      lastUsed = System.currentTimeMillis();
    }
    
    private long getLastUsed() {
      return lastUsed;
    }

    private void resetLastUsed() {
      lastUsed = 0;
    }

    private void close() throws SQLException {
      connection.rollback();
      connection.close();
    }
    
    private boolean isClosed() throws SQLException {
      return connection.isClosed();
    }
  }
}
