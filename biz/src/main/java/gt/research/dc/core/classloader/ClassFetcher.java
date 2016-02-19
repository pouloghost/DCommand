package gt.research.dc.core.classloader;

import android.content.Context;

import java.io.File;

import dalvik.system.DexClassLoader;
import gt.research.dc.util.LogUtils;

/**
 * Created by ayi.zty on 2016/2/19.
 */
public class ClassFetcher {
    private ClassLoader mClassLoader;

    private boolean mUsable;

    public ClassFetcher(Context context, File apkFile) {
        mUsable = false;
        if (null == apkFile || !apkFile.exists()) {
            return;
        }
        mClassLoader = new DexClassLoader(apkFile.getAbsolutePath(), apkFile.getParent(),
                null, context.getClassLoader());
        mUsable = true;
    }

    public <T> Class<T> getClass(String className) {
        if (null == mClassLoader) {
            return null;
        }
        try {
            return (Class<T>) mClassLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
            LogUtils.exception(this, e);
            return null;
        }
    }

    public ClassLoader getClassLoader() {
        return mClassLoader;
    }

    public boolean isUsable() {
        return mUsable;
    }
}
