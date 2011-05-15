package de.vwistuttgart.openstreetmap.osmosis.fastusedfilter.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.EntityType;
import org.openstreetmap.osmosis.core.task.v0_6.MultiSinkRunnableSource;
import org.openstreetmap.osmosis.core.task.v0_6.RunnableSource;
import org.openstreetmap.osmosis.core.task.v0_6.SinkSource;
import org.openstreetmap.osmosis.test.task.v0_6.SinkEntityInspector;

import de.vwistuttgart.openstreetmap.osmosis.fastusedfilter.v0_6.FastUsedNodeFilterTest;

import junit.framework.Assert;


public class TestUtil {
	public static File makeDataFile(String resourcePath) throws Exception {
		File file = File.createTempFile("fastusedfilter.test.", ".xml");
		FileOutputStream os = new FileOutputStream(file);
		byte[] buf = new byte[1024];
		InputStream is = FastUsedNodeFilterTest.class.getResourceAsStream(resourcePath);
		int len = 0;
		while ((len = is.read(buf)) != -1) {
			os.write(buf, 0, len);
		}
		os.close();
		return file;
	}

	public static void assertCount(int expected, Iterable<?> iterable) {
		int count = 0;
		for (@SuppressWarnings("unused")
		Object obj : iterable) {
			count++;
		}
		Assert.assertEquals(expected, count);
	}

	public static void assertEntitiesCountByType(int expected, EntityType type,
			Iterable<? extends EntityContainer> iterable) {
		int count = 0;
		for (EntityContainer e : iterable) {
			if (e.getEntity().getType() == type) {
				count++;
			}
		}
	
		Assert.assertEquals(expected, count);
	}

	public static SinkEntityInspector runFastUsedFilter(MultiSinkRunnableSource multiFilter, RunnableSource nodeSource, RunnableSource waySource,
			SinkSource wayFilter) throws Exception {
	
		SinkEntityInspector inspector = new SinkEntityInspector();
	
		nodeSource.setSink(multiFilter.getSink(0));
		waySource.setSink(wayFilter);
		wayFilter.setSink(multiFilter.getSink(1));
		multiFilter.setSink(inspector);
	
		Thread readerThread1 = new Thread(nodeSource);
		Thread readerThread2 = new Thread(waySource);
		Thread multiFilterThread = new Thread(multiFilter);
	
		readerThread1.start();
		readerThread2.start();
		multiFilterThread.start();
		multiFilterThread.join();
		readerThread1.join();
		readerThread2.join();
	
		return inspector;
	}
}
