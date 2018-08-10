package com.xuexiang.tinkertest.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * @author xuexiang
 * @since 2018/8/10 下午4:40
 */
public final class ReopenAppUtils {

    private ReopenAppUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 重启app
     *
     * @param context
     */
    public static void reopenApp(Context context) {
        Intent intent = new Intent(context, StartAppReceiver.class);
        PendingIntent restartIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        //退出程序
        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent); // 1秒钟后重启应用

        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
}
