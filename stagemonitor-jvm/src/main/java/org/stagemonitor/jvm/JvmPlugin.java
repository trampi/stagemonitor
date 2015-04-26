package org.stagemonitor.jvm;


import java.util.Arrays;
import java.util.List;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stagemonitor.core.CorePlugin;
import org.stagemonitor.core.StagemonitorPlugin;
import org.stagemonitor.core.configuration.Configuration;
import org.stagemonitor.core.elasticsearch.ElasticsearchClient;

public class JvmPlugin extends StagemonitorPlugin {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void initializePlugin(MetricRegistry registry, Configuration configuration) {
		registry.register("jvm.gc", new GarbageCollectorMetricSet());
		registry.register("jvm.memory", new MemoryUsageGaugeSet());

		final CpuUtilisationWatch cpuWatch;
		try {
			cpuWatch = new CpuUtilisationWatch();
			cpuWatch.start();
			registry.register("jvm.cpu.process.usage", new Gauge<Float>() {
				@Override
				public Float getValue() {
					try {
						return cpuWatch.getCpuUsagePercent();
					} finally {
						cpuWatch.start();
					}
				}
			});
		} catch (Exception e) {
			logger.warn("Could not register cpu usage. ({})", e.getMessage());
		}
		registry.register("online", new Gauge<Integer>() {
			@Override
			public Integer getValue() {
				return 1;
			}
		});

		ElasticsearchClient elasticsearchClient = configuration.getConfig(CorePlugin.class).getElasticsearchClient();
		elasticsearchClient.sendGrafanaDashboardAsync("JVM Memory.json");
		elasticsearchClient.sendGrafanaDashboardAsync("JVM Overview.json");
	}

	@Override
	public List<String> getPathsOfWidgetMetricTabPlugins() {
		return Arrays.asList("/stagemonitor/static/tabs/metrics/jvm-metrics");
	}
}
