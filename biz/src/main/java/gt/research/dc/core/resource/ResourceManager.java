package gt.research.dc.core.resource;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import java.io.File;

import gt.research.dc.core.command.verifier.IApkVerifier;
import gt.research.dc.core.command.verifier.original.OriginalVerifier;
import gt.research.dc.core.common.ICache;
import gt.research.dc.core.config.ApkConfigManager;
import gt.research.dc.data.ApkInfo;
import gt.research.dc.util.LogUtils;
import gt.research.dc.util.ReflectUtils;
import gt.research.dc.util.ResourceUtils;

/**
 * Created by ayi.zty on 2016/2/15.
 */
public class ResourceManager {
    private DisplayMetrics mMetrics;
    private Configuration mConfiguration;

    private IApkVerifier mVerifier;
    private ResourceCache mCache;

    private volatile static ResourceManager sInstance;

    private ResourceManager(DisplayMetrics metrics, Configuration configuration) {
        mMetrics = metrics;
        mConfiguration = configuration;

        mVerifier = new OriginalVerifier();
        mCache = new ResourceCache();
    }

    public static ResourceManager getInstance(DisplayMetrics metrics, Configuration configuration) {
        if (null == sInstance && null != metrics && null != configuration) {
            synchronized (ResourceManager.class) {
                if (null == sInstance) {
                    sInstance = new ResourceManager(metrics, configuration);
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
                listener.onResourceLoaded(entry.fetcher, entry.info);
                return;
            }
        }
        ApkConfigManager.getInstance().getApkInfoAndFileById(context, id, mVerifier,
                new ApkConfigManager.LoadApkInfoAndFileListener() {
            @Override
            public void onApkInfoAndFileListener(ApkInfo info, File apkFile) {
                if (!apkFile.exists()) {
                    listener.onResourceLoaded(null, info);
                    return;
                }
                if (TextUtils.isEmpty(info.pkgName)) {
                    LogUtils.debug("empty package update");
                    info.pkgName = ResourceUtils.updateApkPackage(context, apkFile);
                }
                if (TextUtils.isEmpty(info.pkgName)) {
                    listener.onResourceLoaded(null, info);
                    return;
                }
                try {
                    AssetManager assetManager = AssetManager.class.newInstance();
                    ReflectUtils.invokeMethod(assetManager, "addAssetPath",
                            new Class[]{String.class}, new Object[]{apkFile.getAbsolutePath()});
                    ResourceFetcher fetcher = new ResourceFetcher(info.pkgName, new Resources(assetManager, mMetrics, mConfiguration));
                    mCache.onNewResource(info, fetcher);
                    listener.onResourceLoaded(fetcher, info);
                } catch (Throwable throwable) {
                    LogUtils.exception(throwable);
                    listener.onResourceLoaded(null, info);
                }
            }
        });
    }

    public void setVerifier(IApkVerifier mVerifier) {
        this.mVerifier = mVerifier;
    }

    public ICache getCache() {
        return mCache;
    }

    public interface LoadResourceListener {
        void onResourceLoaded(ResourceFetcher fetcher, ApkInfo info);
    }
}
