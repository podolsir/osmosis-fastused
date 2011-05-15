package de.vwistuttgart.openstreetmap.osmosis.fastusedfilter.v0_6;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import junit.framework.Assert;

import org.junit.Test;
import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.EntityType;
import org.openstreetmap.osmosis.core.filter.common.IdTrackerType;
import org.openstreetmap.osmosis.core.task.v0_6.RunnableSource;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;
import org.openstreetmap.osmosis.core.task.v0_6.SinkSource;
import org.openstreetmap.osmosis.tagfilter.v0_6.WayKeyValueFilter;
import org.openstreetmap.osmosis.test.task.v0_6.SinkEntityInspector;
import org.openstreetmap.osmosis.xml.common.CompressionMethod;
import org.openstreetmap.osmosis.xml.v0_6.FastXmlReader;

public class FastUsedNodeFilterTest {

	@Test
	public void standard() throws Exception {
		File inFile = makeDataFile("/data/input/v0_6/inputBound.xml");
		FastXmlReader reader1 = new FastXmlReader(inFile, false,
				CompressionMethod.None);
		FastXmlReader reader2 = new FastXmlReader(inFile, false,
				CompressionMethod.None);
		try {
			WayKeyValueFilter wkvFilter = new WayKeyValueFilter("a.b");
			SinkEntityInspector result = runFastUsedNode(reader1, reader2, wkvFilter);
			assertCount(5, result.getProcessedEntities());
			assertEntitiesCountByType(1, EntityType.Bound, result.getProcessedEntities());
			assertEntitiesCountByType(1, EntityType.Way, result.getProcessedEntities());
			assertEntitiesCountByType(3, EntityType.Node, result.getProcessedEntities());
		} finally {
			inFile.delete();
		}
	}
	
	@Test
	public void allEmpty() throws Exception {
		SinkEntityInspector result = runFastUsedNode(
				new EmptySource(), new EmptySource(), new AcceptAllFilter());
		assertCount(0, result.getProcessedEntities());
	}
	
	private static void assertCount(int expected, Iterable<?> iterable) {
		int count = 0;
		for (@SuppressWarnings("unused") Object obj : iterable) {
			count++;
		}
		Assert.assertEquals(expected, count);
	}
	
	private static void assertEntitiesCountByType(int expected, EntityType type, 
			Iterable<? extends EntityContainer> iterable) {
		int count = 0;
		for (EntityContainer e : iterable) {
			if (e.getEntity().getType() == type) {
				count++;
			}
		}
		
		Assert.assertEquals(expected, count);
	}

	private SinkEntityInspector runFastUsedNode(RunnableSource nodeSource, 
			RunnableSource waySource, SinkSource wayFilter)
			throws Exception {

		FastUsedNodeFilter usedFilter = new FastUsedNodeFilter(
				IdTrackerType.IdList, 1);
		SinkEntityInspector inspector = new SinkEntityInspector();

		nodeSource.setSink(usedFilter.getSink(0));
		waySource.setSink(wayFilter);
		wayFilter.setSink(usedFilter.getSink(1));
		usedFilter.setSink(inspector);

		Thread readerThread1 = new Thread(nodeSource);
		Thread readerThread2 = new Thread(waySource);
		Thread usedFilterThread = new Thread(usedFilter);

		readerThread1.start();
		readerThread2.start();
		usedFilterThread.start();
		usedFilterThread.join();
		readerThread1.join();
		readerThread2.join();

		return inspector;
	}

	private File makeDataFile(String resourcePath) throws Exception {
		File file = File.createTempFile("fastusedfilter.test.", ".xml");
		FileOutputStream os = new FileOutputStream(file);
		byte[] buf = new byte[1024];
		InputStream is = getClass().getResourceAsStream(resourcePath);
		int len = 0;
		while ((len = is.read(buf)) != -1) {
			os.write(buf, 0, len);
		}
		os.close();
		return file;
	}
	
	private static class EmptySource implements RunnableSource {

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
	
	private static class RejectAllFilter implements SinkSource {

		private Sink sink;

		@Override
		public void process(EntityContainer arg0) {
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

	private static class AcceptAllFilter implements SinkSource {

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

}
