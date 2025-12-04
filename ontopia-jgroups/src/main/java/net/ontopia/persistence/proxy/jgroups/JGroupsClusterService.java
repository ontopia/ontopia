/*-
 * #!
 * Ontopia JGroups
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

package net.ontopia.persistence.proxy.jgroups;

import net.ontopia.persistence.proxy.CachesIF;
import net.ontopia.persistence.proxy.ClusterIF;
import net.ontopia.persistence.proxy.ClusterServiceIF;
import net.ontopia.persistence.proxy.RDBMSStorage;
import org.jgroups.JChannel;

public class JGroupsClusterService implements ClusterServiceIF {

	public static final String JGROUPS = "jgroups";

	private static JChannel channel = null;

	@Override
	public String type() {
		return JGROUPS;
	}

	@Override
	public ClusterIF getCluster(String clusterName, RDBMSStorage storage) {
		return channel == null
			? new JGroupsCluster(clusterName, storage)
			: new JGroupsCluster(clusterName, channel);
	}

	@Override
	public CachesIF getCaches(ClusterIF cluster) {
		return new JGroupsCaches(cluster);
	}

	public static void setExternalChannel(JChannel channel) {
		JGroupsClusterService.channel = channel;
	}
}
