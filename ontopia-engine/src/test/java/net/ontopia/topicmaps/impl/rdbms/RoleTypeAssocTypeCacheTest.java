/*-
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2026 The Ontopia Project
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

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import net.ontopia.persistence.proxy.CacheIF;
import net.ontopia.persistence.proxy.CachesIF;
import net.ontopia.persistence.proxy.EvictableIF;
import net.ontopia.persistence.proxy.IdentityIF;
import net.ontopia.persistence.proxy.LongIdentity;
import net.ontopia.persistence.proxy.PersistentIF;
import net.ontopia.persistence.proxy.RDBMSStorage;
import net.ontopia.persistence.proxy.TransactionalLRULookupIndex;
import net.ontopia.topicmaps.core.AbstractTopicMapTest;
import net.ontopia.topicmaps.core.TestFactoryIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.entry.TopicMapSourceIF;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"unchecked", "rawtypes"})
public class RoleTypeAssocTypeCacheTest extends AbstractTopicMapTest {
  private static final Logger logger = LoggerFactory.getLogger(RoleTypeAssocTypeCacheTest.class);

  public static final String THREAD_NAME_PREFIX = "RoleTypeAssocTypeCacheTest-issue-661-";
  public static final String DELAYED_TEST_THREAD_NAME = THREAD_NAME_PREFIX + "slow";
  public static final String FAST_TEST_THREAD_NAME = THREAD_NAME_PREFIX + "fast";

  private TopicIF t;
  private TopicIF rt;
  private TopicIF at;

  private IdentityIF tmId;
  private LongIdentity tId;
  private LongIdentity rtId;
  private LongIdentity atId;

  private ParameterArray key;

  private CyclicBarrier gate = new CyclicBarrier(2);
  private DelayedTransactionalLRULookupIndex cache;

  @Override
  protected TestFactoryIF getFactory() throws Exception {
    return new RDBMSTestFactory();
  }

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();

    t = builder.makeTopic();
    rt = builder.makeTopic();
    at = builder.makeTopic();

    tmId = ((PersistentIF) topicmap)._p_getIdentity();
    tId = (LongIdentity) ((PersistentIF) t)._p_getIdentity();
    rtId = (LongIdentity) ((PersistentIF) rt)._p_getIdentity();
    atId = (LongIdentity) ((PersistentIF) at)._p_getIdentity();

    key = new ParameterArray(new Object[] {tId, rtId, atId});

    builder.makeAssociationRole(builder.makeAssociation(at), rt, t);
    builder.makeAssociationRole(builder.makeAssociation(at), rt, t);

    topicmap.getStore().commit();
    topicmap.getStore().close();

    cache = replaceCache();
  }

  @Test
  public void testCacheCorruptionIssue661() throws InterruptedException, IOException, BrokenBarrierException {
    Thread tst1 = new Thread(this::fastWriter, FAST_TEST_THREAD_NAME);
    Thread tst2 = new Thread(this::slowReader, DELAYED_TEST_THREAD_NAME);
    tst1.start();
    tst2.start();
    tst1.join();
    tst2.join();

    // post corruption check
    Collection<IdentityIF> cached = (Collection<IdentityIF>) cache.get(key);
    if (cached != null) { // evicted cache is acceptable
      Assert.assertEquals("Cache got corrupted,", 4, cached.size());
    }
  }

  // adds 2 roles
  private void fastWriter() {
      try (TopicMapStoreIF store = topicmapRef.createStore(false)) {
        TopicMapIF tm = store.getTopicMap();
        TopicMapBuilderIF builder = tm.getBuilder();
        TopicIF t = (TopicIF) tm.getObjectById(RoleTypeAssocTypeCacheTest.this.t.getObjectId());
        TopicIF rt = (TopicIF) tm.getObjectById(RoleTypeAssocTypeCacheTest.this.rt.getObjectId());
        TopicIF at = (TopicIF) tm.getObjectById(RoleTypeAssocTypeCacheTest.this.at.getObjectId());

        gate.await();

        // minor delay to let tst2 read the cache first
        Thread.sleep(100);

        // read the cache, skip the logic in rdbms.Topic
        logger.debug("[{}] getRolesByType", Thread.currentThread().getName());
        getRT2(tm).getRolesByType(t, rt, at);

        // add two roles
        builder.makeAssociationRole(builder.makeAssociation(at), rt, t);
        builder.makeAssociationRole(builder.makeAssociation(at), rt, t);

        // commit: clears the cache due to added roles
        logger.debug("[{}] commit", Thread.currentThread().getName());
        store.commit();
      } catch (Throwable e) {
        throw new RuntimeException(e);
      }
  }

  // only reads, but takes a long time to do so
  private void slowReader() {
      try (TopicMapStoreIF store = topicmapRef.createStore(false)) {
        TopicMapIF tm = store.getTopicMap();
        TopicIF t = (TopicIF) tm.getObjectById(RoleTypeAssocTypeCacheTest.this.t.getObjectId());
        TopicIF rt = (TopicIF) tm.getObjectById(RoleTypeAssocTypeCacheTest.this.rt.getObjectId());
        TopicIF at = (TopicIF) tm.getObjectById(RoleTypeAssocTypeCacheTest.this.at.getObjectId());

        gate.await();

        // read the cache, skip the logic in rdbms.Topic
        logger.debug("[{}] getRolesByType", Thread.currentThread().getName());
        getRT2(tm).getRolesByType(t, rt, at);

        // abort
        logger.debug("[{}] abort", Thread.currentThread().getName());
        store.abort();
      } catch (Throwable e) {
        throw new RuntimeException(e);
      }
  }

  private DelayedTransactionalLRULookupIndex replaceCache() {
    try {
      TopicMapSourceIF source = ((RDBMSTestFactory) factory).getSource();
      RDBMSStorage storage = (RDBMSStorage) FieldUtils.readField(source, "storage", true);
      TransactionalLRULookupIndex original = (TransactionalLRULookupIndex) storage.getHelperObject(CachesIF.QUERY_CACHE_RT2, tmId);
      DelayedTransactionalLRULookupIndex replacement = new DelayedTransactionalLRULookupIndex((CacheIF) FieldUtils.readField(original, "cache", true), (int) original.getMaxLRUSize());
      ((Map<IdentityIF, Map<String, EvictableIF>>) FieldUtils.readField(storage, "qcmap", true)).get(tmId).put("TopicIF.getRolesByType2", replacement);
      return replacement;
    } catch (ReflectiveOperationException e) {
      throw new RuntimeException(e);
    }
  }

  private RoleTypeAssocTypeCache getRT2(TopicMapIF tm) {
    try {
      return (RoleTypeAssocTypeCache) FieldUtils.readField(((TopicMap) tm).getTransaction(), "rtatcache", true);
    } catch (ReflectiveOperationException e) {
      throw new RuntimeException(e);
    }
  }

  private class DelayedTransactionalLRULookupIndex extends TransactionalLRULookupIndex {
    public DelayedTransactionalLRULookupIndex(CacheIF cache, int lrusize) {
      super(cache, lrusize);
    }

    @Override
    public Object put(Object key, Object value) {
      if (Thread.currentThread().getName().equals(DELAYED_TEST_THREAD_NAME)) {
        try { Thread.sleep(500); } catch (InterruptedException e) { }
      }
      return super.put(key, value);
    }
  }
}
