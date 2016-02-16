package gt.research.dc.core.resource;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import java.io.File;

import gt.research.dc.data.ApkInfo;
import gt.research.dc.util.FileUtils;
import gt.research.dc.util.LogUtils;
import gt.research.dc.util.ReflectUtils;

/**
 * Created by ayi.zty on 2016/2/15.
 */
public class ResourceManager {
    private DisplayMetrics mMetrics;
    private Configuration mConfiguration;

    private volatile static ResourceManager sInstance;

    private ResourceManager(DisplayMetrics metrics, Configuration configuration) {
        mMetrics = metrics;
        mConfiguration = configuration;
    }

    public static ResourceManager getInstance(DisplayMetrics metrics, Configuration configuration) {
        if (null == sInstance) {
            synchronized (ResourceManager.class) {
                if (null == sInstance) {
                    sInstance = new ResourceManager(metrics, configuration);
                }
            }
        }
        return sInstance;
    }

    public void loadResource(Context context, ApkInfo info, LoadResourceListener listener) {
        if (null == listener) {
            return;
        }
        File apkFile = FileUtils.getCacheApkFile(context, info);
        if (!apkFile.exists()) {
            return;
        }
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            ReflectUtils.invokeMethod(assetManager, "addAssetPath",
                    new Class[]{String.class}, new Object[]{apkFile.getAbsolutePath()});

            Resources resources = new Resources(assetManager, mMetrics, mConfiguration);
            listener.onResourceLoaded(resources);
        } catch (Throwable throwable) {
            LogUtils.exception(throwable);
            listener.onResourceLoaded(null);
        }

    }

    public interface LoadResourceListener {
        void onResourceLoaded(Resources resources);
    }
}
