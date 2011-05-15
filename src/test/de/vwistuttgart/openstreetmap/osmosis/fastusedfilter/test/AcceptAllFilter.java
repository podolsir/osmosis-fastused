package de.vwistuttgart.openstreetmap.osmosis.fastusedfilter.test;

import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;
import org.openstreetmap.osmosis.core.task.v0_6.SinkSource;

public class AcceptAllFilter implements SinkSource {

	private Sink sink;


	@Override
	public void process(EntityContainer entityContainer) {
		sink.process(entityContainer);
	}


	@Override
	public void complete() {
		sink.complete();
	}


	@Override
	public void release() {
		sink.release();
	}


	@Override
	public void setSink(Sink sink) {
		this.sink = sink;
	}
}