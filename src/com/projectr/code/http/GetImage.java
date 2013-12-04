package com.projectr.code.http;

import java.io.*;
import java.net.*;

import android.content.*;
import android.os.*;
import android.util.*;

import com.projectr.code.utils.*;

public class GetImage {
	public static boolean downloadImage(String fileImageName, String imageUrl, Context context) {
		
		File storagePath = new File(Environment.getExternalStorageDirectory() + "/com.projectr.mokoz");
		storagePath.mkdirs();
		File file = new File(storagePath, fileImageName);

		new SingleMediaScanner(context, file);
		if(!file.exists()) {
			URL url;
			try {
				url = new URL(imageUrl);
				HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setRequestMethod("GET");
				urlConnection.setDoOutput(true);
				urlConnection.connect();
				
				FileOutputStream fileOutput = new FileOutputStream(file);
				InputStream inputStream = urlConnection.getInputStream();
				int downloadedSize = 0;

				byte[] buffer = new byte[1024];
				int bufferLength = 0;
				while ((bufferLength = inputStream.read(buffer)) > 0) {
					fileOutput.write(buffer, 0, bufferLength);
					downloadedSize += bufferLength;
				}
				// close the output stream when done
				fileOutput.close();

			} catch (MalformedURLException e) {
				Log.e("log_tag_URL", e.toString());
				if(file.exists()) { file.delete(); }
				return false;
			} catch (IOException e) {
				Log.e("log_tag_URL", e.toString());
				if(file.exists()) { file.delete(); }
				return false;
			}
		}
		new SingleMediaScanner(context, file);
		return true;
	}
}
