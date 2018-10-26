package com.xuexiang.tinkertest;

import android.app.Application;
import android.util.Log;

import com.tencent.tinker.lib.service.PatchResult;
import com.tencent.tinker.loader.app.ApplicationLike;
import com.tinkerpatch.sdk.TinkerPatch;
import com.tinkerpatch.sdk.loader.TinkerPatchApplicationLike;
import com.tinkerpatch.sdk.server.callback.ConfigRequestCallback;
import com.tinkerpatch.sdk.tinker.callback.ResultCallBack;
import com.xuexiang.xaop.XAOP;
import com.xuexiang.xutil.XUtil;
import com.xuexiang.xutil.net.JsonUtil;
import com.xuexiang.xutil.tip.ToastUtils;

import java.util.HashMap;

/**
 * @author xuexiang
 * @since 2018/8/10 下午3:59
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        XUtil.init(this);
        XAOP.init(this);

        // 我们可以从这里获得Tinker加载过程的信息
        ApplicationLike tinkerApplicationLike = TinkerPatchApplicationLike.getTinkerPatchApplicationLike();

        // 初始化TinkerPatch SDK, 更多配置可参照API章节中的,初始化SDK
        TinkerPatch.init(tinkerApplicationLike)
                .reflectPatchLibrary()
                //向后台获取是否有补丁包更新,默认的访问间隔为3个小时，若参数为true,即每次调用都会真正的访问后台配置
                //你也可以在用户登录或者APP启动等一些关键路径，使用fetchPatchUpdate(true)强制检查更新
                .fetchPatchUpdate(false)
                //设置访问后台补丁包更新配置的时间间隔,默认为3个小时
                .setFetchPatchIntervalByHours(3)
                //向后台获得动态配置,默认的访问间隔为3个小时
                //若参数为true,即每次调用都会真正的访问后台配置
                .fetchDynamicConfig(new ConfigRequestCallback() {
                    @Override public void onSuccess(HashMap<String, String> hashMap) {
                        Log.e("xuexiang", "参数:" + JsonUtil.toJson(hashMap));

                    }
                    @Override public void onFail(Exception e) { }
                }, true)
                //设置访问后台动态配置的时间间隔,默认为3个小时
                .setFetchDynamicConfigIntervalByHours(3)
                //设置收到后台回退要求时,锁屏清除补丁,默认是等主进程重启时自动清除
                .setPatchRollbackOnScreenOff(true)
                //设置补丁合成成功后,锁屏重启程序,默认是等应用自然重启
                .setPatchRestartOnSrceenOff(true)
                .setPatchResultCallback(new ResultCallBack() {
                    @Override
                    public void onPatchResult(PatchResult patchResult) {
                        ToastUtils.toast("补丁修复:" + (patchResult.isSuccess ? "成功" : "失败"));
                    }
                });

        // 每隔3个小时(通过setFetchPatchIntervalByHours设置)去访问后台时候有更新,通过handler实现轮训的效果
        TinkerPatch.with().fetchPatchUpdateAndPollWithInterval();

    }

}
