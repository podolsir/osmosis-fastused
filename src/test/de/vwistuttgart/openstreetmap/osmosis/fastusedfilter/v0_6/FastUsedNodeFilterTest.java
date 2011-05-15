package de.vwistuttgart.openstreetmap.osmosis.fastusedfilter.v0_6;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.EntityType;
import org.openstreetmap.osmosis.core.filter.common.IdTrackerType;
import org.openstreetmap.osmosis.tagfilter.v0_6.WayKeyValueFilter;
import org.openstreetmap.osmosis.test.task.v0_6.SinkEntityInspector;
import org.openstreetmap.osmosis.xml.common.CompressionMethod;
import org.openstreetmap.osmosis.xml.v0_6.FastXmlReader;

public class FastUsedNodeFilterTest {

	@Test
	public void standard() throws Exception {
		File inFile = makeDataFile("/data/input/v0_6/inputBound.xml");
		try {
			SinkEntityInspector result = runFastUsedNode(inFile);
			assertCount(5, result.getProcessedEntities());
			assertEntitiesCountByType(1, EntityType.Bound, result.getProcessedEntities());
			assertEntitiesCountByType(1, EntityType.Way, result.getProcessedEntities());
			assertEntitiesCountByType(3, EntityType.Node, result.getProcessedEntities());
		} finally {
			inFile.delete();
		}
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

	private SinkEntityInspector runFastUsedNode(File inFile)
			throws InterruptedException {
		FastXmlReader reader1 = new FastXmlReader(inFile, false,
				CompressionMethod.None);
		FastXmlReader reader2 = new FastXmlReader(inFile, false,
				CompressionMethod.None);

		WayKeyValueFilter wkvFilter = new WayKeyValueFilter("a.b");
		FastUsedNodeFilter usedFilter = new FastUsedNodeFilter(
				IdTrackerType.IdList, 1);
		SinkEntityInspector inspector = new SinkEntityInspector();

		reader1.setSink(usedFilter.getSink(0));
		reader2.setSink(wkvFilter);
		wkvFilter.setSink(usedFilter.getSink(1));
		usedFilter.setSink(inspector);

		Thread readerThread1 = new Thread(reader1);
		Thread readerThread2 = new Thread(reader2);
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
}
