package de.vwistuttgart.openstreetmap.osmosis.fastusedfilter.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.EntityType;

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
}
