package com.projectr.code;

import java.io.*;
import java.util.*;

import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.*;
import android.os.*;
import android.util.*;
import android.widget.*;

import com.projectr.code.http.*;
import com.projectr.code.security.*;
import com.projectr.code.vars.*;

public class LoginSplash extends Activity {
	private final String FILE_NAME = "projectr_u";
	private final String URL = "http://cloudysky.iptime.org/projectr_login.php";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginsplash);

		Handler checkHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				BufferedReader br = null;
				ArrayList<String> userData = new ArrayList<String>();		
				
				try {
					br = new BufferedReader(new InputStreamReader(openFileInput(FILE_NAME)));
					String str = null;

					while ((str = br.readLine()) != null) {
						userData.add(str);
					}
					String userId = new String(AES.decryptAES(ByteUtils.toBytesFromHexString(userData.get(0).toString())));
					GetData login = new GetData(userId, new String(AES.decryptAES(ByteUtils.toBytesFromHexString(userData.get(1).toString()))), URL);
					
					if (login.getState()) {
						if(login.getMsgs().get(0).equals(userId)) {
							GlobalVariables.getInstance().setUserId(userId);
							startActivity(new Intent(LoginSplash.this, WonderlandActivity.class));
							finish();
						} else {
							startActivity(new Intent(LoginSplash.this, LoginActivity.class));
							finish();
						}
					} else {
						// TODO http 접속 실패시 메세지 보인후, Application 종료
						Toast.makeText(LoginSplash.this, "서버와의 연결이 원활하지 않습니다", Toast.LENGTH_LONG).show();
						Handler finishHandler = new Handler() {
							@Override
							public void handleMessage(Message msg) {
								LoginSplash.this.finish();
							}
						};
						finishHandler.sendEmptyMessageDelayed(0, 3000);
					}
				} catch (FileNotFoundException fn) {
					startActivity(new Intent(LoginSplash.this, LoginActivity.class));
					finish();
				} catch (Exception e) {
					try {
						br.close();
						Log.e("log_file_error",	"Error reading data! " + e.toString());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}  
			}
		};
		
		String version = null;
		try {
		   PackageInfo i = LoginSplash.this.getPackageManager().getPackageInfo(LoginSplash.this.getPackageName(), 0);
		   version = i.versionName;
		} catch(NameNotFoundException e) { 
			Log.e("log_tag_App", e.toString());
		}
		
		GetData checkVersion = new GetData(version, "http://cloudysky.iptime.org/projectr_checkversion.php");
		
		if(checkVersion.getState()) {
			if(checkVersion.getMsgs().get(0).equals("1")) {
				checkHandler.sendEmptyMessageDelayed(0, 2000);
			} else {			
				Toast.makeText(LoginSplash.this, "새로운 버전이 있어 마켓으로 이동합니다", Toast.LENGTH_LONG).show();
				Handler updateHandler = new Handler() {
					@Override
					public void handleMessage(Message msg) {
						Uri marketUri = Uri.parse("market://details?id=" + "com.projectr.code"); 
						Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri); 
						startActivity(marketIntent); 

						finish();
					}
				};
				updateHandler.sendEmptyMessageDelayed(0, 2000);
			}
		} else {
			Toast.makeText(LoginSplash.this, "서버와의 연결이 원활하지 않습니다", Toast.LENGTH_LONG).show();
			Handler finishHandler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					finish();
				}
			};
			finishHandler.sendEmptyMessageDelayed(0, 3000);
		}
    }
}