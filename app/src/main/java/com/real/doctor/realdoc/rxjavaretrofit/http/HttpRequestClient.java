package com.real.doctor.realdoc.rxjavaretrofit.http;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.real.doctor.realdoc.API.AuthenticationInterceptor;
import com.real.doctor.realdoc.rxjavaretrofit.cache.BaseInterceptor;
import com.real.doctor.realdoc.rxjavaretrofit.cache.CacheInterceptor;
import com.real.doctor.realdoc.rxjavaretrofit.down.DownCallBack;
import com.real.doctor.realdoc.rxjavaretrofit.down.DownSubscriber;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.factory.ResponseConvertFactory;
import com.real.doctor.realdoc.rxjavaretrofit.impl.RetrofitService;
import com.real.doctor.realdoc.rxjavaretrofit.manager.HttpCookieManager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;
import okhttp3.ConnectionPool;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * @author zhujiabin
 * @package rxjavaretrofit.http
 * @fileName ${Name}
 * @Date 2018-1-3 0003
 * @describe TODO
 * @email zhujiabindragon@163.com
 */

public class HttpRequestClient {

    private static final String TAG = "HttpRequestClient";

    private static Context mContext = null;

    private static String mBaseUrl = HttpNetUtil.BASE_URL;

    private static Map<String, String> mHeader = new HashMap<String, String>();

    /**
     * 链接建立的超时时间
     */
    private static final int DEFAULT_TIMEOUT = 30;

    private static Retrofit retrofit = null;
    /**
     * cache
     */
    private File httpCacheDirectory = null;

    private Cache cache = null;
    /**
     * 下载用okhttp
     */
    private static OkHttpClient okHttpClient = null;

    private RetrofitService retrofitService;

    public HttpRequestClient(Context context) {
        this(context, mBaseUrl, null);
        mContext = context;
    }

    private HttpRequestClient(Context context, String url) {
        this(context, url, null);
    }

    private HttpRequestClient(Context context, String url, Map<String, String> headers) {
        if (TextUtils.isEmpty(url)) {
            url = mBaseUrl;
        }
        Log.e(TAG, url);
        if (httpCacheDirectory == null) {
            httpCacheDirectory = new File(mContext.getCacheDir(), "cache");
        }

        try {
            if (cache == null) {
                cache = new Cache(httpCacheDirectory, 10 * 1024 * 1024);
            }
        } catch (Exception e) {
            Log.e(TAG, "Could not create http cache", e);
        }

        okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(
                        new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .cookieJar(new HttpCookieManager(context))
                .cache(cache)
                .addInterceptor(new BaseInterceptor(headers))
                .addInterceptor(new CacheInterceptor(context))
                .addNetworkInterceptor(new CacheInterceptor(context))
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(8, 15, TimeUnit.SECONDS))
                // 这里你可以根据自己的机型设置同时连接的个数和时间，我这里8个，和每个保持时间为10s
                .build();

        retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(ResponseConvertFactory.create())
//                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(url)
                .build();
    }

    /**
     * 在访问HttpMethods时创建单例
     */
    private static class HttpSingletonHolder {
        private static HttpRequestClient INSTANCE = new HttpRequestClient(mContext);
    }

    private static class HttpSingletonWithHeaderHeader {
        private static HttpRequestClient INSTANCEWITHHEADER = new HttpRequestClient(mContext, mBaseUrl, mHeader);
    }

    /**
     * 获取HttpRequestClient单例
     */
    public static HttpRequestClient getInstance(Context context) {
        if (context != null) {
            mContext = context;
        }
        return HttpSingletonHolder.INSTANCE;
    }

    /**
     * 获取HttpRequestClient单例,并修改BaseUrl
     */
    public static HttpRequestClient getInstance(Context context, String url) {
        if (context != null) {
            mContext = context;
        }
        return new HttpRequestClient(context, url);
    }

    /**
     * 获取HttpRequestClient单例,添加headers
     */
    public static HttpRequestClient getInstance(Context context, String url, Map<String, String> headers) {
        if (context != null) {
            mContext = context;
            mBaseUrl = url;
            mHeader = headers;
        }
        return HttpSingletonWithHeaderHeader.INSTANCEWITHHEADER;
    }

    /**
     * create BaseApi  defalte ApiManager
     *
     * @return ApiManager
     */
    public HttpRequestClient createBaseApi() {
        retrofitService = create(RetrofitService.class);
        return this;
    }

    /**
     * create you ApiService
     * Create an implementation of the API endpoints defined by the {@code service} interface.
     */
    public <T> T create(final Class<T> service) {
        if (service == null) {
            throw new RuntimeException("Api service is null!");
        }
        return retrofit.create(service);
    }

    /**
     * Rxjava 消息发送者（被观察者）
     */
    ObservableTransformer schedulersTransformer() {
        return new ObservableTransformer() {
            @Override
            public ObservableSource apply(Observable upstream) {
                return upstream.subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    /**
     * get请求获取数据
     */
    public void get(String url, Map parameters, BaseObserver<ResponseBody> subscriber) {
        retrofitService.executeGet(url, parameters)
                .compose(schedulersTransformer())
                .subscribe(subscriber);
    }

    public void getNotifications(String url, Map parameters){

        OkHttpClient okHttpClient = getOkHttpClient();

        //retrofitService.getNotifications(url, parameters);
    }




    /**
     * post请求获取数据
     */
    public void post(String url, Map<String, String> parameters, BaseObserver<ResponseBody> subscriber) {
        retrofitService.executePost(url, parameters)
                .compose(schedulersTransformer())
                .subscribe(subscriber);
    }

    /**
     * json请求获取数据
     */
    public void json(String url, RequestBody jsonStr, BaseObserver<ResponseBody> subscriber) {
        retrofitService.json(url, jsonStr)
                .compose(schedulersTransformer())
                .subscribe(subscriber);
    }

    /**
     * 上传文件
     */
    public void upload(String url, RequestBody requestBody, BaseObserver<ResponseBody> subscriber) {
        retrofitService.upLoadFile(url, requestBody)
                .compose(schedulersTransformer())
                .subscribe(subscriber);
    }


    /**
     * 上传文件
     */
    public void uploads(String url, Map map, BaseObserver<ResponseBody> subscriber) {
        retrofitService.upLoadMap(url, map)
                .compose(schedulersTransformer())
                .subscribe(subscriber);
    }

    /**
     * 上传文件,并上传json数据
     */
    public void uploadJsonFile(String url, RequestBody jsonStr, MultipartBody.Part file, BaseObserver<ResponseBody> subscriber) {
        retrofitService.upLoadjsonFile(url, jsonStr, file)
                .compose(schedulersTransformer())
                .subscribe(subscriber);
    }

    /**
     * 下载文件
     */
    public void download(String url, final DownCallBack callBack) {
        retrofitService.downloadFile(url)
                .compose(schedulersTransformer())
                .subscribe((Observer) new DownSubscriber<ResponseBody>(callBack, mContext));
    }

    private OkHttpClient getOkHttpClient() {
        //日志显示级别
        HttpLoggingInterceptor.Level level = HttpLoggingInterceptor.Level.BODY;
        //新建log拦截器
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.d("jsonData", "OkHttp====Message:" + message);
            }
        });
        loggingInterceptor.setLevel(level);
        //定制OkHttp
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient
                .Builder();
        //OkHttp进行添加拦截器loggingInterceptor
        httpClientBuilder.addInterceptor(loggingInterceptor);
        return httpClientBuilder.build();
    }

}
