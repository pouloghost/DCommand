package gt.research.dc.core.resource;

import gt.research.dc.core.common.ICache;
import gt.research.dc.core.common.LruCacheMap;

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

    public void onNewResource(ApkInfo info, ResourceFetcher fetcher) {
        mCache.put(info.id, new Entry(info, fetcher));
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
        public ApkInfo info;

        public Entry(ApkInfo apkInfo, ResourceFetcher resourceFetcher) {
            fetcher = resourceFetcher;
            info = apkInfo;
        }
    }
}
