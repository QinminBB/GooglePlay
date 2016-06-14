package com.flamingo.jni.usersystem.implement;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.text.TextUtils;
import android.widget.Toast;

import com.beguts.wyfx.utils.IabHelper;
import com.beguts.wyfx.utils.IabResult;
import com.beguts.wyfx.utils.Inventory;
import com.beguts.wyfx.utils.Purchase;
import com.beguts.wyfx.utils.SkuDetails;
import com.beguts.wyfx.utils.IabHelper.OnConsumeFinishedListener;
import com.beguts.wyfx.utils.IabHelper.OnIabPurchaseFinishedListener;
import com.beguts.wyfx.utils.IabHelper.QueryInventoryFinishedListener;
import com.flamingo.jni.usersystem.ProductInfo;
import com.flamingo.jni.usersystem.UserSystemConfig.USStatusCode;

public class GooglePlayPayListener{
	
	private static IabHelper mIabHelper = null;
	private static OnConsumeFinishedListener onConsumeFinishedListener = null;
	private static QueryInventoryFinishedListener queryInventoryFinishedListener = null;
	private static OnIabPurchaseFinishedListener onIabPurchaseFinishedListener = null;
	private static GooglePlayPayListener sInstance = null;
	private static Activity mActivity = null;
	private static SkuDetails mSkuDetails = null;
	
	static public GooglePlayPayListener getInstance(Activity activity) {
		if (sInstance == null) {
			sInstance = new GooglePlayPayListener();
			mIabHelper = UserSystem.mIabHelper;
			mActivity = activity;
		}
		return sInstance;
	}
	
	
	public OnIabPurchaseFinishedListener getOnIabPurchaseFinishedListener() {
		onIabPurchaseFinishedListener = null;
		onIabPurchaseFinishedListener = new OnIabPurchaseFinishedListener() {
			
			@Override
			public void onIabPurchaseFinished(IabResult result, Purchase info) {
				if(result.isSuccess()) {
					UserSystem.LogD("purchase info: " + info);
//					mIabHelper.consumeAsync(info, getConsumeFinishedListener());
					
					mIabHelper.queryInventoryAsync(getOnlyQueryListener(info));
					UserSystem.LogD("google play buy success");
					
				}else {
					UserSystem.LogE("google play buy fail " + result.getMessage());
					PayListener.getInstance().callBack(USStatusCode.kStatusFail, result.getMessage());
				}
			}
		};
		return onIabPurchaseFinishedListener;
	}
	
	public QueryInventoryFinishedListener getOnlyQueryListener(final Purchase info) {
		queryInventoryFinishedListener = null;
		queryInventoryFinishedListener = new QueryInventoryFinishedListener() {
			
			@Override
			public void onQueryInventoryFinished(IabResult result, Inventory inv) {
				UserSystem.LogD("query begin");
				if(mIabHelper == null)
					return;
				
				if (result.isFailure()) {
					UserSystem.LogE("google query buy fail " + result.getMessage());
					PayListener.getInstance().callBack(USStatusCode.kStatusFail, result.getMessage());
					UserSystem.tracePurchase(mActivity, "Google Query Product Fail");
		            return;
		        }else {
		        	SkuDetails item = inv.getSkuDetails(info.getSku());
		        	mSkuDetails = item;
					 Purchase purchase = inv.getPurchase(info.getSku());
					 if(item !=null && purchase != null) {
						 String data =purchase.getOriginalJson();							 
						 String signature = TextUtils.isEmpty(purchase.getSignature()) ?"test":purchase.getSignature();
						 signature = TextUtils.isEmpty(signature) ? "test" : signature;
						 if(item != null){
							 mIabHelper.consumeAsync(info, getConsumeFinishedListener());
							 payResultReport();
						 }
					 }
		        }
		    }
		};
		return queryInventoryFinishedListener;
	}
	

	public OnConsumeFinishedListener getConsumeFinishedListener(){
		onConsumeFinishedListener = null;
		onConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
			
			@Override
			public void onConsumeFinished(Purchase purchase, IabResult result) {
				String json = purchase.getOriginalJson();
				UserSystem.LogD("originalJson: " + json);
				UserSystem.LogD("purchase signature: " + purchase.getSignature());
				JSONObject mesJsonObject = null;
				try {
					mesJsonObject = UserSystem.getDefaultPayJson();
					//不能将这个String 的json 变成json格式传进去，否则到时候在lua解出来后字段排序不一样导致验证失败
					mesJsonObject.put("google_purchase_json", json);
					mesJsonObject.put("google_purchase_signature", purchase.getSignature());
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				if(result.isSuccess()){
					PayListener.getInstance().callBack(USStatusCode.kStatusSuccess, mesJsonObject.toString());
					UserSystem.LogD("o.toString: " + mesJsonObject.toString());
					UserSystem.LogD("purchase signature: " + purchase.getSignature());
					
					UserSystem.tracePurchase(mActivity, "Google Purchase Success and Consume Product Success");
				}else{
					PayListener.getInstance().callBack(USStatusCode.kStatusFail, result.getMessage());
					UserSystem.LogE("onConsume fail");
					
					UserSystem.tracePurchase(mActivity, "Google Consume Product Fail");
				}
			}
		};
		return onConsumeFinishedListener;
	}
	
	public QueryInventoryFinishedListener getQueryInventoryFinishedListener() {
		queryInventoryFinishedListener = null;
		queryInventoryFinishedListener = new QueryInventoryFinishedListener() {
			
			@Override
			public void onQueryInventoryFinished(IabResult result, Inventory inv) {
				UserSystem.LogD("query begin");
				if(mIabHelper == null)
					return;
				
				if (result.isFailure()) {
		            Toast.makeText(mActivity, "Failed to query inventory: " + result, Toast.LENGTH_LONG).show();
		            return;
		        }
				ArrayList<String> skuOwnedList = (ArrayList<String>)inv.getAllOwnedSkus(IabHelper.ITEM_TYPE_INAPP);
				for(int i=0;i<skuOwnedList.size();++i)
				{
					UserSystem.LogD("SKU " + skuOwnedList.get(i));
					mIabHelper.consumeAsync(inv.getPurchase(skuOwnedList.get(i)), getConsumeFinishedListener());
				}
			}
		};
		return queryInventoryFinishedListener;
	}
	

	private void payResultReport(){
		//ProductInfo productInfo = UserSystem.mProductInfo;
		//party支付统计
		//adwords支付统计  不用sdk也可以
//		AdWordsConversionReporter.reportWithConversionId(mActivity, DataConfig.KEY_ADWORDS_PAY_CONVERSION_ID, "", "", true);
		UserSystem.tracePurchase(mActivity, "Google Buy Product Success");
	}
	
	public static SkuDetails getSkuDetails() {
		return mSkuDetails;
	}

}
