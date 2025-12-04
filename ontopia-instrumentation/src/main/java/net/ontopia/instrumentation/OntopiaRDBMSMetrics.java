/*-
 * #!
 * Ontopia Instrumentation
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

package net.ontopia.instrumentation;

import io.prometheus.metrics.config.PrometheusProperties;
import io.prometheus.metrics.core.metrics.CounterWithCallback;
import io.prometheus.metrics.core.metrics.GaugeWithCallback;
import io.prometheus.metrics.core.metrics.Info;
import io.prometheus.metrics.model.registry.PrometheusRegistry;
import io.prometheus.metrics.model.snapshots.Unit;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Stream;
import net.ontopia.persistence.proxy.CacheMetricsIF;
import net.ontopia.persistence.proxy.RDBMSMetricsIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.entry.TopicMapRepositoryIF;
import net.ontopia.topicmaps.impl.rdbms.RDBMSTopicMapReference;
import net.ontopia.topicmaps.impl.rdbms.RDBMSTopicMapSource;

public class OntopiaRDBMSMetrics {
	public static final String PREFIX = OntopiaMetrics.PREFIX + "topicmap_rdbms_";
	public static final String LABEL_TOPICMAP = "topicmap";

	private static final Set<PrometheusRegistry> REGISTERED = ConcurrentHashMap.newKeySet();
	private static final Map<RDBMSTopicMapSource, RDBMSMetricsIF> RDBMS_METRICS = new WeakHashMap<>();

	private OntopiaRDBMSMetrics() {
		// no-op
	}

	public static Builder builder() {
		return new Builder(PrometheusProperties.get());
	}

	public static Builder builder(PrometheusProperties config) {
		return new Builder(config);
	}

	public static class Builder {

		private final PrometheusProperties config;

		private Builder(PrometheusProperties config) {
			this.config = config;
		}

		public void register() {
			register(PrometheusRegistry.defaultRegistry, null);
		}

		public void register(PrometheusRegistry registry, TopicMapRepositoryIF repository) {
			if (REGISTERED.add(registry)) {

				createGauge(repository, registry, "storage_active_connections", "The number of currently active connections as reported by RDBMSStorage", RDBMSMetricsIF::getActiveTransactionCount);
				createGauge(repository, registry, "shared_cache_size", "The size of the Shared Storage cache", RDBMSMetricsIF::getSharedCacheSize);

				// --- Connection pooling

				GaugeWithCallback.builder(config)
					.name(PREFIX + "connection_pool_size")
					.help("Metrics about the DBCP connection pools")
					.labelNames(OntopiaMetrics.LABEL_SOURCE, "state")
					.callback(t -> getRDBMSTopicMapSources(repository).forEach(s -> {
						Optional<RDBMSMetricsIF> metrics = getMetrics(s);
						metrics.map(RDBMSMetricsIF::getConnectionPoolActive).ifPresent(count -> t.call(count, s.getId(), "active"));
						metrics.map(RDBMSMetricsIF::getConnectionPoolIdle).ifPresent(count -> t.call(count, s.getId(), "idle"));
						metrics.map(RDBMSMetricsIF::getConnectionPoolMaxIdle).ifPresent(count -> t.call(count, s.getId(), "maxidle"));
						metrics.map(RDBMSMetricsIF::getConnectionPoolMaxTotal).ifPresent(count -> t.call(count, s.getId(), "maxtotal"));
						metrics.map(RDBMSMetricsIF::getConnectionPoolMinIdle).ifPresent(count -> t.call(count, s.getId(), "minidle"));
					})).register(registry);

				createCounter(repository, registry, "connections_borrowed", "Number of connections borrowed from the pool", RDBMSMetricsIF::getConnectionsBorrowed);
				createCounter(repository, registry, "connections_returned", "Number of connections returned to the pool", RDBMSMetricsIF::getConnectionsReturned);
				createCounter(repository, registry, "connections_opened", "Number of new connections opened by the pool", RDBMSMetricsIF::getConnectionsOpened);
				createCounter(repository, registry, "connections_closed", "Number of connections closed by the pool", RDBMSMetricsIF::getConnectionsClosed);
				createCounter(repository, registry, "connections_closed_evicted", "Number of connections closed by the pool due to eviction", RDBMSMetricsIF::getConnectionsClosedByEviction);
				createCounter(repository, registry, "connections_closed_validation", "Number of connections closed by the pool due to validation failure", RDBMSMetricsIF::getConnectionsClosedByValidation);

				// --- QueryCache

				createQueryCacheGauge(repository, registry, "TopicMapIF_getObjectByItemIdentifier_lru_size", "The size of the LRU of the TopicMapIF.getObjectByItemIdentifier cache", RDBMSMetricsIF::getTopicMapIFgetObjectByItemIdentifierQueryCacheMetrics, CacheMetricsIF::getLRUSize);
				createQueryCacheGauge(repository, registry, "TopicMapIF_getObjectByItemIdentifier_lru_max_size", "The maximum size of the LRU of the TopicMapIF.getObjectByItemIdentifier cache", RDBMSMetricsIF::getTopicMapIFgetObjectByItemIdentifierQueryCacheMetrics, CacheMetricsIF::getMaxLRUSize);
				createQueryCacheGauge(repository, registry, "TopicMapIF_getObjectByItemIdentifier_cache_size", "The size of the TopicMapIF.getObjectByItemIdentifier cache", RDBMSMetricsIF::getTopicMapIFgetObjectByItemIdentifierQueryCacheMetrics, CacheMetricsIF::getCacheSize);

				createQueryCacheGauge(repository, registry, "TopicMapIF_getTopicBySubjectIdentifier_lru_size", "The size of the LRU of the TopicMapIF.getTopicBySubjectIdentifier cache", RDBMSMetricsIF::getTopicMapIFgetTopicBySubjectIdentifierQueryCacheMetrics, CacheMetricsIF::getLRUSize);
				createQueryCacheGauge(repository, registry, "TopicMapIF_getTopicBySubjectIdentifier_lru_max_size", "The maximum size of the LRU of the TopicMapIF.getTopicBySubjectIdentifier cache", RDBMSMetricsIF::getTopicMapIFgetTopicBySubjectIdentifierQueryCacheMetrics, CacheMetricsIF::getMaxLRUSize);
				createQueryCacheGauge(repository, registry, "TopicMapIF_getTopicBySubjectIdentifier_cache_size", "The size of the TopicMapIF.getTopicBySubjectIdentifier cache", RDBMSMetricsIF::getTopicMapIFgetTopicBySubjectIdentifierQueryCacheMetrics, CacheMetricsIF::getCacheSize);

				createQueryCacheGauge(repository, registry, "TopicMapIF_getTopicBySubject_lru_size", "The size of the LRU of the TopicMapIF.getTopicBySubject cache", RDBMSMetricsIF::getTopicMapIFgetTopicBySubjectQueryCacheMetrics, CacheMetricsIF::getLRUSize);
				createQueryCacheGauge(repository, registry, "TopicMapIF_getTopicBySubject_lru_max_size", "The maximum size of the LRU of the TopicMapIF.getTopicBySubject cache", RDBMSMetricsIF::getTopicMapIFgetTopicBySubjectQueryCacheMetrics, CacheMetricsIF::getMaxLRUSize);
				createQueryCacheGauge(repository, registry, "TopicMapIF_getTopicBySubject_cache_size", "The size of the TopicMapIF.getTopicBySubject cache", RDBMSMetricsIF::getTopicMapIFgetTopicBySubjectQueryCacheMetrics, CacheMetricsIF::getCacheSize);

				createQueryCacheGauge(repository, registry, "TopicIF_getRolesByType_lru_size", "The size of the LRU of the TopicIF.getRolesByType cache", RDBMSMetricsIF::getTopicIFgetRolesByTypeQueryCacheMetrics, CacheMetricsIF::getLRUSize);
				createQueryCacheGauge(repository, registry, "TopicIF_getRolesByType_lru_max_size", "The maximum size of the LRU of the TopicIF.getRolesByType cache", RDBMSMetricsIF::getTopicIFgetRolesByTypeQueryCacheMetrics, CacheMetricsIF::getMaxLRUSize);
				createQueryCacheGauge(repository, registry, "TopicIF_getRolesByType_cache_size", "The size of the TopicIF.getRolesByType cache", RDBMSMetricsIF::getTopicIFgetRolesByTypeQueryCacheMetrics, CacheMetricsIF::getCacheSize);

				createQueryCacheGauge(repository, registry, "TopicIF_getRolesByType2_lru_size", "The size of the LRU of the TopicIF.getRolesByType2 cache", RDBMSMetricsIF::getTopicIFgetRolesByType2QueryCacheMetrics, CacheMetricsIF::getLRUSize);
				createQueryCacheGauge(repository, registry, "TopicIF_getRolesByType2_lru_max_size", "The maximum size of the LRU of the TopicIF.getRolesByType2 cache", RDBMSMetricsIF::getTopicIFgetRolesByType2QueryCacheMetrics, CacheMetricsIF::getMaxLRUSize);
				createQueryCacheGauge(repository, registry, "TopicIF_getRolesByType2_cache_size", "The size of the TopicIF.getRolesByType2 cache", RDBMSMetricsIF::getTopicIFgetRolesByType2QueryCacheMetrics, CacheMetricsIF::getCacheSize);

				// --- clustering

				Info clusterNames = Info.builder(config)
					.name(PREFIX + "cluster_info")
					.help("Information about the jgroups cluster per TopicMapSource")
					.labelNames(OntopiaMetrics.LABEL_SOURCE, "name", "state")
					.register(registry);

				Runnable updateClusterInfo = () -> 
					getRDBMSTopicMapSources(repository).forEach(source -> {
						Optional<RDBMSMetricsIF> metrics = getMetrics(source);
						if (metrics.isPresent()) {
							clusterNames.setLabelValues(source.getId(), metrics.get().getClusterName(), metrics.get().getClusterState());
						}
					});

				updateClusterInfo.run();

				createCounter(repository, registry, "cluster_send_bytes", "Number of bytes send to the cluster by jgroups", RDBMSMetricsIF::getClusterSentBytes, Unit.BYTES);
				createCounter(repository, registry, "cluster_send_total", "Number of messages send to the cluster by jgroups", RDBMSMetricsIF::getClusterSentMessages);
				createCounter(repository, registry, "cluster_received_bytes", "Number of bytes recieved to the cluster by jgroups", RDBMSMetricsIF::getClusterReceivedBytes, Unit.BYTES);
				createCounter(repository, registry, "cluster_received_total", "Number of messages recieved to the cluster by jgroups", RDBMSMetricsIF::getClusterReceivedMessages);

				GaugeWithCallback.builder(config)
					.name(PREFIX + "cluster_node_count")
					.help("The number of nodes in the cluster")
					.labelNames(OntopiaMetrics.LABEL_SOURCE)
					.callback(c -> {
						updateClusterInfo.run(); // update cluster state info

						getRDBMSTopicMapSources(repository).forEach(source ->
							getMetrics(source).map(RDBMSMetricsIF::getClusterNodeCount).ifPresent(count -> c.call(count, source.getId())));
					})
					.register(registry);

				// --- Transactions

				createGauge(repository, registry, "access_counter", "A counter for opened RDBMSAccess instances, rougly indicates number of transactions", RDBMSMetricsIF::getAccessCount);
			}
		}

		private Stream<RDBMSTopicMapSource> getRDBMSTopicMapSources(TopicMapRepositoryIF repository) {
			return repository.getSources().stream()
				.filter(s -> s instanceof RDBMSTopicMapSource)
				.map(RDBMSTopicMapSource.class::cast);
		}

		private Optional<RDBMSMetricsIF> getMetrics(RDBMSTopicMapSource source) {
			return Optional.ofNullable(RDBMS_METRICS.computeIfAbsent(source, RDBMSTopicMapSource::getMetrics));
		}

		private void createCounter(TopicMapRepositoryIF repository, PrometheusRegistry registry, String name, String help, Function<RDBMSMetricsIF, Long> mapping) {
			createCounter(repository, registry, name, help, mapping, null);
		}
		private void createCounter(TopicMapRepositoryIF repository, PrometheusRegistry registry, String name, String help, Function<RDBMSMetricsIF, Long> mapping, Unit unit) {
			CounterWithCallback.builder(config)
				.name(PREFIX + name)
				.help(help)
				.unit(unit)
				.labelNames(OntopiaMetrics.LABEL_SOURCE)
				.callback(c -> getRDBMSTopicMapSources(repository).forEach(source ->
					getMetrics(source).map(mapping).filter(this::isNotNegative).ifPresent(count -> c.call(count, source.getId()))))
				.register(registry);
		}

		private void createGauge(TopicMapRepositoryIF repository, PrometheusRegistry registry, String name, String help, Function<RDBMSMetricsIF, Long> mapping) {
			GaugeWithCallback.builder(config)
				.name(PREFIX + name)
				.help(help)
				.labelNames(OntopiaMetrics.LABEL_SOURCE)
				.callback(c -> getRDBMSTopicMapSources(repository).forEach(source ->
					getMetrics(source).map(mapping).ifPresent(count -> c.call(count, source.getId()))))
				.register(registry);
		}

		private void createQueryCacheGauge(TopicMapRepositoryIF repository, PrometheusRegistry registry, String name, String help, Function<RDBMSMetricsIF, Map<Long, CacheMetricsIF>> mapping1, Function<CacheMetricsIF, Long> mapping2) {
			GaugeWithCallback.builder(config)
				.name(PREFIX + name)
				.help(help)
				.labelNames(OntopiaMetrics.LABEL_SOURCE, LABEL_TOPICMAP)
				.callback(t -> getRDBMSTopicMapSources(repository).forEach(s ->
					getMetrics(s).map(mapping1)
						.ifPresent(counts -> counts.forEach((tmid, count) -> t.call(mapping2.apply(count), s.getId(), getTopicmapId(tmid, repository))))
				)).register(registry);
		}

		private String getTopicmapId(Long tm, TopicMapRepositoryIF repository) {
			return repository.getReferences().stream()
				.filter(r -> ((RDBMSTopicMapReference) r).getTopicMapId() == tm)
				.findAny()
				.map(TopicMapReferenceIF::getId)
				.orElse("unknown");
		}

		private boolean isNotNegative(Long t) {
			return t != null && t >= 0;
		}
	}
}
