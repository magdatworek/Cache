# Cache
  Implementation of an in-memory cache to allow the results of long-running calculations or data retrievals to be stored in this memory

+ The cache provides a method to request the value for a key.
+ If the cache does not contain the requested data it loads it from an underlying data source, and then cache it for future requests.
+ If the cache exceeds a set number of items in size then the least recently requested items are removed.
+ It can be instantiated for different types of keys and values.
+ The key is the only input to the calculation which produces the value. The same key always produces the same value.
