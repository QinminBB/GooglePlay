package com.flamingo.jni.usersystem.implement;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;

import com.appsflyer.AFInAppEventParameterName;
import com.appsflyer.AFInAppEventType;
import com.appsflyer.AppsFlyerLib;
import com.beguts.wyfx.utils.IabHelper;
import com.beguts.wyfx.utils.IabResult;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.flamingo.jni.usersystem.ProductInfo;
import com.flamingo.jni.usersystem.UserSystemBase;

public class UserSystem extends UserSystemBase {
	public final static String MAIL_ADD = "";
	public static boolean isInit = false;
	public final static int RC_REQUEST = 10001;
	public static ProductInfo mProductInfo = null;
	public static IabHelper mIabHelper = null;
	public static ProgressDialog progressDialog = null;
	
	public static  CallbackManager mCallbackManager;
	public static  AccessTokenTracker tokenTracker;
	public static  ProfileTracker profileTracker;
	public static  LoginButton loginButton;
	
	@Override
	public void initSDK() {
		
		//init google play
		initGooglePlayUtils(DataConfig.KEY_GOOGLEPLAY);
		
		//init FaceBookSDK
		mCallbackManager = CallbackManager.Factory.create();
		
		tokenTracker = new AccessTokenTracker() {
			@Override
			protected void onCurrentAccessTokenChanged(AccessToken arg0,AccessToken arg1) {
			}
        };
        
        profileTracker= new ProfileTracker() {
			@Override
			protected void onCurrentProfileChanged(Profile arg0,Profile arg1) {
			}
        };

        tokenTracker.startTracking();
        profileTracker.startTracking();    
	}
	
	@Override
	public void login() {
		super.login();
		Log.e("fanren"," invoke login");
		tapFBLoginButton();
	}
	
	private void tapFBLoginButton() {
		if (loginButton == null) {
			loginButton = new LoginButton(mActivity);
	        loginButton.setReadPermissions("public_profile", "email","user_friends");
	        loginButton.registerCallback(mCallbackManager, mFacebookCallback);
		}
        loginButton.performClick();
	}
	
	@Override
	public void logout() {
		
	}

	@Override
	public boolean hasUserCenter() {
		return false;
	}

	@Override
	public void openUserCenter() {

	}

	@Override
	public USPayType[] getSupportPayType() {
		return null;
	}
	
	@Override
	public void purchase(ProductInfo product, USPayType payType) {
		tracePurchase(mActivity, "Start Purchase");
		
		mProductInfo = product;
		String payload = "";
		String productId = product.itemInfo.productID;
		mIabHelper.launchPurchaseFlow(mActivity,productId,RC_REQUEST, GooglePlayPayListener.getInstance(mActivity).getOnIabPurchaseFinishedListener(),payload);
	
	}
	
	@Override
	public void extraAPI(int flag, String mes) {
		Log.e("beguts",String.valueOf(flag) +" " +mes);
		if(flag == 5000) {
			tapFBLoginButton();
			Map<String, Object> eventValue = new HashMap<String, Object>();
			eventValue.put(AFInAppEventParameterName.DESCRIPTION,"FaceBookLogin");
			eventValue.put(AFInAppEventParameterName.REGSITRATION_METHOD,"FaceBook");
			eventValue.put(AFInAppEventParameterName.SUCCESS, "1");
			AppsFlyerLib.getInstance().trackEvent(mActivity,AFInAppEventType.LOGIN,eventValue);
		}else if (flag == 5001) {
			
		}else if (flag == 5002) {
			Map<String, Object> eventValue = new HashMap<String, Object>();
			eventValue.put(AFInAppEventParameterName.DESCRIPTION,"DirectLogin");
			eventValue.put(AFInAppEventParameterName.REGSITRATION_METHOD,"Direct");
			eventValue.put(AFInAppEventParameterName.SUCCESS, "1");
			AppsFlyerLib.getInstance().trackEvent(mActivity,AFInAppEventType.LOGIN,eventValue);
		}else if (flag == 5003) {
			Map<String, Object> eventValue = new HashMap<String, Object>();
			eventValue.put(AFInAppEventParameterName.DESCRIPTION,"MobileLogin");
			eventValue.put(AFInAppEventParameterName.REGSITRATION_METHOD,"Mobile");
			eventValue.put(AFInAppEventParameterName.SUCCESS, "1");
			AppsFlyerLib.getInstance().trackEvent(mActivity,AFInAppEventType.LOGIN,eventValue);
		}
	}

	@Override
	public void update(final String address) {
		
	}
	
	public static void tracePurchase(Activity activity, String des) {
		if (mProductInfo == null) {
			return;
		}
		Map<String, Object> eventValue = new HashMap<String, Object>();
		eventValue.put(AFInAppEventParameterName.LEVEL,mProductInfo.roleInfo.roleLevel);
		eventValue.put(AFInAppEventParameterName.PRICE,mProductInfo.itemInfo.amount);
		eventValue.put(AFInAppEventParameterName.REVENUE,mProductInfo.itemInfo.amount);
		eventValue.put(AFInAppEventParameterName.CURRENCY,"NTD");
		eventValue.put(AFInAppEventParameterName.DATE_A,new Date());
		eventValue.put(AFInAppEventParameterName.DESCRIPTION,des);
		eventValue.put("productID",mProductInfo.itemInfo.productID);
		eventValue.put("roleName",mProductInfo.roleInfo.roleName);
		eventValue.put("roleLevel",mProductInfo.roleInfo.roleLevel);
		eventValue.put("orderID",mProductInfo.itemInfo.orderID);
		eventValue.put("productName",mProductInfo.itemInfo.productName);
		eventValue.put("productDescription",mProductInfo.itemInfo.description);
		eventValue.put("ServerID",mProductInfo.roleInfo.serverID);
		AppsFlyerLib.getInstance().trackEvent(activity,AFInAppEventType.PURCHASE,eventValue);
	}
	
	//GooglePlay
	private void initGooglePlayUtils(String app_key){
		try{
			System.out.println(String.format("initGooglePlayUtils isDebugMode: %s",isDebugMode));
			mIabHelper = new IabHelper(mActivity,app_key);
			mIabHelper.enableDebugLogging(isDebugMode);
			mIabHelper.startSetup(new IabHelper.OnIabSetupFinishedListener(){
				@Override
				public void onIabSetupFinished(IabResult result) {
					if(!result.isSuccess() || mIabHelper == null){
						System.out.println("mIabHelper.startSetup失败");
						return;
					}else {
						System.out.println("mIabHelper.startSetup成功");
						mIabHelper.queryInventoryAsync(GooglePlayPayListener.getInstance(mActivity).getQueryInventoryFinishedListener());						
					}
				}
			});
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			if(mIabHelper == null){
				this.mActivity.runOnUiThread(new Runnable(){
					@Override
					public void run() {
						System.out.println("尚未安装google play");
						new AlertDialog.Builder(mActivity).setTitle("Error").setMessage("Not installed play Google!").setPositiveButton("Quit", new OnClickListener(){
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								
							}		
						}).show();
					}
				});
			}					
		}
		
	}
	
	//FacebookCallback
	private FacebookCallback<LoginResult> mFacebookCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            AccessToken accessToken = loginResult.getAccessToken();
            Profile profile = Profile.getCurrentProfile();
            LoginListener.getInstance().onLoginResult(accessToken.getUserId());
        }

        @Override
        public void onCancel() {
        	Log.e("FBFB", "cancel");
        }

        @Override
        public void onError(FacebookException e) {
        	Log.e("FBFB", e.getLocalizedMessage());
        }
    };
	
}
