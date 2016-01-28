package gt.research.util;

import android.content.Context;
import android.text.TextUtils;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import gt.research.dc.core.FileConstants;
import gt.research.dc.data.ApkInfo;

/**
 * Created by ayi.zty on 2016/1/25.
 */
public class FileUtils {
    public static void deleteApk(Context context, ApkInfo info) {
        File dir = context.getDir(FileConstants.DIR_DOWNLOAD, Context.MODE_PRIVATE);
        if (!dir.exists()) {
            return;
        }
        File apk = new File(dir, info.id + FileConstants.SUFFIX_APK);
        if (!apk.exists()) {
            return;
        }
        apk.delete();
        File dex = new File(dir, info.id + FileConstants.SUFFIX_DEX);
        if (!dex.exists()) {
            return;
        }
        dex.delete();
    }

    public static boolean copy(String src, String dst) {
        if (TextUtils.isEmpty(src) || TextUtils.isEmpty(dst)) {
            return false;
        }
        File srcFile = new File(src);
        File dstFile = new File(dst);

        try {
            if (!srcFile.exists() || !(dstFile.exists() || dstFile.createNewFile())) {
                return false;
            }
        } catch (IOException e) {
            LogUtils.exception(e);
            return false;
        }
        FileInputStream srcStream = null;
        FileOutputStream dstStream = null;
        try {
            srcStream = new FileInputStream(srcFile);
            dstStream = new FileOutputStream(dstFile);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = srcStream.read(buffer)) > 0) {
                dstStream.write(buffer, 0, read);
            }
            dstStream.flush();
            dstStream.close();
            srcStream.close();
        } catch (IOException e) {
            LogUtils.exception(e);
            return false;
        } finally {
            closeStream(dstStream);
            closeStream(srcStream);
        }
        return true;
    }

    private static void closeStream(Closeable stream) {
        if (null == stream) {
            return;
        }
        try {
            stream.close();
        } catch (IOException e) {
        }
    }
}
