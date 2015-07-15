package com.aaron.imageloader;

import java.util.Collection;

/**
 * 内存缓存
 * @author Aaron
 *
 * @param <K>
 * @param <V>
 */
public interface MemoryCacheAware<K, V> {

	/**
	 * 添加
	 * @param key
	 * @param value
	 * @return
	 */
	boolean put(K key, V value);
	
	/**
	 * 获取
	 * @param key
	 */
	V get(K key);
	
	/**
	 * 移除
	 * @param key
	 */
	void remove(K key);
	
	/**
	 * 清空
	 * @param key
	 */
	void clear();
	
	/**
	 * 获取所有Key
	 * @return
	 */
	Collection<K> keys();
	
}
