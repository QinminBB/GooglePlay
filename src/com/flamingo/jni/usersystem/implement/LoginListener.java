package com.flamingo.jni.usersystem.implement;

import org.json.JSONException;
import org.json.JSONObject;

import com.flamingo.jni.usersystem.UserSystemCallback;
import com.flamingo.jni.usersystem.UserSystemConfig;

public class LoginListener implements UserSystemConfig {
	static LoginListener sInstance = null;
	private USStatusCode uStatusCode = USStatusCode.kStatusFail;
	private JSONObject jsonObject = null;
	
	public static LoginListener getInstance() {
		if (sInstance == null) {
			sInstance = new LoginListener();
		}
		return sInstance;
	}

	public void onLoginResult(String userId) {
		if (userId != null) {
			jsonObject = new JSONObject();
			uStatusCode = USStatusCode.kStatusSuccess;
			try {
				jsonObject.put(KEY_LOGIN_USER_NAME, userId);
			} catch (JSONException e) {
				UserSystem.LogE(" login callback jsonexception ");
				e.printStackTrace();
			}
		}else {
			uStatusCode = USStatusCode.kStatusFail;
		}
		UserSystemCallback.getInstance().nativeCallback(USAction.kActionLogin, uStatusCode, jsonObject.toString());
	}



}
