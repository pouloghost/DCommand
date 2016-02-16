package gt.research.dc.core.resource;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import org.xmlpull.v1.XmlPullParser;

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

    public ResourceFetcher(String pkgName, Resources resources) {
        mPackage = pkgName;
        mResources = resources;
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
}
