package Cache;

public interface DataAccessor <K,V>{
	public V getData(K key);
}
