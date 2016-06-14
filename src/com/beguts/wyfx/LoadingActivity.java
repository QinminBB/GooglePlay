/**
 * @author 	Irvin Pang
 * @email	halo.irvin@gmail.com
 */
package com.beguts.wyfx;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

import com.appsflyer.AppsFlyerLib;
import com.flamingo.jni.usersystem.implement.DataConfig;

public class LoadingActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
             
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);  
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.splash);
        
        //AppsFlyer
        AppsFlyerLib.getInstance().startTracking(getApplication(), DataConfig.APP_FLYER_KEY);
        AppsFlyerLib.getInstance().setCurrencyCode("NTD");
        
        AlphaAnimation enterAnim = new AlphaAnimation(0.0f, 1.0f);  
        enterAnim.setDuration(1500);
          
        ImageView img_logo = (ImageView)this.findViewById(R.id.loading_img);
        img_logo.setBackgroundResource(R.drawable.splash);
        img_logo.setAnimation(enterAnim);
        
        enterAnim.setAnimationListener(new AnimationListener(){  
            public void onAnimationEnd(Animation enterAnim){
            	Intent intent = new Intent(LoadingActivity.this, FanRen.class);
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }  
  
            public void onAnimationRepeat(Animation enterAnim){    
                  
            }
  
            public void onAnimationStart(Animation enterAnim){ 
                  
            }
        });
        
        
        
    }
}
