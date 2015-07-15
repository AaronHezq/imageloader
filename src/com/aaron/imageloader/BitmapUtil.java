package com.aaron.imageloader;

import java.io.FileDescriptor;
import java.lang.reflect.Field;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.util.DisplayMetrics;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

/**
 * 
 * @author Aaron
 *
 */
public class BitmapUtil {

	/**
	 * 根据指定比例缩放图片
	 * @param fileDescriptor
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static Bitmap decodeSampleBitmap(FileDescriptor fileDescriptor, int reqWidth, int reqHeight) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
		options.inSampleSize = calculateInSampleSize(options,reqWidth,reqHeight);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFileDescriptor(fileDescriptor,null,options);
	}

	private static int calculateInSampleSize(Options options, int reqWidth, int reqHeight) {
		final int width = options.outWidth;
		final int height = options.outHeight;
		int inSampleSize = 1;
		if (width > reqWidth || height > reqHeight) {
			final int widthRadio = Math.round(width * 1.0f / reqWidth);
			final int heightRadio = Math.round(height * 1.0f / reqHeight);
			inSampleSize = Math.min(widthRadio, heightRadio);
		}
		return inSampleSize;
	}

	
	/**
	 * 根据ImageView获适当的压缩的宽和高
	 * 
	 * @param imageView
	 * @return
	 */
	public static ImageSize getImageViewSize(ImageView imageView) {

		ImageSize imageSize = new ImageSize();
		DisplayMetrics displayMetrics = imageView.getContext().getResources().getDisplayMetrics();

		LayoutParams lp = imageView.getLayoutParams();

		int width = imageView.getWidth();// 获取imageview的实际宽度
		if (width <= 0) {
			width = lp.width;// 获取imageview在layout中声明的宽度
		}
		if (width <= 0) {
			// width = imageView.getMaxWidth();// 检查最大值
			width = getImageViewFieldValue(imageView, "mMaxWidth");
		}
		if (width <= 0) {
			width = displayMetrics.widthPixels;
		}

		int height = imageView.getHeight();// 获取imageview的实际高度
		if (height <= 0) {
			height = lp.height;// 获取imageview在layout中声明的宽度
		}
		if (height <= 0) {
			height = getImageViewFieldValue(imageView, "mMaxHeight");// 检查最大值
		}
		if (height <= 0) {
			height = displayMetrics.heightPixels;
		}
		imageSize.width = width;
		imageSize.height = height;
		return imageSize;
	}

	/**
	 * 通过反射获取imageview的某个属性值
	 * 
	 * @param object
	 * @param fieldName
	 * @return
	 */
	private static int getImageViewFieldValue(Object object, String fieldName) {
		int value = 0;
		try {
			Field field = ImageView.class.getDeclaredField(fieldName);
			field.setAccessible(true);
			int fieldValue = field.getInt(object);
			if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
				value = fieldValue;
			}
		} catch (Exception e) {
		}
		return value;

	}
	
	
}
