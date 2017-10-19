package com.yuan.leopardkit;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.yuan.leopardkit.db.HttpDbUtil;
import com.yuan.leopardkit.download.DownLoadManager;
import com.yuan.leopardkit.download.model.DownloadInfo;
import com.yuan.leopardkit.http.LeopardClient;
import com.yuan.leopardkit.http.base.BaseEnetity;
import com.yuan.leopardkit.http.base.HttpMethod;
import com.yuan.leopardkit.http.factory.RequestComFactory;
import com.yuan.leopardkit.http.factory.RequestJsonFactory;
import com.yuan.leopardkit.http.factory.UploadFileFactory;
import com.yuan.leopardkit.interfaces.FileRespondResult;
import com.yuan.leopardkit.interfaces.HttpRespondResult;
import com.yuan.leopardkit.interfaces.IProgress;
import com.yuan.leopardkit.upload.FileUploadEnetity;

import java.util.HashMap;

import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Yuan on 2016/9/1.
 * Detail 自动化构建请求对象 开发者也可以自定义
 */
public class LeopardHttp {

    private static final String TAG = "LeopardHttp";

    private static int HANDER_DELAYED_TIME = 1000;
    private static String ADDRESS = "http://127.0.0.1";
    private static int tmp_progress = 0;

    /**
     * 用之前必须先初始化主机域名
     *
     * @param address
     */
    public static void init(String address, Context context) {
        ADDRESS = address;
        HttpDbUtil.initHttpDB(context.getApplicationContext());
    }

    private static LeopardClient.Builder getBuilder(HttpRespondResult respondResult) {
        return new LeopardClient.Builder()
                .addRequestComFactory(new RequestComFactory(respondResult))
                .addGsonConverterFactory(GsonConverterFactory.create())
                .addRxJavaCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(ADDRESS);
    }

    /**
     * 提供不需要自定义头部入口
     *
     * @param type
     * @param enetity
     * @param httpRespondResult
     */
    public static void SEND(HttpMethod type, Context context, BaseEnetity enetity, HttpRespondResult httpRespondResult) {
        if (type.toString().equals(HttpMethod.GET.toString())) {
            GET(context, enetity, httpRespondResult);
        } else if (type.toString().equals(HttpMethod.GET_JSON.toString())) {
            GETjson(context, enetity, httpRespondResult);
        } else if (type.toString().equals(HttpMethod.POST.toString())) {
            POST(context, enetity, httpRespondResult);
        } else if (type.toString().equals(HttpMethod.POST_JSON.toString())) {
            POSTJson(context, enetity, httpRespondResult);
        } else {
            Log.i(TAG, "Type unknown");
        }
    }


    static final Handler handler = new Handler();

    /**
     * 下载入口
     *
     * @param downloadInfo
     * @param iProgress
     * @return 拥有Task的下载实体
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static DownloadInfo DWONLOAD(final DownloadInfo downloadInfo, final IProgress iProgress, final Context mContext) {
        tmp_progress = 0;

        DownLoadManager.getManager().addTask(downloadInfo, new FileRespondResult() {
            @Override
            public void onExecuting(final long progress, final long total, boolean done) {
//                final Message message = handler.obtainMessage();
                if (total == 0) {
                    return;
                }
                int cur_progress = (int) (((float) progress / total) * 1000);
//                message.arg1 = (int) progress;
//                message.arg2 = (int) total;


                if (total != 0 && cur_progress == tmp_progress) {
                    return;
                }

                tmp_progress = cur_progress;
                if (tmp_progress >= 1000) {
                    tmp_progress = 0;
                }


                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (downloadInfo.getState() != DownLoadManager.STATE_WAITING && downloadInfo.getState() != DownLoadManager.STATE_PAUSE) {
                            if (total == 0) {
                                iProgress.onProgress(progress, total, false);
                                return;
                            }
                            iProgress.onProgress(progress, total, progress >= total);
                        }
                    }
                });
            }
        });
        return downloadInfo;

    }

    public static void UPLOAD(FileUploadEnetity uploadEnetity, final IProgress iProgress) {
        final Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                iProgress.onProgress(msg.arg1, msg.arg2, msg.arg1 >= msg.arg2);
            }
        };

        FileRespondResult respondResult = new FileRespondResult() {
            @Override
            public void onExecuting(long progress, long total, boolean done) {
                Message message = new Message();
                message.arg1 = (int) progress;
                message.arg2 = (int) total;
                handler.sendMessageDelayed(message, HANDER_DELAYED_TIME);
            }
        };

        getBuilder(respondResult)
                .addUploadFileFactory(UploadFileFactory.create())
                .build()
                .upLoadFiles(uploadEnetity, respondResult);

    }

    /**
     * 提供需要自定义头部入口
     *
     * @param type
     * @param context
     * @param enetity
     * @param header
     * @param httpRespondResult
     */
    public static void SEND(HttpMethod type, Context context, BaseEnetity enetity, HashMap<String, String> header, HttpRespondResult httpRespondResult) {
        if (type.toString().equals(HttpMethod.GET.toString())) {
            GET(context, enetity, header, httpRespondResult);
        } else if (type.toString().equals(HttpMethod.GET_JSON.toString())) {
            GETjson(context, enetity, header, httpRespondResult);
        } else if (type.toString().equals(HttpMethod.POST.toString())) {
            POST(context, enetity, header, httpRespondResult);
        } else if (type.toString().equals(HttpMethod.POST_JSON.toString())) {
            POSTJson(context, enetity, header, httpRespondResult);
        } else {
            Log.i(TAG, "Type unknown");
        }
    }


    private static void POST(Context context, BaseEnetity enetity, HttpRespondResult httpRespondResult) {
        getBuilder(httpRespondResult)
                .build()
                .POST(context, enetity, httpRespondResult);
    }

    private static void POST(Context context, BaseEnetity enetity, HashMap<String, String> header, HttpRespondResult httpRespondResult) {
        getBuilder(httpRespondResult)
                .addHeader(header)
                .build()
                .POST(context, enetity, httpRespondResult);
    }

    private static void POSTJson(Context context, BaseEnetity enetity, HttpRespondResult httpRespondResult) {
        getBuilder(httpRespondResult)
                .addRequestJsonFactory(RequestJsonFactory.create())
                .build()
                .POST(context, enetity, httpRespondResult);
    }

    private static void POSTJson(Context context, BaseEnetity enetity, HashMap<String, String> header, HttpRespondResult httpRespondResult) {
        getBuilder(httpRespondResult)
                .addHeader(header)
                .addRequestJsonFactory(RequestJsonFactory.create())
                .build()
                .POST(context, enetity, httpRespondResult);
    }

    private static void GET(Context context, BaseEnetity enetity, HashMap<String, String> header, HttpRespondResult httpRespondResult) {
        getBuilder(httpRespondResult)
                .addHeader(header)
                .build()
                .GET(context, enetity, httpRespondResult);
    }

    private static void GET(Context context, BaseEnetity enetity, HttpRespondResult httpRespondResult) {
        getBuilder(httpRespondResult)
                .build()
                .GET(context, enetity, httpRespondResult);
    }

    private static void GETjson(Context context, BaseEnetity enetity, HttpRespondResult httpRespondResult) {
        getBuilder(httpRespondResult)
                .addRequestJsonFactory(RequestJsonFactory.create())
                .build()
                .GET(context, enetity, httpRespondResult);
    }

    private static void GETjson(Context context, BaseEnetity enetity, HashMap<String, String> header, HttpRespondResult httpRespondResult) {
        getBuilder(httpRespondResult)
                .addHeader(header)
                .addRequestJsonFactory(RequestJsonFactory.create())
                .build()
                .GET(context, enetity, httpRespondResult);
    }

}
