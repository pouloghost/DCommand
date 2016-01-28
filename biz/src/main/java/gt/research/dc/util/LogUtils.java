package gt.research.dc.util;

import android.util.Log;

/**
 * Created by ayi.zty on 2016/1/26.
 */
public class LogUtils {
    public static void debug(String msg) {
        Log.e("GT", msg);
    }

    public static void exception(Throwable e) {
        Log.e("GT", "", e);
    }

    private static String getCurrentClassSimpleName() {
        String fullName = new Exception().getStackTrace()[0].getClassName();
        return fullName.substring(fullName.lastIndexOf("."));
    }
}
