package gt.research.dc.core.resource;

import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;

import gt.research.dc.core.db.Apk;
import gt.research.dc.util.LogUtils;
import gt.research.dc.util.ReflectUtils;

/**
 * Created by ayi.zty on 2016/2/16.
 */
public class ResourceFetcher {
    // TODO: 2016/2/16 add more types of resources
    private static final String sTypeString = "string";
    private static final String sTypeDrawable = "drawable";
    private static final String sTypeId = "id";
    private static final String sTypeLayout = "layout";

    private String mPackage;
    private Resources mResources;
    private boolean mUsable;

    public ResourceFetcher(Apk info, File apkFile, DisplayMetrics metrics, Configuration configuration) {
        mUsable = false;
        AssetManager assetManager = null;
        try {
            assetManager = AssetManager.class.newInstance();
            ReflectUtils.invokeMethod(assetManager, "addAssetPath",
                    new Class[]{String.class}, new Object[]{apkFile.getAbsolutePath()});
            mPackage = info.getPkgName();
            mResources = new Resources(assetManager, metrics, configuration);
            mUsable = true;
        } catch (Throwable e) {
            LogUtils.exception(this, e);
        }
    }

    public Resources getResources() {
        return mResources;
    }

    public String getString(String name) {
        int id = mResources.getIdentifier(name, sTypeString, mPackage);
        return mResources.getString(id);
    }

    public Drawable getDrawable(String name) {
        int id = mResources.getIdentifier(name, sTypeDrawable, mPackage);
        return mResources.getDrawable(id);
    }

    public int getId(String name) {
        return mResources.getIdentifier(name, sTypeId, mPackage);
    }

    public XmlPullParser getLayout(String name) {
        int id = mResources.getIdentifier(name, sTypeLayout, mPackage);
        return mResources.getLayout(id);
    }

    public AssetManager getAsset() {
        return mResources.getAssets();
    }

    public String getPackage() {
        return mPackage;
    }

    public boolean isUsable() {
        return mUsable;
    }
}
