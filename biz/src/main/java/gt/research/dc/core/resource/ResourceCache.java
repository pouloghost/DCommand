package gt.research.dc.core.resource;

import android.content.Context;

import gt.research.dc.core.common.ICache;
import gt.research.dc.core.common.LruCacheMap;
import gt.research.dc.core.db.Apk;

/**
 * Created by ayi.zty on 2016/2/16.
 */
public class ResourceCache implements ICache, ResourceManager.LoadResourceListener {
    private LruCacheMap<String, Entry> mCache;

    public ResourceCache() {
        mCache = new LruCacheMap<>(6);
    }

    public Entry getCachedResource(String id) {
        return mCache.get(id);
    }

    @Override
    public void invalidate(Context context, String id) {
        mCache.remove(id);
    }

    @Override
    public void clear(Context context) {
        mCache.evictAll();
    }

    @Override
    public void onResourceLoaded(ResourceFetcher fetcher, Apk info) {
        mCache.put(info.getApk(), new Entry(info, fetcher));
    }

    public static class Entry {
        public ResourceFetcher fetcher;
        public Apk info;

        public Entry(Apk apkInfo, ResourceFetcher resourceFetcher) {
            fetcher = resourceFetcher;
            info = apkInfo;
        }
    }
}
