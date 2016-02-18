package gt.research.dc.util;

import android.content.Context;
import android.text.TextUtils;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import gt.research.dc.core.constant.FileConstants;

/**
 * Created by ayi.zty on 2016/1/25.
 */
public class FileUtils {
    private static File sCacheDir;

    public static void deleteApk(Context context, String id) {
        File dir = context.getDir(FileConstants.DIR_DOWNLOAD, Context.MODE_PRIVATE);
        if (!dir.exists()) {
            return;
        }
        File apk = new File(dir, id + FileConstants.SUFFIX_APK);
        if (!apk.exists()) {
            return;
        }
        apk.delete();
        File dex = new File(dir, id + FileConstants.SUFFIX_DEX);
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

        if (dstFile.exists()) {
            dstFile.delete();
        }
        try {
            if (!srcFile.exists() || !dstFile.createNewFile()) {
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
        srcFile.delete();
        return true;
    }

    public static String fileToId(File apkFile) {
        String name = apkFile.getName();
        return name.substring(0, name.indexOf(FileConstants.SUFFIX_APK));
    }

    public static File getCacheDir(Context context) {
        if (null == sCacheDir) {
            sCacheDir = context.getDir(FileConstants.DIR_DOWNLOAD, Context.MODE_PRIVATE);
        }
        return sCacheDir;
    }

    public static File getCacheApkFile(Context context, ApkInfo info) {
        return getCacheApkFile(context, info.id);
    }

    public static File getCacheApkFile(Context context, String id) {
        File cacheDir = getCacheDir(context);
        return new File(cacheDir, id + FileConstants.SUFFIX_APK);
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
