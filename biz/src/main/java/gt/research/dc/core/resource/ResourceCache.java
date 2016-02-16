package gt.research.dc.core.resource;

import android.util.LruCache;

import gt.research.dc.data.ApkInfo;

/**
 * Created by ayi.zty on 2016/2/16.
 */
public class ResourceCache {
    private LruCache<String, Entry> mCache;

    public ResourceCache() {
        mCache = new LruCache<>(6);
    }

    public Entry getCachedResource(String id) {
        return mCache.get(id);
    }

    public void onNewResource(ApkInfo info, ResourceFetcher fetcher) {
        mCache.put(info.id, new Entry(info, fetcher));
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
