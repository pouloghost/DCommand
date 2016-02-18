package gt.research.dc.core.resource;

import gt.research.dc.core.common.ICache;
import gt.research.dc.core.common.LruCacheMap;
import gt.research.dc.core.db.Apk;

/**
 * Created by ayi.zty on 2016/2/16.
 */
public class ResourceCache implements ICache {
    private LruCacheMap<String, Entry> mCache;

    public ResourceCache() {
        mCache = new LruCacheMap<>(6);
    }

    public Entry getCachedResource(String id) {
        return mCache.get(id);
    }

    public void onNewResource(Apk info, ResourceFetcher fetcher) {
        mCache.put(info.getApk(), new Entry(info, fetcher));
    }

    @Override
    public void invalidate(String id) {
        mCache.remove(id);
    }

    @Override
    public void clear() {
        mCache.evictAll();
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
