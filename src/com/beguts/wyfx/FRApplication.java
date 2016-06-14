/**
 * @author 	Irvin Pang
 * @email	halo.irvin@gmail.com
 */
package com.beguts.wyfx;

import java.security.MessageDigest;

import org.cocos2dx.lib.Cocos2dxBaseAppInterface;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import cn.jpush.android.api.JPushInterface;
import com.facebook.FacebookSdk;

public class FRApplication extends Application implements Cocos2dxBaseAppInterface {
	static FRApplication sharedApplication;
	
	public FRApplication() {
		super();
		sharedApplication = this;
	}
	
	public static FRApplication sharedApplication() {
		if (sharedApplication == null) {
			sharedApplication = new FRApplication();
		}
		return sharedApplication;
	}
	
	@Override
	public void beforeDestroy() {
	}
	
	@Override
	public boolean onKeyDown(final int pKeyCode, final KeyEvent pKeyEvent) {
		return false;
	}
	
	@Override
    public void onCreate() {
		super.onCreate();
		
		FacebookSdk.sdkInitialize(getApplicationContext());
        gettingHashKey();

        //JPushInterface.setDebugMode(true);//发布时删掉
        JPushInterface.init(this);
	}
	
	public void gettingHashKey() {
        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.beguts.wyfx",PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        }catch (Exception e) {

        }

    }
	
}
