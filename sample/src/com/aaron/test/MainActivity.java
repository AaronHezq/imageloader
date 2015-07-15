package com.aaron.test;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.aaron.imageloader.ImageLoader;
import com.aaron.imageloader.R;

public class MainActivity extends ActionBarActivity {

	private GridView mGridView;
	private String[] mUrlStrs = new String[] { "http://img.my.csdn.net/uploads/201407/26/1406383299_1976.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383291_6518.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383291_8239.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383290_9329.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383290_1042.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383275_3977.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383265_8550.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383264_3954.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383264_4787.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383264_8243.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383248_3693.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383243_5120.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383242_3127.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383242_9576.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383242_1721.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383219_5806.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383214_7794.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383213_4418.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383213_3557.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383210_8779.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383172_4577.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383166_3407.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383166_2224.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383166_7301.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383165_7197.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383150_8410.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383131_3736.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383130_5094.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383130_7393.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383129_8813.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383100_3554.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383093_7894.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383092_2432.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383092_3071.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383091_3119.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383059_6589.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383059_8814.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383059_2237.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383058_4330.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383038_3602.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382942_3079.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382942_8125.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382942_4881.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382941_4559.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382941_3845.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382924_8955.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382923_2141.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382923_8437.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382922_6166.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382922_4843.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382905_5804.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382904_3362.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382904_2312.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382904_4960.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382900_2418.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382881_4490.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382881_5935.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382880_3865.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382880_4662.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382879_2553.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382862_5375.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382862_1748.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382861_7618.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382861_8606.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382861_8949.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382841_9821.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382840_6603.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382840_2405.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382840_6354.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382839_5779.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382810_7578.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382810_2436.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382809_3883.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382809_6269.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382808_4179.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382790_8326.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382789_7174.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382789_5170.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382789_4118.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382788_9532.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382767_3184.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382767_4772.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382766_4924.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382766_5762.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382765_7341.jpg" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mGridView = (GridView) findViewById(R.id.id_gridview);
		ImageLoader.getInstance(this).build();
		setUpAdapter();
	}

	private void setUpAdapter() {
		if (MainActivity.this == null || mGridView == null)
			return;

		if (mUrlStrs != null) {
			mGridView.setAdapter(new ListImgItemAdaper(MainActivity.this, 0, mUrlStrs));
		} else {
			mGridView.setAdapter(null);
		}

	}

	private class ListImgItemAdaper extends ArrayAdapter<String> {

		public ListImgItemAdaper(Context context, int resource, String[] datas) {
			super(MainActivity.this, 0, datas);
			Log.e("TAG", "ListImgItemAdaper");
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = MainActivity.this.getLayoutInflater().inflate(R.layout.item_fragment_list_imgs, parent, false);
			}
			ImageView imageview = (ImageView) convertView.findViewById(R.id.id_img);
			ImageLoader.getInstance(MainActivity.this).display(imageview, mUrlStrs[position]);
			//ImageLoader.getInstance(MainActivity.this).display(imageview, mUrlStrs[position],R.drawable.pictures_no);
			return convertView;
		}

	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			System.out.println("getCacheSize:"+ImageLoader.getInstance(this).getCacheSize());
			ImageLoader.getInstance(this).clearCache();
			System.out.println("getCacheSize:"+ImageLoader.getInstance(this).getCacheSize());
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
