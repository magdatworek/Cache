package Cache;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Cache <K,V>{
	private DataAccessor<K,V> accessor;
	private Map<K,V> cache;
	private Map<K,V> cacheLinked;
	/**
	 * Builds cache using synchronized LinkedHashMap, which is access-ordered and thread-safe
	 * @param accessor
	 * @param maxElements
	 */
	public Cache(DataAccessor<K,V> accessor, int maxElements) {
		this.accessor = accessor;
		//defined max size of cache is maxElements, if the size exceeds this number (maxElements+1 which is initial size of HashMap),
		//the least recently accessed element is removed, but HashMap doesn't grown in size as load factor=1 (https://docs.oracle.com/javase/8/docs/api/java/util/LinkedHashMap.html)
		cacheLinked = new LinkedHashMap<>(maxElements+1,1,true){ 
			private static final long serialVersionUID = 6617526239694074350L;
			protected boolean removeEldestEntry(Map.Entry<K,V> eldest) { 
	            return size() > maxElements; 
	        }
	    };
	    //LinkedHashMap isn't synchronized itself, so it has to be synchronized externally
	    cache = Collections.synchronizedMap(cacheLinked);
	}
	public V getElement(K key) {
		return cache.get(key);
	}
	public int getSize() {
		return cache.size();
	}
	/**
	 * Retrieves value from cache based on key, if it's not available, loads it from data source
	 * @param key
	 * @return value
	 */
	public V getValue(K key) {
		if (!cache.containsKey(key)) {
			//I assumed there would be no null values:
			//even if the value for requested key doesn't exist in underlying data source, it would be calculated
			cache.put(key, accessor.getData(key));
			Logger.getLogger(Cache.class.getName()).log(Level.FINE," Data saved to cache");
		}
		Logger.getLogger(Cache.class.getName()).log(Level.FINE," Data retrieved");
		return cache.get(key);
	}
}
