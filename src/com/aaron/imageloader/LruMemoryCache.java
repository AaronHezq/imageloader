package com.aaron.imageloader;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import android.graphics.Bitmap;

/**
 * 基于Lru算法的内存缓存
 * 
 * @author Aaron
 * 
 */
public class LruMemoryCache implements MemoryCacheAware<String, Bitmap> {

	/**
	 * LRU缓存
	 */
	private final LinkedHashMap<String, Bitmap> cache;

	/**
	 * 最大缓存空间
	 */
	private final int maxSize;

	/**
	 * 当前缓存空间
	 */
	private int currentSize;

	public LruMemoryCache(int maxSize) {
		if (maxSize <= 0) {
			throw new IllegalArgumentException("maxSize <= 0");
		}
		this.maxSize = maxSize;
		// 初始化队列按照访问顺序从少到多排列
		this.cache = new LinkedHashMap<String, Bitmap>(0, 0.75f, true);
	}

	@Override
	public boolean put(String key, Bitmap value) {
		if (key == null || value == null) {
			throw new NullPointerException("key == null || value == null");
		}
		synchronized (this) {
			currentSize += sizeOf(key, value);
			Bitmap previous = cache.put(key, value);
			if (previous != null) {
				currentSize -= sizeOf(key, previous);
			}
		}
		trimToSize(maxSize);
		return true;
	}

	@Override
	public Bitmap get(String key) {
		if (key == null) {
			throw new NullPointerException("key == null");
		}
		synchronized (this) {
			return this.cache.get(key);
		}
	}

	/**
	 * 从缓存中移除指定Key对应的Bitmap
	 */
	@Override
	public void remove(String key) {
		if (key == null) {
			throw new NullPointerException("key == null");
		}
		synchronized (this) {
			Bitmap previous = cache.remove(key);
			if (previous != null) {
				currentSize -= sizeOf(key, previous);
			}
		}
	}

	@Override
	public void clear() {
		trimToSize(-1);
	}

	@Override
	public Collection<String> keys() {
		return new HashSet<String>(cache.keySet());
	}

	/**
	 * 返回图片大小
	 * 
	 * @param key
	 * @param previous
	 * @return
	 */
	private int sizeOf(String key, Bitmap value) {
		return value.getRowBytes() * value.getHeight();
	}

	/**
	 * 超出缓存预定值时，将最近最少使用的对象移除缓存
	 * 
	 * @param maxSize
	 */
	private void trimToSize(int maxSize) {
		while (true) {
			String key;
			Bitmap value;
			synchronized (this) {
				if (currentSize < 0 || (cache.isEmpty() && (currentSize != 0))) {
					throw new IllegalArgumentException("currentSize<0||(cache.isEmpty()&&(currentSize!=0))");
				}
				if (currentSize < maxSize || cache.isEmpty()) {
					break;
				}
				Map.Entry<String, Bitmap> toEvict = cache.entrySet().iterator().next();
				if (toEvict == null) {
					break;
				}
				key = toEvict.getKey();
				value = toEvict.getValue();
				cache.remove(key);
				currentSize -= sizeOf(key, value);
			}
		}
	}

}
