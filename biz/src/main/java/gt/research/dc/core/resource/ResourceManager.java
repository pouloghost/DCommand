package gt.research.dc.core.resource;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import java.io.File;

import gt.research.dc.core.common.ICache;
import gt.research.dc.core.common.manifest.Manifest;
import gt.research.dc.core.config.ApkConfigManager;
import gt.research.dc.core.db.Apk;
import gt.research.dc.core.db.ApkDao;
import gt.research.dc.core.db.DbManager;
import gt.research.dc.event.IOnNewApkListener;
import gt.research.dc.util.LogUtils;

/**
 * Created by ayi.zty on 2016/2/15.
 */
public class ResourceManager implements IOnNewApkListener {
    private DisplayMetrics mMetrics;
    private Configuration mConfiguration;

    private ResourceCache mCache;

    private volatile static ResourceManager sInstance;

    private ResourceManager(DisplayMetrics metrics, Configuration configuration) {
        LogUtils.debug(this, "new instance");
        mMetrics = metrics;
        mConfiguration = configuration;

        mCache = new ResourceCache();
    }

    public static ResourceManager getInstance(Context context) {
        if (null == sInstance && null != context) {
            synchronized (ResourceManager.class) {
                if (null == sInstance) {
                    Resources res = context.getResources();
                    sInstance = new ResourceManager(res.getDisplayMetrics(), res.getConfiguration());
                }
            }
        }
        return sInstance;
    }

    public void loadResource(final Context context, final String id, boolean ignoreCache,
                             final LoadResourceListener listener) {
        if (null == listener) {
            return;
        }
        if (!ignoreCache) {
            ResourceCache.Entry entry = mCache.getCachedResource(id);
            if (null != entry) {
                LogUtils.debug(this, "cached resource");
                listener.onResourceLoaded(entry.fetcher, entry.info);
                return;
            }
        }
        ApkConfigManager.getInstance().getApkInfoAndFileById(context, id, false,
                new ApkConfigManager.LoadApkInfoAndFileListener() {
                    @Override
                    public void onApkInfoAndFile(Apk info, File apkFile) {
                        if (null == apkFile || !apkFile.exists()) {
                            listener.onResourceLoaded(null, info);
                            return;
                        }
                        if (TextUtils.isEmpty(info.getPkgName())) {
                            LogUtils.debug(ResourceManager.this, "empty package");
                            listener.onResourceLoaded(null, info);
                            return;
                        }
                        ResourceFetcher fetcher = new ResourceFetcher(info, apkFile, mMetrics, mConfiguration);
                        if (!fetcher.isUsable()) {
                            fetcher = null;
                        }
                        mCache.onResourceLoaded(fetcher, info);
                        listener.onResourceLoaded(fetcher, info);
                    }
                });
    }

    public ICache getCache() {
        return mCache;
    }

    @Override
    public void onNewApk(Context context, Apk info, File apkFile, Manifest manifest) {
        LogUtils.debug(this, "new apk " + info.getApk());
        ApkDao apkDao = DbManager.getInstance(context).getDao(Apk.class);
        if (null == apkDao) {
            return;
        }
        apkDao.delete(info);
        info.setPkgName(manifest.getPackage());
        apkDao.insert(info);
        LogUtils.debug(this, "update db " + info.getApk() + " : " + info.getPkgName());
    }

    public interface LoadResourceListener {
        void onResourceLoaded(ResourceFetcher fetcher, Apk info);
    }
}
