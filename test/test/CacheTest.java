package test;

import Cache.Cache;
import Cache.DataAccessor;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;


class CacheTest {
	/**
	 * Tests if values pulled from cache are correct
	 */
	@Test
	void testValues() {
		Cache<Integer, String> cacher = new Cache<>(new SampleDataProvider(),100);
		for (int i = 1; i < 10; ++i) {
			
			for (int j = 1; j < 100; ++j) {
				
				assertEquals(getExpectedValue(j),cacher.getValue(j));
			}
		}
	}
	/**
	 * Tests if least recently accessed value is removed and if element already added to the cache is accessible without repeating calculation
	 */
	@Test
	void testRemove() {
		Cache<Integer, Double> cacher = new Cache<>(new SampleDataLeastRecent(),10);
		for (int i = 1; i < 12; ++i) {
			cacher.getValue(i);
		}
		assertEquals(cacher.getElement(1),null);
		assertEquals(cacher.getElement(2),2.0);
		cacher.getValue(2);
		cacher.getValue(12);
		assertEquals(cacher.getElement(3),null);
	}
	/**
	 * Checks if size of cache exceeds given max size after adding more elements than defined max size
	 */
	@Test
	void testMaxSize() {
		Cache<Integer, String> cacher = new Cache<>(new SampleDataProvider(),100);
		for(int j=0;j<2;++j) {
			for(int i=0; i<100;++i) {
				cacher.getValue(i);
			}
			assertEquals(cacher.getSize(),100);
		}
	}
	/**
	 * Tests if complex calculations are slower than retrieving data from cache
	 */
	@Test
	void testSpeed() {
		Cache<Integer, String> cacher = new Cache<>(new SampleDataProvider(),10000);
		long timeWithCacher = (new Date()).getTime();
		for(int j=0;j<100;++j) {
			for(int i=0; i<100;++i) {
				cacher.getValue(i);
			}
		}
		timeWithCacher = (timeWithCacher - (new Date().getTime()))*(-1);
		Logger.getLogger(CacheTest.class.getName()).log(Level.INFO," Time using cache: "+timeWithCacher);
		long timeWithoutCacher = (new Date()).getTime();
		for(int j=0;j<100;++j) {
			for(int i=0; i<100;++i) {
				getExpectedValue(i);
			}
		}
		timeWithoutCacher = (timeWithoutCacher - (new Date().getTime()))*(-1);
		Logger.getLogger(CacheTest.class.getName()).log(Level.INFO," Time without cache: "+timeWithoutCacher);
		assertTrue(timeWithCacher<timeWithoutCacher);
	}
	/**
	 * Tests if used collection is thread-safe
	 */
	@Test
	void testThread() {
		Cache<Integer, String> cacher = new Cache<>(new SampleDataProvider(),100);
		boolean exception = false;
		try {
			for(int i=0;i<5;++i) {
			new Thread(new TestThread(cacher)).start();
			}
		}catch(ConcurrentModificationException e){
			e.printStackTrace();
			exception = true;
			Logger.getLogger(CacheTest.class.getName()).log(Level.SEVERE,null,e);
		}
		assertNotNull(cacher);
		assertFalse(exception);
	}
	/**
	 * Generates values on spot
	 * @param key
	 * @return long string
	 */
	String getExpectedValue(int key) {
		String value = "value";
		for (int i=0;i<key;++i) {
			value=value+i+" value";
			Logger.getLogger(CacheTest.class.getName()).log(Level.FINE," Data saved to cache");
		}
		return value;
	}
}
/**
 * Sample Data Source generating value if needed
 *
 */
class SampleDataProvider implements DataAccessor<Integer, String> {
	/**
	 * Gets data from underlying data source, in this case, generates it
	 */
	@Override
	public String getData(Integer key) {
		String value = "value";
			for (int i=0;i<key;++i) {
				value=value+i+" value";
			}
		return value;
	}
}

class SampleDataLeastRecent implements DataAccessor<Integer, Double> {
	@Override
	public Double getData(Integer key) {
		double value = key;
		return value;
	}
}
