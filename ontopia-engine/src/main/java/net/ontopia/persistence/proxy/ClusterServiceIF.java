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

import java.io.IOException;
import java.util.Set;
import net.ontopia.utils.ServiceUtils;
import org.apache.commons.lang3.Strings;

/**
 * Service interface that provides clustering functionalitiy for ontopia RDBMS persistence.
 */
public interface ClusterServiceIF {

  /**
   * A prefix to be used in cluster name configuration to indicate this service.
   * For example: "jgroups"
   * @return
   */
  String type();

  /**
   * Get a ClusterIF implementation specific to this cluster service.
   * @param clusterName The name of the cluster specified in configuration
   * @param storage The storage that will be using the cluster
   * @return A ClusterIF implementation
   */
  ClusterIF getCluster(String clusterName, RDBMSStorage storage);

  /**
   * Get a CachesIF implementation specific to this service, or null if a default implementation is
   * to be used.
   * @param cluster The cluster returned by {@link #getCluster(java.lang.String, net.ontopia.persistence.proxy.RDBMSStorage)}
   * @return A CachesIF implementation
   */
  CachesIF getCaches(ClusterIF cluster);

  /**
   * Loads all ClusterServiceIF implementations on the classpath
   * @return
   * @throws IOException
   */
  public static Set<ClusterServiceIF> getServices() throws IOException {
    return ServiceUtils.loadServices(ClusterServiceIF.class);
  }

  /**
   * Loads a ClusterServiceIF for the provided type.
   * @param type A type identifier to match to {@link #type()}
   * @return A ClusterServiceIF or null if non was found
   * @throws IOException
   */
  public static ClusterServiceIF getClusterService(String type) throws IOException {
    for (ClusterServiceIF service : getServices()) {
      if (Strings.CS.equals(type, service.type())) {
        return service;
      }
    }
    return null;
  }
}
