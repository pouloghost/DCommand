package gt.research.dc.util;

import android.util.Log;

/**
 * Created by ayi.zty on 2016/1/26.
 */
public class LogUtils {
    public static void debug(Object me, String msg) {
        if (null == me) {
            Log.e("GT", msg);
            return;
        }
        String suffix = me.getClass().getSimpleName();
        if (me instanceof String) {
            suffix = (String) me;
        }
        Log.e("GT-" + suffix, msg);
    }

    public static void exception(Object me, Throwable e) {
        if (null == me) {
            Log.e("GT", "", e);
        }
        String suffix = me.getClass().getSimpleName();
        if (me instanceof String) {
            suffix = (String) me;
        }
        Log.e("GT-" + suffix, "", e);
    }
}
