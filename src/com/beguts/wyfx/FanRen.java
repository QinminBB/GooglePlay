/**
 * @author 	Irvin Pang
 * @email	halo.irvin@gmail.com
 */
package com.beguts.wyfx;

import org.cocos2dx.lib.Cocos2dxActivity;
import org.cocos2dx.lib.Cocos2dxHelper;
import org.cocos2dx.lib.Cocos2dxNotificationCenter;

import android.content.Intent;
import android.os.Bundle;

import com.beguts.wyfx.utils.IabHelper;
import com.flamingo.FRBaseActivity;
import com.flamingo.jni.loader.NativeLoader;
import com.flamingo.jni.usersystem.UserSystemManager;
import com.flamingo.jni.usersystem.implement.UserSystem;
import com.flamingo.utils.Constants;

public class FanRen extends FRBaseActivity implements Constants {	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.onPauseVisible = true;
		
		NativeLoader.load(Cocos2dxActivity.getContext(), "crash_reporter");
		NativeLoader.load(Cocos2dxActivity.getContext(), "fanren");
		
		try {
			UserSystemManager.sharedInstance().load(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Cocos2dxHelper.setAppDelegate(FRApplication.sharedApplication());
				
		/* NativeLoader完成后,通知各Activity进行native的初始化 */
		Cocos2dxNotificationCenter.getInstance().dispatchEvent(KEY_NATIVE_LOADER_FINISH);
	}
	
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IabHelper helper = UserSystem.mIabHelper;
    	if(helper == null)return;
    	
    	if(!helper.handleActivityResult(requestCode, resultCode, data))
    	{
    		UserSystem.LogD("super onActivityResult");
    		super.onActivityResult(requestCode, resultCode, data);
    		if (UserSystem.mCallbackManager != null) {
            	UserSystem.mCallbackManager.onActivityResult(requestCode, resultCode, data);
    		}
    		
    	}
    }
	
	  @Override
	  public void onStop() {
	        super.onStop();
	        if (UserSystem.profileTracker != null && UserSystem.tokenTracker != null) {
	        	 UserSystem.profileTracker.stopTracking();
	 	         UserSystem.tokenTracker.stopTracking();
			}
	       
	  }
	
}
