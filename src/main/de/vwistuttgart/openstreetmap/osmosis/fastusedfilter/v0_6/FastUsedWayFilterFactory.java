// This software is released into the Public Domain.  See copying.txt for details.
package de.vwistuttgart.openstreetmap.osmosis.fastusedfilter.v0_6;

import org.openstreetmap.osmosis.core.OsmosisRuntimeException;
import org.openstreetmap.osmosis.core.filter.common.IdTrackerType;
import org.openstreetmap.osmosis.core.pipeline.common.TaskConfiguration;
import org.openstreetmap.osmosis.core.pipeline.common.TaskManager;
import org.openstreetmap.osmosis.core.pipeline.common.TaskManagerFactory;
import org.openstreetmap.osmosis.core.pipeline.v0_6.MultiSinkRunnableSourceManager;


/**
 * Extends the basic task manager factory functionality with fast-used-way
 * filter task specific common methods.
 * 
 * @author Igor Podolskiy
 */
public class FastUsedWayFilterFactory extends TaskManagerFactory {

	private static final String ARG_ID_TRACKER_TYPE = "idTrackerType";
	private static final IdTrackerType DEFAULT_ID_TRACKER_TYPE = IdTrackerType.IdList;

	private static final String ARG_BUFFER_CAPACITY = "bufferCapacity";
	private static final int DEFAULT_BUFFER_CAPACITY = 20;


	/**
	 * Utility method that returns the IdTrackerType to use for a given
	 * taskConfig.
	 * 
	 * @param taskConfig
	 *            Contains all information required to instantiate and configure
	 *            the task.
	 * @return The entity identifier tracker type.
	 */
	protected IdTrackerType getIdTrackerType(TaskConfiguration taskConfig) {
		if (doesArgumentExist(taskConfig, ARG_ID_TRACKER_TYPE)) {
			String idTrackerType;

			idTrackerType = getStringArgument(taskConfig, ARG_ID_TRACKER_TYPE);

			try {
				return IdTrackerType.valueOf(idTrackerType);
			} catch (IllegalArgumentException e) {
				throw new OsmosisRuntimeException("Argument " + ARG_ID_TRACKER_TYPE + " for task " + taskConfig.getId()
						+ " must contain a valid id tracker type.", e);
			}

		} else {
			return DEFAULT_ID_TRACKER_TYPE;
		}
	}


	@Override
	protected TaskManager createTaskManagerImpl(TaskConfiguration taskConfig) {
		int bufferCapacity = getIntegerArgument(taskConfig, ARG_BUFFER_CAPACITY, DEFAULT_BUFFER_CAPACITY);
		IdTrackerType idTrackerType = getIdTrackerType(taskConfig);
		return new MultiSinkRunnableSourceManager(taskConfig.getId(), new FastUsedWayFilter(idTrackerType,
				bufferCapacity), taskConfig.getPipeArgs());
	}
}
