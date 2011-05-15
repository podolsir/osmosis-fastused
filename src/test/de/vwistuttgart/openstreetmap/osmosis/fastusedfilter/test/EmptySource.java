/**
 * 
 */
package de.vwistuttgart.openstreetmap.osmosis.fastusedfilter.test;

import org.openstreetmap.osmosis.core.task.v0_6.RunnableSource;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;

public class EmptySource implements RunnableSource {

	private Sink sink;


	@Override
	public void setSink(Sink sink) {
		this.sink = sink;
	}


	@Override
	public void run() {
		sink.complete();
		sink.release();
	}
}