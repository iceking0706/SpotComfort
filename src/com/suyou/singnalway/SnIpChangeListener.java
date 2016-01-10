package com.suyou.singnalway;

import java.util.concurrent.ConcurrentMap;

public interface SnIpChangeListener {
	/**
	 * 当map中的信息变化的时候调用
	 * @param snIpMap
	 */
	public void onSnIpChange(ConcurrentMap<String, SnIpPort> snIpMap);
}
