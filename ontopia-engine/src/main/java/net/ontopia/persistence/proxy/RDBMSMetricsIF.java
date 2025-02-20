/*-
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2025 The Ontopia Project
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

import java.util.Map;

public interface RDBMSMetricsIF {

  // connection pool

  long getROConnectionPoolActive();
  long getROConnectionPoolIdle();
  long getROConnectionPoolMaxActive();
  long getROConnectionPoolMaxIdle();
  long getROConnectionPoolMinIdle();

  long getRWConnectionPoolActive();
  long getRWConnectionPoolIdle();
  long getRWConnectionPoolMaxActive();
  long getRWConnectionPoolMaxIdle();
  long getRWConnectionPoolMinIdle();

  // connection factory

  long getROConnectionsClosed();
  long getROConnectionsOpened();
  long getROConnectionsValidated();
  long getROConnectionsBorrowed();
  long getROConnectionsReturned();

  long getRWConnectionsClosed();
  long getRWConnectionsOpened();
  long getRWConnectionsValidated();
  long getRWConnectionsBorrowed();
  long getRWConnectionsReturned();

  // shared cache

  long getSharedCacheSize();

  // query caches

  Map<Long, CacheMetricsIF> getTopicMapIFgetObjectByItemIdentifierQueryCacheMetrics();
  Map<Long, CacheMetricsIF> getTopicMapIFgetTopicBySubjectIdentifierQueryCacheMetrics();
  Map<Long, CacheMetricsIF> getTopicMapIFgetTopicBySubjectQueryCacheMetrics();
  Map<Long, CacheMetricsIF> getTopicIFgetRolesByTypeQueryCacheMetrics();
  Map<Long, CacheMetricsIF> getTopicIFgetRolesByType2QueryCacheMetrics();

  // clustering

  String getClusterName();
  String getClusterState();
  long getClusterReceivedBytes();
  long getClusterReceivedMessages();
  long getClusterSentBytes();
  long getClusterSentMessages();

  // access

  long getAccessCount();

}
