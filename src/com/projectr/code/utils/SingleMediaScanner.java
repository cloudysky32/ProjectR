package com.projectr.code.utils;

import java.io.*;

import android.content.*;
import android.media.*;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.*;

public class SingleMediaScanner implements MediaScannerConnectionClient {

	private MediaScannerConnection mMs;
	private File mFile;

	public SingleMediaScanner(Context context, File f) {
		mFile = f;
		mMs = new MediaScannerConnection(context, this);
		mMs.connect();
	}

	@Override
	public void onMediaScannerConnected() {
		mMs.scanFile(mFile.getAbsolutePath(), null);
	}

	@Override
	public void onScanCompleted(String path, Uri uri) {
		mMs.disconnect();
	}

}
