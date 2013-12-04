package com.projectr.code;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.*;
import android.widget.ImageView.ScaleType;

public class ClueImageActivity extends Activity {
	private final int imgWidth = 400;
	private final int imgHeight = 400;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clueimage);
		
		Intent i = getIntent();
		Bundle extras = i.getExtras();
		String imgPath = extras.getString("filename");
		
		BitmapFactory.Options bfo = new BitmapFactory.Options();
		bfo.inSampleSize = 2;
		ImageView iv = (ImageView)findViewById(R.id.imageView);
		Bitmap bm = BitmapFactory.decodeFile(imgPath, bfo);
		Bitmap resized = Bitmap.createScaledBitmap(bm, imgWidth, imgHeight, true);
		iv.setImageBitmap(resized);
	}
}