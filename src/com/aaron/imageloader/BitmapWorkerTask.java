package com.aaron.imageloader;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.aaron.imageloader.LruDiskCache.Snapshot;

/**
 * 
 * @author Aaron
 *
 */
public class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {

	/**
	 * 加载图片工具类
	 */
	private ImageLoader imageLoader;

	private ImageView imageView;

	/**
	 * 图片url地址
	 */
	private String imageUrl;

	private ImageSize imageSize;

	public BitmapWorkerTask(ImageLoader imageLoader, ImageView imageView) {
		this.imageLoader = imageLoader;
		this.imageView = imageView;
		imageSize = BitmapUtil.getImageViewSize(imageView);
	}

	@Override
	protected Bitmap doInBackground(String... params) {
		imageUrl = params[0];
		FileDescriptor fileDescriptor = null;
		FileInputStream fileInputStream = null;
		// 读取硬盘缓存对象
		Snapshot snapshot = null;
		try {
			// 生成图片Url对应的key
			final String key = imageLoader.hashKeyForDisk(imageUrl);
			// 查找key对应的缓存
			snapshot = imageLoader.diskCache.get(key);
			if (snapshot == null) {
				LruDiskCache.Editor editor = imageLoader.diskCache.edit(key);
				if (editor != null) {
					OutputStream outputStream = editor.newOutputStream(0);
					// 网络获取Bitmap写入指定输出流
					if (downloadUrlToString(imageUrl, outputStream)) {
						// 提交生效
						editor.commit();
					} else {// 放弃此次写入
						editor.abort();
					}
				}
				// 写入缓存后，再次查找对应的缓存
				snapshot = imageLoader.diskCache.get(key);
			}
			if (snapshot != null) {
				// 读取缓存文件
				fileInputStream = (FileInputStream) snapshot.getInputStream(0);
				fileDescriptor = fileInputStream.getFD();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		Bitmap bitmap = null;
		if (fileDescriptor != null) {
			bitmap = BitmapUtil.decodeSampleBitmap(fileDescriptor, imageSize.width, imageSize.height);
		}
		if (bitmap != null) {
			// 图片下载完缓存到lrucache中
			imageLoader.addBitmapToMemoryCache(params[0], bitmap);
		}
		return bitmap;
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		super.onPostExecute(result);
		if (imageView != null && imageUrl != null && imageUrl.equals(imageView.getTag().toString())) {
			if (result != null) {
				imageView.setImageBitmap(result);
			} else {// 加载失败
				if (imageLoader.loadfaildBitmap != null) {
					imageView.setImageBitmap(imageLoader.loadfaildBitmap);
				}
			}
		}
		// 从集合中移除任务
		imageLoader.taskCollections.remove(this);
	}

	/**
	 * 下载图片
	 * 
	 * @param imageUrl
	 * @return
	 */
	public Bitmap downloadBitmap(String imageUrl) {
		Bitmap bitmap = null;
		HttpURLConnection con = null;
		try {
			URL url = new URL(imageUrl);
			con = (HttpURLConnection) url.openConnection();
			con.setConnectTimeout(5 * 1000);
			con.setReadTimeout(10 * 1000);
			bitmap = BitmapFactory.decodeStream(con.getInputStream());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}
		return bitmap;
	}

	/**
	 * 建立http请求，获取Bitmap写入输出流
	 * 
	 * @param imageUrl
	 * @param outputStream
	 * @return
	 */
	public boolean downloadUrlToString(String imageUrl, OutputStream outputStream) {
		HttpURLConnection urlConnection = null;
		BufferedOutputStream out = null;
		BufferedInputStream in = null;
		try {
			final URL url = new URL(imageUrl);
			urlConnection = (HttpURLConnection) url.openConnection();
			in = new BufferedInputStream(urlConnection.getInputStream(), 8 * 1024);
			out = new BufferedOutputStream(outputStream, 8 * 1024);
			int b;
			while ((b = in.read()) != -1) {
				out.write(b);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

}
