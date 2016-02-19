package gt.research.dc.core.classloader;

import gt.research.dc.core.common.ICache;
import gt.research.dc.core.common.LruCacheMap;
import gt.research.dc.core.db.Apk;

/**
 * Created by ayi.zty on 2016/2/19.
 */
public class ClassLoaderCache implements ICache, ClassManager.LoadClassListener {
    private LruCacheMap<String, Entry> mCache;

    public ClassLoaderCache() {
        mCache = new LruCacheMap<>(6);
    }

    public Entry getCachedClassLoader(String id) {
        return mCache.get(id);
    }

    @Override
    public void invalidate(String id) {
        mCache.remove(id);
    }

    @Override
    public void clear() {
        mCache.evictAll();
    }

    @Override
    public void onClassLoaded(ClassFetcher fetcher, Apk info) {
        mCache.put(info.getApk(), new Entry(info, fetcher));
    }

    public static class Entry {
        public ClassFetcher fetcher;
        public Apk info;

        public Entry(Apk apkInfo, ClassFetcher resourceFetcher) {
            fetcher = resourceFetcher;
            info = apkInfo;
        }
    }
}
