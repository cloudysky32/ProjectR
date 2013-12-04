package com.projectr.code;

import java.io.*;
import java.util.*;

import android.app.*;
import android.content.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;

import com.projectr.code.http.*;
import com.projectr.code.security.*;
import com.projectr.code.utils.*;
import com.projectr.code.vars.*;

public class LoginActivity extends Activity implements OnClickListener {
	
	private final String FILE_NAME = "projectr_u";
	private final String URL = "http://cloudysky.iptime.org/projectr_login.php";
	
	private Button loginButton;
	private TextView signupButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		loginButton = (Button) findViewById(R.id.loginButton);
		signupButton = (TextView) findViewById(R.id.signupButton);
		
		loginButton.setOnClickListener(this);
		signupButton.setOnClickListener(this);
	}
	
    @Override
    public void onClick(View v) {
    	if(v == loginButton) {
    		new LoginTask().execute();			
    	} else if(v == signupButton){
			startActivity(new Intent(this, SignUpActivity.class));
    	}
    }
    
    // loginButton Task
	private class LoginTask extends AsyncTask<Void, Void, Void> {
		
		String msg = null;
		
		ArrayList<String> userData = new ArrayList<String>();

		EditText loginId = (EditText) findViewById(R.id.loginId);
		EditText loginPw = (EditText) findViewById(R.id.loginPw);
		
		GetData login;
		
		@Override
		protected void onPreExecute(){
			
			LoadingDialog.getInstance().showLoading(LoginActivity.this);
			super.onPreExecute();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			
			login = new GetData(loginId.getText().toString(), SHA1.password(loginPw.getText().toString()), URL);
			return null;
		}
		
		@Override
		protected void onPostExecute(Void unused){

			msg = login.getMsgs().get(0);
			
			OutputStreamWriter osw = null;
			
			if(login.getState()){
				if(msg.equals(loginId.getText().toString())){
					try {
						osw = new OutputStreamWriter(openFileOutput(FILE_NAME, MODE_PRIVATE));
						
						GlobalVariables.getInstance().setUserId(loginId.getText().toString());
						userData.add(ByteUtils.toHexString(AES.encryptAES(loginId.getText().toString())));
						userData.add(ByteUtils.toHexString(AES.encryptAES(SHA1.password(loginPw.getText().toString()))));
	
						for (int i = 0; i < userData.size(); i++) {
							osw.write(userData.get(i) + "\n");
						}
						userData.clear();
					} catch (Exception e) {
						Log.e("log_file_error",	"Error writing data! " + e.toString());
					} finally {
						try {
							osw.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					// TODO 로그인 후 메뉴 페이지로 이동
					startActivity(new Intent(LoginActivity.this, WonderlandActivity.class));
					finish();
				} else {
					// TODO 로그인 실패시
					Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_LONG).show();
				}
			} else {
				Toast.makeText(LoginActivity.this, "서버와의 연결이 원활하지 않습니다", Toast.LENGTH_SHORT).show();
			}
			
			LoadingDialog.getInstance().hideLoading();
		}
	}
}
