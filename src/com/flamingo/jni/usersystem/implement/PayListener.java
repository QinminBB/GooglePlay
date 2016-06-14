package com.flamingo.jni.usersystem.implement;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import com.flamingo.jni.usersystem.UserSystemCallback;
import com.flamingo.jni.usersystem.UserSystemConfig;

public class PayListener implements UserSystemConfig {
	static PayListener sInstance = null;
	
	static public PayListener getInstance() {
		if (sInstance == null) {
			sInstance = new PayListener();
		}
		return sInstance;
	}
	
	public void callBack(USStatusCode statusCode, String msg) {
		UserSystem.LogD("payListener callback invoke");
		String purchaseJson = "";
		if(statusCode == USStatusCode.kStatusSuccess){
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put("mes", URLEncoder.encode(msg));
				jsonObject.put(KEY_PURCHASE_RESULT, USPayResult.SUCCESS);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			purchaseJson =  jsonObject.toString();
		}else {
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put("mes", msg);
				jsonObject.put(KEY_PURCHASE_RESULT, USPayResult.FAILED);
				purchaseJson = jsonObject.toString();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if(purchaseJson != null) {
			this.writeToSDcardFile("google.txt", "/wyfx_beguts_res", purchaseJson+"\n\r");
		}
		UserSystemCallback.getInstance().nativeCallback(USAction.kActionPurchase, statusCode, purchaseJson);
	}
	
    public void writeToSDcardFile(String file, String destDirStr,
            String szOutText) {
        // 获取扩展SD卡设备状态
        String sDStateString = android.os.Environment.getExternalStorageState();

        File myFile = null;
        // 拥有可读可写权限
        if (sDStateString.equals(android.os.Environment.MEDIA_MOUNTED)) {
            try {
                // 获取扩展存储设备的文件目录
                File SDFile = android.os.Environment.getExternalStorageDirectory();

                File destDir = new File(SDFile.getAbsolutePath() + destDirStr);//文件目录

                if (!destDir.exists()){//判断目录是否存在，不存在创建
                    destDir.mkdir();//创建目录
                }
                // 打开文件
                myFile = new File(destDir + File.separator + file);

                // 判断文件是否存在,不存在则创建
                if (!myFile.exists()) {
                    myFile.createNewFile();//创建文件
                }
                // 写数据   注意这里，两个参数，第一个是写入的文件，第二个是指是覆盖还是追加，
                //默认是覆盖的，就是不写第二个参数，这里设置为true就是说不覆盖，是在后面追加。
                FileOutputStream outputStream = new FileOutputStream(myFile,true);
                outputStream.write(szOutText.getBytes());//写入内容
                outputStream.close();//关闭流

            } catch (Exception e) {
                // TODO: handle exception
                e.getStackTrace();
            }

        }
    }

}
