package com.real.doctor.realdoc.rxjavaretrofit.factory;

import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * @author zhujiabin
 * @package rxjavaretrofit
 * @fileName ${Name}
 * @Date 2018-1-3 0003
 * @describe TODO
 * @email zhujiabindragon@163.com
 */

class GsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final Type type;

    GsonResponseBodyConverter(Gson gson, Type type) {
        this.gson = gson;
        this.type = type;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        String response = value.string();
        Log.d("Network", "response>>" + response);
        return gson.fromJson(response, type);
    }
}
