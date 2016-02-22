package gt.research.dc.core.classloader.component;

import android.content.Context;
import android.content.Intent;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import gt.research.dc.core.classloader.ClassFetcher;
import gt.research.dc.core.classloader.ClassManager;
import gt.research.dc.core.common.ICache;
import gt.research.dc.core.common.manifest.Manifest;
import gt.research.dc.core.config.ApkConfigManager;
import gt.research.dc.core.constant.ComponentConstants;
import gt.research.dc.core.db.Apk;
import gt.research.dc.core.db.Comp;
import gt.research.dc.core.db.CompDao;
import gt.research.dc.core.db.DbManager;
import gt.research.dc.event.IOnNewApkListener;
import gt.research.dc.util.LogUtils;

/**
 * Created by ayi.zty on 2016/2/19.
 */
public class ComponentManager implements ICache, IOnNewApkListener {
    private Map<String, Comp> mActivities;

    private volatile static ComponentManager sInstance;

    private ComponentManager(Context context) {
        loadDb(context);
    }

    public static ComponentManager getInstance(Context context) {
        if (null == sInstance) {
            synchronized (ComponentManager.class) {
                if (null == sInstance) {
                    sInstance = new ComponentManager(context);
                }
            }
        }
        return sInstance;
    }

    public void startActivity(final Context context, final String comp, final String id) {
        ApkConfigManager.getInstance().getApkInfoAndFileById(context, id, false,
                new ApkConfigManager.LoadApkInfoAndFileListener() {
                    @Override
                    public void onApkInfoAndFile(Apk info, File apkFile) {
                        if (null == context || TextUtils.isEmpty(comp) || TextUtils.isEmpty(id)) {
                            return;
                        }
                        Intent intent = new Intent();
                        intent.setClass(context, ProxyActivity.class);
                        intent.putExtra(ComponentConstants.BUNDLE_APK_ID, id);
                        intent.putExtra(ComponentConstants.BUNDLE_COMPONENT_NAME, comp);
                        context.startActivity(intent);
                    }
                });
    }

    public void loadActivity(final Context context, final String comp, final String id, boolean ignoreCache,
                             final LoadComponentListener<BaseActivity> listener) {
        ClassManager.getInstance().loadClass(context, id, ignoreCache,
                new ClassManager.LoadClassListener() {
                    @Override
                    public void onClassLoaded(ClassFetcher fetcher, Apk info) {
                        if (null == fetcher) {
                            notifyListener(null, null);
                            return;
                        }
                        //check existence
                        Comp compInfo = mActivities.get(getKey(id, comp));
                        if (null == compInfo) {
                            notifyListener(null, null);
                            return;
                        }
                        Class activityClass = fetcher.getClass(comp);
                        BaseActivity instance = null;
                        try {
                            instance = (BaseActivity) activityClass.newInstance();
                        } catch (InstantiationException | IllegalAccessException e) {
                            LogUtils.exception(ComponentManager.this, e);
                        } finally {
                            notifyListener(instance, compInfo);
                        }
                    }

                    private void notifyListener(BaseActivity instance, Comp info) {
                        if (null == listener) {
                            return;
                        }
                        listener.onComponentLoaded(instance, info);
                    }
                });
    }

    @Override

    public void onNewApk(Context context, Apk info, File apkFile, Manifest manifest) {
        LogUtils.debug(this, "new apk " + info.getApk());
        DbManager dbManager = DbManager.getInstance(context);
        CompDao compDao = dbManager.getDao(Comp.class);
        dbManager.deleteDataByApkIdInDb(info.getApk(), "APK", compDao);
        List<Comp> comps = manifest.getComponents();
        for (Comp comp : comps) {
            comp.setApk(info.getApk());
            compDao.insertOrReplace(comp);
        }
        LogUtils.debug(this, "update db " + comps.size());
    }

    @Override
    public void invalidate(Context context, String id) {
        LogUtils.debug(this, "invalidate apk " + id);
        invalidateInMap(mActivities, id);
        DbManager dbManager = DbManager.getInstance(context);
        CompDao compDap = dbManager.getDao(Comp.class);
        List<Comp> fromDb = dbManager.loadDataByApkIdInDb(id, "APK", compDap);
        for (Comp comp : fromDb) {
            addComponent(comp);
        }
    }

    @Override
    public void clear(Context context) {
        loadDb(context);
    }

    private void loadDb(Context context) {
        CompDao compDao = DbManager.getInstance(context).getDao(Comp.class);
        mActivities = new ArrayMap<>();
        List<Comp> comps = compDao.loadAll();
        for (Comp comp : comps) {
            addComponent(comp);
        }
        LogUtils.debug(this, "load db activity " + mActivities.size());
    }

    private void invalidateInMap(Map<String, Comp> map, String id) {
        Collection<Comp> values = map.values();
        for (Comp comp : values) {
            if (TextUtils.equals(comp.getApk(), id)) {
                map.remove(getKey(comp));
            }
        }
    }

    private void addComponent(Comp comp) {
        switch (comp.getType()) {
            case Manifest.sTagActivity:
                mActivities.put(getKey(comp), comp);
                break;
        }
    }

    private String getKey(Comp comp) {
        return getKey(comp.getApk(), comp.getComp());
    }

    private String getKey(String id, String comp) {
        return id + ":" + comp;
    }

    public interface StartComponentListener {
        void onComponentStarted(boolean succes);
    }

    public interface LoadComponentListener<T> {
        void onComponentLoaded(T instance, Comp info);
    }
}
