package gt.research.dc.core.classloader;

import android.content.Context;

import java.io.File;

import gt.research.dc.core.common.ICache;
import gt.research.dc.core.config.ApkConfigManager;
import gt.research.dc.core.db.Apk;
import gt.research.dc.util.LogUtils;

/**
 * Created by ayi.zty on 2016/2/19.
 */
public class ClassManager {
    private ClassLoaderCache mCache;

    private volatile static ClassManager sInstance;

    private ClassManager() {
        LogUtils.debug(this, "new instance");
        mCache = new ClassLoaderCache();
    }

    public static ClassManager getInstance() {
        if (null == sInstance) {
            synchronized (ClassManager.class) {
                if (null == sInstance) {
                    sInstance = new ClassManager();
                }
            }
        }
        return sInstance;
    }

    public void loadClass(final Context context, final String id, boolean ignoreCache,
                          final LoadClassListener listener) {
        if (null == listener) {
            return;
        }
        if (!ignoreCache) {
            ClassLoaderCache.Entry entry = mCache.getCachedClassLoader(id);
            if (null != entry) {
                LogUtils.debug(this, "cached resource");
                listener.onClassLoaded(entry.fetcher, entry.info);
                return;
            }
        }
        ApkConfigManager.getInstance().getApkInfoAndFileById(context, id, false,
                new ApkConfigManager.LoadApkInfoAndFileListener() {
                    @Override
                    public void onApkInfoAndFile(Apk info, File apkFile) {
                        if (!apkFile.exists()) {
                            listener.onClassLoaded(null, info);
                            return;
                        }
                        ClassFetcher fetcher = new ClassFetcher(context, apkFile);
                        if (!fetcher.isUsable()) {
                            LogUtils.debug(ClassManager.this, "no fetcher available");
                            fetcher = null;
                        }

                        mCache.onClassLoaded(fetcher, info);
                        listener.onClassLoaded(fetcher, info);
                    }
                });
    }

    public ICache getCache() {
        return mCache;
    }

    public interface LoadClassListener {
        void onClassLoaded(ClassFetcher fetcher, Apk info);
    }
}
