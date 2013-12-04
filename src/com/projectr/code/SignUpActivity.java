package com.projectr.code;

import java.util.*;

import android.app.*;
import android.os.*;
import android.view.*;
import android.view.WindowManager.LayoutParams;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.projectr.code.http.*;
import com.projectr.code.security.*;
import com.projectr.code.utils.*;

public class SignUpActivity extends Activity {

	private final String URL = "http://cloudysky.iptime.org/projectr_signup.php";
	private final String univURL = "http://cloudysky.iptime.org/projectr_getuniv.php";
	private final String majorURL = "http://cloudysky.iptime.org/projectr_getmajor.php";
	private String userGender = null;
	private String userUniv;
	private String userMajor;
	
    private ArrayList<String> univList = new ArrayList<String>();
    private ArrayList<String> majorList = new ArrayList<String>();
    
    private Spinner univSpinner;
    private Spinner majorSpinner;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
      
		getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

		RadioGroup genderRadio = (RadioGroup) findViewById(R.id.gender);
        Button sendButton = (Button) findViewById(R.id.sendButton);		
        univSpinner = (Spinner) findViewById(R.id.spinnerUniv);
        majorSpinner = (Spinner) findViewById(R.id.spinnerMajor);
        
        univList.add("학교선택");
        majorList.add("학과선택");
        	
		univSpinner.setAdapter(new ArrayAdapter<String>(SignUpActivity.this, R.layout.layout_spinnerlist, univList));
		majorSpinner.setAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.layout_spinnerlist, majorList));
		
        new GetUnivTask().execute();
     
		univSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				new ItemSelectedTask().execute(arg0);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub				
			}
		});
		
		genderRadio.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				RadioButton genderButton = (RadioButton) findViewById(checkedId);
				userGender = genderButton.getHint().toString();
			}
		});
		
        sendButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new ClickTask().execute();
			}
		});

    }

    // getUniv
	private class GetUnivTask extends AsyncTask<Void, Void, Void> {
		
		GetData getUniv;
		
		@Override
		protected void onPreExecute(){
			LoadingDialog.getInstance().showLoading(SignUpActivity.this);
			super.onPreExecute();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
 
	        getUniv = new GetData(univURL);
			return null;
		}
		
		@Override
		protected void onPostExecute(Void unused){

	        univList.addAll(1, getUniv.getMsgs());
			univSpinner.setAdapter(new ArrayAdapter<String>(SignUpActivity.this, R.layout.layout_spinnerlist, univList));
			
			LoadingDialog.getInstance().hideLoading();
		}
	}
    
    
    // univSpinner.setOnItemSelectedListener
	private class ItemSelectedTask extends AsyncTask<AdapterView<?>, Void, Void> {
		
		GetData getMajor;
		
		@Override
		protected void onPreExecute(){
			
			LoadingDialog.getInstance().showLoading(SignUpActivity.this);
			super.onPreExecute();
		}
		
		@Override
		protected Void doInBackground(AdapterView<?>... arg) {
       
	        getMajor = new GetData(arg[0].getSelectedItem().toString(), majorURL);
			return null;
		}
		
		@Override
		protected void onPostExecute(Void unused){
			
			if(getMajor.getState()) {
		        majorList.addAll(1, getMajor.getMsgs());
				majorSpinner.setAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.layout_spinnerlist, majorList));
			} else {
				Toast.makeText(SignUpActivity.this, "서버와의 연결이 원활하지 않습니다", Toast.LENGTH_SHORT).show();
			}
			LoadingDialog.getInstance().hideLoading();
		}
	}

	// sendButton.setOnClickListener
	private class ClickTask extends AsyncTask<Void, Void, Void> {
		
        EditText signId = (EditText) findViewById(R.id.signId);
        EditText signPw = (EditText) findViewById(R.id.signPw);
        GetData signUp;
        
		@Override
		protected void onPreExecute(){
	        
			userUniv = univSpinner.getSelectedItem().toString();
			userMajor = majorSpinner.getSelectedItem().toString();
			
			LoadingDialog.getInstance().showLoading(SignUpActivity.this);
			super.onPreExecute();
		}
		
		@Override
		protected Void doInBackground(Void... params) {

	        signUp = new GetData(signId.getText().toString(), SHA1.password(signPw.getText().toString()), userUniv, userMajor, userGender, URL);
	
			return null;
		}
		
		@Override
		protected void onPostExecute(Void unused){

	        if(signUp.getMsgs().get(0).equals("success")) {
				Toast.makeText(getApplicationContext(), "성공적으로 가입되었습니다", Toast.LENGTH_LONG).show();
				finish();
	        } else {
	        	Toast.makeText(getApplicationContext(), signUp.getMsgs().get(0), Toast.LENGTH_LONG).show();
	        }
			
			LoadingDialog.getInstance().hideLoading();
		}
	}

}
