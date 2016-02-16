package gt.research.dc.core.resource;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;

/**
 * Created by ayi.zty on 2016/2/16.
 */
public class ResourceFetcher {
    // TODO: 2016/2/16 add more types of resources
    private static final String sTypeString = "string";
    private static final String sTypeDrawable = "drawable";

    private String mPackage;
    private Resources mResources;

    public ResourceFetcher(String pkgName, Resources resources) {
        mPackage = pkgName;
        mResources = resources;
    }

    public String getString(String name) {
        int id = mResources.getIdentifier(name, sTypeString, mPackage);
        return mResources.getString(id);
    }
    public Drawable getDrawable(String name){
        int id = mResources.getIdentifier(name, sTypeDrawable, mPackage);
        return mResources.getDrawable(id);
    }
}
