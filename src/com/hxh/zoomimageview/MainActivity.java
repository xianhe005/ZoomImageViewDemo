package com.hxh.zoomimageview;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final int SAVE_ERROR = -1;
	private static final int SAVE_SUCCESS = 0;
	private static final int SAVE_START = 1;
	private static final int SAVE_END = 2;

	private TouchImageView touchImageView;
	private SquareView squareView;
	private BitmapRegionDecoder mDecoder;
	private final Rect mRect = new Rect();
	private View decorView;
	private FrameLayout fl;
	private Button btnSave, btnGet;

	private ProgressDialog mProgressDialog;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SAVE_START:
				btnSave.setEnabled(false);
				mProgressDialog.show();
				break;
			case SAVE_SUCCESS:
				Toast.makeText(MainActivity.this, "保存成功", Toast.LENGTH_LONG)
						.show();
				break;
			case SAVE_ERROR:
				Toast.makeText(MainActivity.this, "保存失败", Toast.LENGTH_LONG)
						.show();
				break;
			case SAVE_END:
				btnSave.setEnabled(true);
				squareView.setVisibility(View.VISIBLE);
				mProgressDialog.dismiss();
				break;
			default:
				break;
			}
		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		touchImageView = (TouchImageView) findViewById(R.id.touchImageView);
		fl = (FrameLayout) findViewById(R.id.fl);
		squareView = new SquareView(this);
		fl.addView(squareView, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);

		btnSave = (Button) findViewById(R.id.btnSave);
		btnGet = (Button) findViewById(R.id.btnGet);

		decorView = getWindow().getDecorView();
		decorView.setDrawingCacheEnabled(true);
		decorView.buildDrawingCache();

		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setCancelable(false);
		mProgressDialog.setMessage("正在保存......");

		btnSave.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				save();
			}
		});

		btnGet.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				/* 开启Pictures画面Type设定为image */
				intent.setType("image/*");
				/* 使用Intent.ACTION_GET_CONTENT这个Action */
				intent.setAction(Intent.ACTION_GET_CONTENT);
				/* 取得相片后返回本画面 */
				startActivityForResult(intent, 1);
			}
		});
	}

	private void save() {
		squareView.setVisibility(View.INVISIBLE);
		new Thread(new Runnable() {

			@Override
			public void run() {
				mHandler.sendEmptyMessage(SAVE_START);
				try {
					mDecoder = BitmapRegionDecoder.newInstance(
							Bitmap2InputStream(decorView.getDrawingCache(), 100),
							true);
					int left = squareView.getSquareLeft();
					int top = squareView.getSquareTop() + fl.getTop();
					int right = left + squareView.getsWidth();
					int bottom = top + squareView.getsWidth();

					mRect.set(left, top, right, bottom);

					Bitmap bm = mDecoder.decodeRegion(mRect, null);
					boolean saveFlag = saveBitmap(bm, "test");
					if (saveFlag) {
						mHandler.sendEmptyMessage(SAVE_SUCCESS);
					} else {
						mHandler.sendEmptyMessage(SAVE_ERROR);
					}
				} catch (IOException e) {
					e.printStackTrace();
					mHandler.sendEmptyMessage(SAVE_ERROR);
				} finally {
					mHandler.sendEmptyMessage(SAVE_END);
				}

			}
		}).start();
	}

	private void save2() {
		try {
			mDecoder = BitmapRegionDecoder.newInstance(
					Bitmap2InputStream(touchImageView.getDrawingCache(), 100),
					true);
			float[] values = new float[9];
			touchImageView.getMatrix().getValues(values);
			int left = (int) (values[2] + squareView.getSquareLeft());
			int top = (int) (values[5] + squareView.getSquareTop());
			int right = left + squareView.getsWidth();
			int bottom = top + squareView.getsWidth();
			setImageRegion(left, top, right, bottom);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void setImageRegion(int left, int top, int right, int bottom) {
		mRect.set(left, top, right, bottom);
		Bitmap bm = mDecoder.decodeRegion(mRect, null);
		saveBitmap(bm, "test");
	}

	private InputStream Bitmap2InputStream(Bitmap bm, int quality) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, quality, baos);
		InputStream is = new ByteArrayInputStream(baos.toByteArray());
		return is;
	}

	@SuppressLint("SdCardPath")
	public boolean saveBitmap(Bitmap mBitmap, String bitName) {
		boolean flag = false;
		File f = new File("/sdcard/" + bitName + ".jpg");
		if (f.exists()) {
			f.delete();
		}
		try {
			f.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
			flag = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
		try {
			fOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return flag;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			Uri uri = data.getData();
			ContentResolver cr = this.getContentResolver();
			try {
				Bitmap bitmap = BitmapFactory.decodeStream(cr
						.openInputStream(uri));
				/* 将Bitmap设定到ImageView */
				touchImageView.setImageBitmap(bitmap);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);

	}

}
