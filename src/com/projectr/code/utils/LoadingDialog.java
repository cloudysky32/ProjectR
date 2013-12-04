package com.projectr.code.utils;

import android.app.*;
import android.content.*;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;

import com.projectr.code.*;

//Singleton design pattern
public class LoadingDialog {

	private static Dialog loadingDialog = null;

	public void showLoading(Context context) {
		if(context == null) {
			// do nothing
		} else {
			if (loadingDialog == null) {
				loadingDialog = new Dialog(context, R.style.LoadingDialog);
				ProgressBar pb = new ProgressBar(context);
				LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				loadingDialog.addContentView(pb, params);
				loadingDialog.setCancelable(false);
			}
	
			loadingDialog.show();
		}
	}

	public void hideLoading() {
		if (loadingDialog != null) {
			loadingDialog.dismiss();
			loadingDialog = null;
		}
	}
	
	private static LoadingDialog instance = null;

	public static synchronized LoadingDialog getInstance() {
		if (null == instance) {
			instance = new LoadingDialog();
		}
		return instance;
	}
}