package com.kii.sample.kiigeocoupon;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.kii.cloud.storage.KiiUser;
import com.kii.tool.login.KiiLoginFragment;

public class GeoLoginFragment extends KiiLoginFragment{
	private static final String TAG = "Login";
	private EditText emailField;
	private EditText passwordField;
	private Button switchButton;
	private Button loginButton;
	private Button signupButton;
	private View rootView;
	private LinearLayout _loginArea,_signupArea;

	public static GeoLoginFragment newInstance() {
		GeoLoginFragment fragment = new GeoLoginFragment();
        return fragment;
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		// link our variables to UI elements
		rootView = inflater.inflate(R.layout.login_fragment, container, false);
		switchButton = (Button) rootView.findViewById(R.id.switchButton);
		loginButton = (Button) rootView.findViewById(R.id.loginButton);
		signupButton = (Button) rootView.findViewById(R.id.signupButton);
		emailField = (EditText) rootView.findViewById(R.id.fullNameField);
		passwordField = (EditText) rootView.findViewById(R.id.pwdField);
		_loginArea=(LinearLayout)rootView.findViewById(R.id.login_area);
		_signupArea=(LinearLayout)rootView.findViewById(R.id.signup_area);
		
		switchButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				handleSwitchSignup(rootView);
			}

		});
		SharedPreferences settings = getActivity().getSharedPreferences(GeoSampleAndroidApp.PREFS_NAME, 0);
		emailField.setText(settings.getString(GeoSampleAndroidApp.USER_EMAIL, ""));
		passwordField.setText(settings.getString(GeoSampleAndroidApp.USER_PASSWORD, ""));
		getDialog().setTitle(R.string.login);
		getDialog().setFeatureDrawableResource(STYLE_NORMAL, R.drawable.kiilogo);
		getDialog().getWindow().setWindowAnimations(R.style.MyAnimation_Window);
		return rootView;
	}
	 @Override
    protected String getIdentifier() {
        return emailField.getText().toString();
    }

    @Override
    protected String getPassword() {
        return passwordField.getText().toString();
    }

    @Override
    protected View getLoginButton() {
        return loginButton;
    }
    
    @Override
    protected View getRegisterButton() {
        return signupButton;
    }

	public void handleSwitchSignup(View v) {

		_loginArea.setVisibility(View.GONE);
		_signupArea.setVisibility(View.VISIBLE);
		getDialog().setTitle(R.string.signup);
	}

    
    // This method will be called when login is succeeded.
    @Override
    protected void onLoginSucceeded(KiiUser user) {
        super.onLoginSucceeded(user);
        Toast.makeText(getActivity(), "Login succeeded", Toast.LENGTH_LONG).show();
        savePreferences();
		Log.e(TAG,"KiiUser="+KiiUser.getCurrentUser());
		Log.e(TAG,"login="+KiiUser.isLoggedIn());
		KiiData.setUser(user);
//		try{
//		KiiData.getUser().bucket(GeoSampleAndroidApp.USER_BUCKET).delete();
//		}catch(Exception e){
//			e.printStackTrace();
//		}
        dismiss();
    }

    private void savePreferences() {
		SharedPreferences settings = getActivity().getSharedPreferences(GeoSampleAndroidApp.PREFS_NAME, 0);
		Editor edit=settings.edit();
		edit.putString(GeoSampleAndroidApp.USER_EMAIL, emailField.getText().toString());
		edit.putString(GeoSampleAndroidApp.USER_PASSWORD, passwordField.getText().toString());
		edit.commit();
	}

	// This method will be called when login is failed
    @Override
    protected void onLoginError(int errorCode) {
        super.onLoginError(errorCode);
        Toast.makeText(getActivity(), "Login failed code=" + errorCode, Toast.LENGTH_LONG).show();
    }
    
	@Override
	protected void onPrepareRegistration(KiiUser user) {
        
    }
     
	@Override
    protected void onRegisterSucceeded(KiiUser user) {
        super.onRegisterSucceeded(user);
		Toast.makeText(getActivity(), "Signup succeeded", Toast.LENGTH_LONG)
		.show();
        savePreferences();
		KiiData.setUser(user);
		dismiss();
    }
	
	@Override
    protected void onRegisterError(int errorCode) {
		super.onLoginError(errorCode);
		Toast.makeText(getActivity(), "Signup failed code=" + errorCode,
				Toast.LENGTH_LONG).show();
   }

}
