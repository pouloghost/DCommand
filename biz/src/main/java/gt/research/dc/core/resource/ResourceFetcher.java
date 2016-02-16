package gt.research.dc.core.resource;

import android.content.res.Resources;

/**
 * Created by ayi.zty on 2016/2/16.
 */
public class ResourceFetcher {
    private static final String sTypeString = "string";

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
}
