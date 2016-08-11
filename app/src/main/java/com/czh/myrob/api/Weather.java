package com.czh.myrob.api;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * 天气请求API
 * http://op.juhe.cn/onebox/weather/query
 * cityname=%E5%8C%97%E4%BA%AC&dtype=&key=c4211ccae4f143cedd5d1ab65721abae
 * Created by LiJianfei on 2016/8/4.
 */
public interface Weather {
//    @GET(UrlManager.URLWeather)

    /**
     *  接口中定义的每一个方法  包含了返回值、api、和传参
     * @param cityname
     * @param appkey
     * @return
     */
    @GET("/onebox/weather/query") // 拼接方式
    Call reqWeather(@Query("cityname") String cityname, @Query("key") String appkey);
}
