package com.game.helper.sdk;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * @Description
 * @Path
 * @Author
 * @Date 2016年8月23日 下午4:33:33
 * @Company
 */
public class Config {
    public static Config ins;
    /**
     * 0  正式环境
     * 1  测试环境
     * 2  开发环境
     */
    public static int Env = 0;

    public static Config getInstance() {
        if (ins == null) {
            ins = new Config();
        }
        return ins;
    }

    public String BASE_URL = "";
    //	public  String IP="http://ghelper.h5h5h5.cn";
    public String IP = "http://60.205.204.218:8000"/*"http://192.168.1.98:8000"*/;

    public void setHost(Context ct, String url) {
//        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ct);
//        String host = sp.getString("HOST", url);
//        if (!TextUtils.isEmpty(url)) {
//            sp.edit().putString("HOST", url).commit();
//        } else {
//            if (TextUtils.isEmpty(host)) {
//                host = IP;
//                sp.edit().putString("HOST", IP).commit();
//            }
//            url = host;
//        }
////		Toast.makeText(ct, url, 0).show();
//        IP = url;
//        BASE_URL = IP /*+ "/helper/srTrustee/apidev.do"*/;
//        Toast.makeText(ct, "setHost------->" + BASE_URL, Toast.LENGTH_LONG).show();
    }

    public Config() {
        switch (Env) {
            //正式环境
            case 0:
			IP="http://ghelper.h5h5h5.cn";
                BASE_URL = IP+"/helper/srTrustee/apidev.do";
//                BASE_URL = IP/*+"/helper/srTrustee/apidev.do"*/;

                break;
            //测试环境
            case 1:
//			IP="http://101.200.133.125:53010";
                //IP="http://192.168.1.110:8080";
                BASE_URL = IP/*+"/helper/srTrustee/apidev.do"*/;
                break;
            //开发环境
            case 2:

                break;
        }

    }

    public static String APP_STYPE = "android";


    /**
     * 上传log的最大行数
     */
    public static int UP_LOG_MAX_LINE = 200;

    /**
     * 日志打印的开启和关闭，true:表示开启，flase:表示关闭日志
     */
    public boolean DEBUG_FLAG = true;
}
