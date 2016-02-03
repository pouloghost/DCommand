package gt.research.dc.core.config;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import de.greenrobot.dao.query.DeleteQuery;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;
import gt.research.dc.core.config.fetcher.IConfigFetcher;
import gt.research.dc.core.config.fetcher.NetFileFetcher;
import gt.research.dc.core.config.fetcher.OnConfigFetchedListener;
import gt.research.dc.core.constant.DBConstants;
import gt.research.dc.core.db.Apk;
import gt.research.dc.core.db.ApkDao;
import gt.research.dc.core.db.DaoMaster;
import gt.research.dc.core.db.DaoSession;
import gt.research.dc.core.db.Intf;
import gt.research.dc.core.db.IntfDao;
import gt.research.dc.data.ApkInfo;
import gt.research.dc.data.Config;
import gt.research.dc.util.FileUtils;
import gt.research.dc.util.LogUtils;
import gt.research.dc.util.VersionUtils;

/**
 * Created by ayi.zty on 2016/1/26.
 */
public class ConfigManager {
    private volatile static ConfigManager sInstance;

    private HashMap<String, ApkInfo> mInterfaceIndex;
    private IConfigFetcher mFetcher;
    private List<String> mApkToDelete;

    private ConfigManager() {
        mFetcher = new NetFileFetcher();
    }

    public static ConfigManager getInstance() {
        if (null == sInstance) {
            synchronized (ConfigManager.class) {
                if (null == sInstance) {
                    sInstance = new ConfigManager();
                }
            }
        }
        return sInstance;
    }

    public void getApk(Context context, final String intf, final LoadApkInfoListener listener) {
        if (null == listener) {
            return;
        }
        Runnable afterLoad = new Runnable() {
            @Override
            public void run() {
                ApkInfo info = mInterfaceIndex.get(intf);
                listener.onApkLoaded(info);
            }
        };
        if (null == mInterfaceIndex) {
            updateConfig(context, afterLoad);
            return;
        }
        afterLoad.run();
    }

    public void loadLocalConfig(Context context) {
        mInterfaceIndex = null;
        SQLiteOpenHelper helper = new DaoMaster.DevOpenHelper(context, DBConstants.DB_FILE_CONFIG, null);
        DaoMaster daoMaster = new DaoMaster(helper.getReadableDatabase());
        DaoSession session = daoMaster.newSession();
        ApkDao apkDao = session.getApkDao();
        IntfDao intfDao = session.getIntfDao();

        List<Apk> apks = apkDao.loadAll();
        Map<String, ApkInfo> apkInfos = new HashMap<>();
        for (Apk apk : apks) {
            ApkInfo apkInfo = new ApkInfo();
            apkInfo.interfaces = new HashMap<>();
            apkInfo.id = apk.getId();
            apkInfo.version = apk.getVersion();
            apkInfo.url = apk.getUrl();
            apkInfo.isLatest = apk.getLatest();
            apkInfos.put(apkInfo.id, apkInfo);
        }

        List<Intf> intfs = intfDao.loadAll();
        mInterfaceIndex = new HashMap<>();
        for (Intf intf : intfs) {
            ApkInfo apkInfo = apkInfos.get(intf.getApk());
            if (null != apkInfo) {
                apkInfo.interfaces.put(intf.getIntf(), intf.getImpl());
                mInterfaceIndex.put(intf.getIntf(), apkInfo);
            }
        }
    }

    public void updateLocalConfig(final Context context) {
        updateConfig(context, new Runnable() {
            @Override
            public void run() {
                saveConfigToDb(context);
            }
        });
    }

    public void updateConfig(Context context, final Runnable afterLoad) {
        mFetcher.fetch(context, new OnConfigFetchedListener() {
            @Override
            public void onConfigFetched(Config config) {
                if (null == config) {
                    return;
                }
                List<ApkInfo> apkInfos = config.update;
                if (null != apkInfos) {
                    mInterfaceIndex = new HashMap<>();
                    for (ApkInfo apkInfo : apkInfos) {
                        for (String intf : apkInfo.interfaces.keySet()) {
                            mInterfaceIndex.put(intf, apkInfo);
                        }
                    }
                    LogUtils.debug(apkInfos.get(0).id);
                }
                if (null != config.delete) {
                    mApkToDelete = config.delete;
                }
                if (null != afterLoad) {
                    afterLoad.run();
                }
            }
        });
    }

    public void setConfigFetcher(IConfigFetcher fetcher) {
        mFetcher = fetcher;
    }

    private void saveConfigToDb(Context context) {
        SQLiteOpenHelper helper = new DaoMaster.DevOpenHelper(context, DBConstants.DB_FILE_CONFIG, null);
        DaoMaster daoMaster = new DaoMaster(helper.getReadableDatabase());
        DaoSession session = daoMaster.newSession();
        ApkDao apkDao = session.getApkDao();
        IntfDao intfDao = session.getIntfDao();

        deleteDb(context, apkDao, intfDao);
        updateDb(apkDao, intfDao);

        session.clear();
    }

    private void updateDb(ApkDao apkDao, IntfDao intfDao) {
        HashSet<ApkInfo> apkInfos = new HashSet<>();

        for (String intf : mInterfaceIndex.keySet()) {
            ApkInfo apkInfo = mInterfaceIndex.get(intf);
            apkInfos.add(apkInfo);
            Intf intfEntity = new Intf(intf, apkInfo.getImplement(intf), apkInfo.id);
            intfDao.insertOrReplace(intfEntity);
        }

        for (ApkInfo apkInfo : apkInfos) {
            Apk old = apkDao.load(apkInfo.id);
            boolean isLatest = null != old &&
                    (VersionUtils.isLatest(old.getVersion(), apkInfo.version) && old.getLatest());
            Apk apk = new Apk(apkInfo.id, apkInfo.version, apkInfo.url, isLatest);
            apkDao.insertOrReplace(apk);
        }
    }

    private void deleteDb(Context context, ApkDao apkDao, IntfDao intfDao) {
        for (String id : mApkToDelete) {
            apkDao.deleteByKey(id);

            QueryBuilder queryBuilder = intfDao.queryBuilder();
            queryBuilder.where(new WhereCondition.StringCondition("APK = '" + id + "'"));
            DeleteQuery delete = queryBuilder.buildDelete();
            delete.executeDeleteWithoutDetachingEntities();

            FileUtils.deleteApk(context, id);
        }
    }

    public interface LoadApkInfoListener {
        void onApkLoaded(ApkInfo info);
    }
}
