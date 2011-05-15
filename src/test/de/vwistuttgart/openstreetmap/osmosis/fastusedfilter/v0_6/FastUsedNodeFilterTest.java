package de.vwistuttgart.openstreetmap.osmosis.fastusedfilter.v0_6;

import java.io.File;

import org.junit.Test;
import org.openstreetmap.osmosis.core.domain.v0_6.EntityType;
import org.openstreetmap.osmosis.core.filter.common.IdTrackerType;
import org.openstreetmap.osmosis.tagfilter.v0_6.WayKeyValueFilter;
import org.openstreetmap.osmosis.test.task.v0_6.SinkEntityInspector;
import org.openstreetmap.osmosis.xml.common.CompressionMethod;
import org.openstreetmap.osmosis.xml.v0_6.FastXmlReader;

import de.vwistuttgart.openstreetmap.osmosis.fastusedfilter.test.AcceptAllFilter;
import de.vwistuttgart.openstreetmap.osmosis.fastusedfilter.test.EmptySource;
import de.vwistuttgart.openstreetmap.osmosis.fastusedfilter.test.TestUtil;


public class FastUsedNodeFilterTest {

	@Test
	public void standard() throws Exception {
		File inFile = TestUtil.makeDataFile("/data/input/v0_6/inputBound.xml");
		FastXmlReader reader1 = new FastXmlReader(inFile, false, CompressionMethod.None);
		FastXmlReader reader2 = new FastXmlReader(inFile, false, CompressionMethod.None);
		try {
			WayKeyValueFilter wkvFilter = new WayKeyValueFilter("a.b");
			SinkEntityInspector result = TestUtil.runFastUsedFilter(
					new FastUsedNodeFilter(IdTrackerType.IdList, 1), 
					reader1, reader2, wkvFilter);
			TestUtil.assertCount(5, result.getProcessedEntities());
			TestUtil.assertEntitiesCountByType(1, EntityType.Bound, result.getProcessedEntities());
			TestUtil.assertEntitiesCountByType(1, EntityType.Way, result.getProcessedEntities());
			TestUtil.assertEntitiesCountByType(3, EntityType.Node, result.getProcessedEntities());
		} finally {
			inFile.delete();
		}
	}


	@Test
	public void shuffled() throws Exception {
		File inFile = TestUtil.makeDataFile("/data/input/v0_6/inputShuffled.xml");
		FastXmlReader reader1 = new FastXmlReader(inFile, false, CompressionMethod.None);
		FastXmlReader reader2 = new FastXmlReader(inFile, false, CompressionMethod.None);
		try {
			WayKeyValueFilter wkvFilter = new WayKeyValueFilter("a.b");
			SinkEntityInspector result = TestUtil.runFastUsedFilter(
					new FastUsedNodeFilter(IdTrackerType.IdList, 1), 
					reader1, reader2, wkvFilter);
			TestUtil.assertCount(4, result.getProcessedEntities());
			TestUtil.assertEntitiesCountByType(1, EntityType.Way, result.getProcessedEntities());
			TestUtil.assertEntitiesCountByType(3, EntityType.Node, result.getProcessedEntities());
		} finally {
			inFile.delete();
		}
	}


	@Test
	public void allEmpty() throws Exception {
		SinkEntityInspector result = TestUtil.runFastUsedFilter(
				new FastUsedNodeFilter(IdTrackerType.IdList, 1), 
				new EmptySource(), new EmptySource(), new AcceptAllFilter());
		TestUtil.assertCount(0, result.getProcessedEntities());
	}


	@Test
	public void nodesEmpty() throws Exception {
		File inFile = TestUtil.makeDataFile("/data/input/v0_6/inputBound.xml");
		FastXmlReader reader1 = new FastXmlReader(inFile, false, CompressionMethod.None);
		try {
			SinkEntityInspector result = TestUtil.runFastUsedFilter(
					new FastUsedNodeFilter(IdTrackerType.IdList, 1), 
					new EmptySource(), reader1, new AcceptAllFilter());
			TestUtil.assertCount(3, result.getProcessedEntities());
			TestUtil.assertEntitiesCountByType(1, EntityType.Bound, result.getProcessedEntities());
			TestUtil.assertEntitiesCountByType(2, EntityType.Way, result.getProcessedEntities());
		} finally {
			inFile.delete();
		}
	}


	@Test
	public void waysEmpty() throws Exception {
		File inFile = TestUtil.makeDataFile("/data/input/v0_6/inputBound.xml");
		FastXmlReader reader1 = new FastXmlReader(inFile, false, CompressionMethod.None);
		try {
			SinkEntityInspector result = TestUtil.runFastUsedFilter(
					new FastUsedNodeFilter(IdTrackerType.IdList, 1), 
					reader1, new EmptySource(), new AcceptAllFilter());
			TestUtil.assertCount(0, result.getProcessedEntities());
		} finally {
			inFile.delete();
		}
	}


	@Test
	public void relation() throws Exception {
		File inFile = TestUtil.makeDataFile("/data/input/v0_6/inputRelation.xml");
		FastXmlReader reader1 = new FastXmlReader(inFile, false, CompressionMethod.None);
		FastXmlReader reader2 = new FastXmlReader(inFile, false, CompressionMethod.None);
		try {
			SinkEntityInspector result = TestUtil.runFastUsedFilter(
					new FastUsedNodeFilter(IdTrackerType.IdList, 1), 
					reader1, reader2, new AcceptAllFilter());
			TestUtil.assertCount(3, result.getProcessedEntities());
			TestUtil.assertEntitiesCountByType(1, EntityType.Bound, result.getProcessedEntities());
			TestUtil.assertEntitiesCountByType(1, EntityType.Relation, result.getProcessedEntities());
			TestUtil.assertEntitiesCountByType(1, EntityType.Node, result.getProcessedEntities());
		} finally {
			inFile.delete();
		}
	}

}
