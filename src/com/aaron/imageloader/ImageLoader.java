package com.aaron.imageloader;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.widget.ImageView;

/**
 * 加载图片类
 * 
 * @author Aaron
 * 
 */
public class ImageLoader {

	private static ImageLoader imageLoader;

	private Context context;

	/**
	 * 异步任务执行者
	 */
	private Executor executor;

	/**
	 * 加载任务集合
	 */
	public Set<BitmapWorkerTask> taskCollections;

	/**
	 * 线程池大小默认100
	 */
	private int poolSize = 100;

	/**
	 * 核心线程数默认3
	 */
	private int coreTasks = 3;

	/**
	 * 内存缓存
	 */
	public LruMemoryCache memoryCache;

	/**
	 * 硬盘缓存目录
	 */
	private File cacheDir;

	/**
	 * 硬盘缓存
	 */
	public LruDiskCache diskCache;

	/**
	 * 硬盘缓存大小，以M为单位
	 */
	private int diskCacheSize = 10;

	/**
	 * 加载中显示的图片
	 */
	public Bitmap loadingBitmap;

	/**
	 * 加载失败显示的图片
	 */
	public Bitmap loadfaildBitmap;

	/**
	 * 获取单例
	 * 
	 * @param context
	 * @return
	 */
	public static ImageLoader getInstance(Context context) {
		if (imageLoader == null) {
			imageLoader = new ImageLoader(context);
		}
		return imageLoader;
	}

	private ImageLoader(Context context) {
		this.context = context;
	}
	

	public void build() {
		// 初始化线程池，核心线程数3 线程池大小100
		executor = new ThreadPoolExecutor(coreTasks, poolSize, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
		// 初始化任务集合
		taskCollections = new HashSet<BitmapWorkerTask>();
		// 获取应用的最大可用内存
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int cacheSize = maxMemory / 8;
		memoryCache = new LruMemoryCache(cacheSize);
		try {
			cacheDir = getDiskCacheDir(context, "bitmap");
			if (!cacheDir.exists()) {
				cacheDir.mkdirs();
			}
			// 创建LruDiskCache实例，初始化硬盘缓存
			diskCache = LruDiskCache.open(cacheDir, getAppVersion(context), 1, diskCacheSize * 1024 * 1024);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (loadingBitmap == null) {
			setLoadingDrawable(R.drawable.pictures_no);
		}
	}

	/**
	 * 加载图片
	 * 
	 * @param imageView
	 *            图片空间
	 * @param imageUrl
	 *            图片地址
	 */
	public void display(ImageView imageView, String imageUrl) {
		display(imageView, imageUrl, -1, -1);
	}

	/**
	 * 加载图片
	 * 
	 * @param imageView
	 *            图片空间
	 * @param imageUrl
	 *            图片地址
	 * @param loadingResId
	 *            加载中的图片id
	 */
	public void display(ImageView imageView, String imageUrl, int loadingResId) {
		display(imageView, imageUrl, loadingResId, -1);
	}

	/**
	 * 加载图片
	 * 
	 * @param imageView
	 *            图片空间
	 * @param imageUrl
	 *            图片地址
	 * @param loadingResId
	 *            加载中的图片id
	 * @param loadfailResId
	 *            加载失败的图片id
	 */
	public void display(final ImageView imageView, final String imageUrl, final int loadingResId, final int loadfailResId) {
		imageView.setTag(imageUrl);
		if (imageView != null) {
			if (loadingResId > 0) {
				imageView.setImageResource(loadingResId);
			} else {
				if (loadingBitmap != null) {
					imageView.setImageBitmap(loadingBitmap);
				}
			}
		}
		try {
			Bitmap bitmap = getBitmapFromeMemoryCache(imageUrl);
			if (bitmap == null) {
				BitmapWorkerTask task = new BitmapWorkerTask(imageLoader, imageView);
				taskCollections.add(task);
				task.executeOnExecutor(executor, imageUrl);
			} else {
				if (imageView != null && imageView.getTag().toString().equals(imageUrl)) {
					imageView.setImageBitmap(bitmap);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置线程池大小
	 * 
	 * @param poolSize
	 */
	public ImageLoader setPoolSize(int poolSize) {
		this.poolSize = poolSize;
		return imageLoader;
	}

	/**
	 * 设置核心任务数量
	 * 
	 * @param coreTasks
	 */
	public ImageLoader setCoreTasks(int coreTasks) {
		this.coreTasks = coreTasks;
		return imageLoader;
	}

	/**
	 * 设置加载中图片
	 * 
	 * @param resourceId
	 */
	public void setLoadingDrawable(int resourceId) {
		loadingBitmap = getBitmapFormRes(resourceId);
	}

	/**
	 * 设置加载失败图片
	 * 
	 * @param resourceId
	 */
	public void setLoadFailDrawable(int resourceId) {
		loadfaildBitmap = getBitmapFormRes(resourceId);
	}

	public Bitmap getBitmapFormRes(int resourceId) {
		return BitmapFactory.decodeResource(context.getResources(), resourceId);
	}

	/**
	 * 将图片存储到memoryCache中
	 * 
	 * @param key
	 * @param value
	 */
	public void addBitmapToMemoryCache(String key, Bitmap value) {
		if (getBitmapFromeMemoryCache(key) == null) {
			memoryCache.put(key, value);
		}
	}

	/**
	 * 从memoryCache中获取一张图片
	 * 
	 * @param key
	 * @return
	 */
	private Bitmap getBitmapFromeMemoryCache(String key) {
		return memoryCache.get(key);
	}

	/**
	 * 取消所有下载或等待下载的任务
	 */
	public void cancleAllTasks() {
		if (taskCollections != null) {
			for (BitmapWorkerTask task : taskCollections) {
				task.cancel(false);
			}
		}
	}

	/**
	 * 使用MD5算法对传入的key进行加密并返回，避免url命名时存在不合法
	 * 
	 * @param key
	 * @return
	 */
	public String hashKeyForDisk(String key) {
		String cacheKey;
		try {
			final MessageDigest mDigest = MessageDigest.getInstance("MD5");
			mDigest.update(key.getBytes());
			cacheKey = bytesToHexString(mDigest.digest());
		} catch (NoSuchAlgorithmException e) {
			cacheKey = String.valueOf(key.hashCode());
		}
		return cacheKey;
	}

	public String bytesToHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		String tmp = null;
		for (byte b : bytes) {
			tmp = Integer.toHexString(0xFF & b);
			if (tmp.length() == 1) {
				sb.append("0");
			}
			sb.append(tmp);
		}
		return sb.toString();
	}

	/**
	 * 根据传入的uniqueName获得硬盘缓存地址
	 * 
	 * @param context
	 * @param uniqueName
	 * @return
	 */
	public File getDiskCacheDir(Context context, String uniqueName) {
		String cachePath;
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			cachePath = context.getExternalCacheDir().getPath();
		} else {
			cachePath = context.getCacheDir().getPath();
		}
		return new File(cachePath + File.separator + uniqueName);
	}

	/**
	 * 获取当前系统版本号
	 * 
	 * @param context
	 * @return
	 */
	public int getAppVersion(Context context) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return info.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * 设置磁盘缓存大小，以M为单位
	 * 
	 * @param diskCacheSize
	 */
	public ImageLoader setDiskCacheSize(int diskCacheSize) {
		this.diskCacheSize = diskCacheSize;
		return imageLoader;
	}

	/**
	 * 返回当前缓存文件大小，以byte为单位
	 * 
	 * @return
	 */
	public long getCacheSize() {
		if (diskCache != null) {
			return diskCache.size();
		}
		return 0;
	}

	/**
	 * 将缓存记录同步到journal文件中
	 */
	public void flushCache() {
		if (diskCache != null) {
			try {
				diskCache.flush();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 清空缓存
	 */
	public void clearCache() {
		if (diskCache != null) {
			try {
				diskCache.delete();
				// 恢复LruDiskCache实例，初始化硬盘缓存
				diskCache = LruDiskCache.open(cacheDir, getAppVersion(context), 1, diskCacheSize * 1024 * 1024);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 关闭缓存
	 */
	public void closeCache() {
		if (diskCache != null) {
			try {
				diskCache.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
