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
import io.prometheus.metrics.core.metrics.GaugeWithCallback;
import io.prometheus.metrics.core.metrics.Info;
import io.prometheus.metrics.model.registry.PrometheusRegistry;
import io.prometheus.metrics.model.snapshots.Labels;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import net.ontopia.Ontopia;
import net.ontopia.topicmaps.entry.TopicMapRepositoryIF;
import net.ontopia.topicmaps.entry.TopicMaps;

public class OntopiaMetrics {
	public static final String PREFIX = "ontopia_";
	public static final String LABEL_SOURCE = "source";
	public static final String LABEL_TYPE = "type";

	private static final Set<PrometheusRegistry> REGISTERED = ConcurrentHashMap.newKeySet();

	private OntopiaMetrics() {
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

		public void register(PrometheusRegistry registry, String ontopiaRepository) {
			if (REGISTERED.add(registry)) {
				TopicMapRepositoryIF repository = (ontopiaRepository == null)
					? TopicMaps.getRepository()
					: TopicMaps.getRepository(ontopiaRepository);

				Info.builder(config)
					.name("ontopia_info")
					.help("Information about the Ontopia engine")
					.constLabels(Labels.of(
						"name", Ontopia.getName(),
						"version", Ontopia.getVersion(),
						"build", Ontopia.getBuild()
					)).register(registry);

				GaugeWithCallback.builder(config)
					.name(PREFIX + "topicmap_sources")
					.help("Number of topicmap sources")
					.labelNames(LABEL_TYPE)
					.callback(t ->
						repository.getSources().stream().collect(Collectors.groupingBy(Object::getClass, Collectors.counting()))
							.forEach((type, count) -> t.call(count, type.getName())))
					.register(registry);

				GaugeWithCallback.builder(config)
					.name(PREFIX + "topicmap_references")
					.help("Number of topicmap references")
					.labelNames(LABEL_SOURCE, LABEL_TYPE)
					.callback(callback -> repository.getSources().forEach(source ->
						source.getReferences().stream().collect(Collectors.groupingBy(Object::getClass, Collectors.counting())).forEach((type, references) ->
							callback.call(references, source.getId(), type.getName())
					)))
					.register(registry);

				OntopiaRDBMSMetrics.builder(config).register(registry, repository);
			}
		}
	}
}
