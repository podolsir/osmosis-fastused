package de.vwistuttgart.openstreetmap.osmosis.fastusedfilter;

import java.util.HashMap;
import java.util.Map;

import org.openstreetmap.osmosis.core.pipeline.common.TaskManagerFactory;
import org.openstreetmap.osmosis.core.plugin.PluginLoader;

import de.vwistuttgart.openstreetmap.osmosis.fastusedfilter.v0_6.FastUsedNodeFilterFactory;

public class FastUsedPluginLoader implements PluginLoader {

	@Override
	public Map<String, TaskManagerFactory> loadTaskFactories() {
		Map<String, TaskManagerFactory> factories = new HashMap<String, TaskManagerFactory>();

		factories.put("--fast-used-node", new FastUsedNodeFilterFactory());
		factories.put("--fun", new FastUsedNodeFilterFactory());

		factories.put("--fast-used-node-0.6", new FastUsedNodeFilterFactory());
		factories.put("--fun-0.6", new FastUsedNodeFilterFactory());

		return factories;
	}

}
