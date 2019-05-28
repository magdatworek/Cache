package test;

import Cache.Cache;
/**
 * Sample thread getting value from cache
 */
public class TestThread implements Runnable{
	Cache<Integer, String> cacher;
	public TestThread(Cache<Integer, String> cacher) {
		this.cacher = cacher;
	}
	@Override
	public void run() {
		int i=0;
		while(i<300) {
			cacher.getValue((int)(Math.random()*253+0));
			++i;
		}
	}

}
