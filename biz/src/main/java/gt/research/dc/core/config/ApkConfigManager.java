package gt.research.dc.core.config;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import de.greenrobot.dao.query.DeleteQuery;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;
import gt.research.dc.core.command.verifier.IApkVerifier;
import gt.research.dc.core.command.verifier.OnVerifiedListener;
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
import gt.research.dc.util.CacheUtils;
import gt.research.dc.util.FileUtils;
import gt.research.dc.util.LogUtils;
import gt.research.dc.util.NetUtils;
import gt.research.dc.util.ResourceUtils;

/**
 * Created by ayi.zty on 2016/1/26.
 */
public class ApkConfigManager {
    private volatile static ApkConfigManager sInstance;

    private HashMap<String, ApkInfo> mInterfaceIndex;
    private IConfigFetcher mFetcher;
    private List<String> mApkToDelete;

    private ApkDao mApkDao;
    private IntfDao mIntfDao;
    private DaoSession mSession;

    private ApkConfigManager() {
        mFetcher = new NetFileFetcher();
    }

    public static ApkConfigManager getInstance() {
        if (null == sInstance) {
            synchronized (ApkConfigManager.class) {
                if (null == sInstance) {
                    sInstance = new ApkConfigManager();
                }
            }
        }
        return sInstance;
    }

    public void getApkInfoByInterface(Context context, final String intf, final LoadApkInfoListener listener) {
        if (null == listener) {
            return;
        }
        Runnable afterLoad = new Runnable() {
            @Override
            public void run() {
                ApkInfo info = mInterfaceIndex.get(intf);
                listener.onApkInfoLoaded(info);
            }
        };
        loadConfigByAllMeans(context, afterLoad);
    }

    public void getApkInfoById(Context context, final String id, final LoadApkInfoListener listener) {
        if (null == listener) {
            return;
        }
        Runnable afterLoad = new Runnable() {
            @Override
            public void run() {
                for (ApkInfo info : mInterfaceIndex.values()) {
                    if (TextUtils.equals(id, info.id)) {
                        listener.onApkInfoLoaded(info);
                        return;
                    }
                }
            }
        };
        loadConfigByAllMeans(context, afterLoad);
    }

    public void getApkInfoAndFileByInterface(final Context context, final String intf, final IApkVerifier verifier,
                                             final LoadApkInfoAndFileListener listener) {
        if (null == listener) {
            return;
        }
        getApkInfoByInterface(context, intf, new LoadApkInfoListener() {
            @Override
            public void onApkInfoLoaded(final ApkInfo info) {
                if (null == info) {
                    listener.onApkInfoAndFileListener(null, null);
                    return;
                }

                final File apkFile = FileUtils.getCacheApkFile(context, info);
                downloadAndVerifyApk(context, info, apkFile, verifier, new Runnable() {
                    @Override
                    public void run() {
                        listener.onApkInfoAndFileListener(info, apkFile);
                    }
                });
            }
        });
    }

    public void getApkInfoAndFileById(final Context context, final String intf, final IApkVerifier verifier,
                                      final LoadApkInfoAndFileListener listener){
        if (null == listener) {
            return;
        }
        getApkInfoById(context, intf, new LoadApkInfoListener() {
            @Override
            public void onApkInfoLoaded(final ApkInfo info) {
                if (null == info) {
                    listener.onApkInfoAndFileListener(null, null);
                    return;
                }

                final File apkFile = FileUtils.getCacheApkFile(context, info);
                downloadAndVerifyApk(context, info, apkFile, verifier, new Runnable() {
                    @Override
                    public void run() {
                        listener.onApkInfoAndFileListener(info, apkFile);
                    }
                });
            }
        });
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
            apkInfo.url = apk.getUrl();
            apkInfo.pkgName = apk.getPkgName();
            apkInfo.timestamp = apk.getTimestamp();
            apkInfos.put(apkInfo.id, apkInfo);
        }

        List<Intf> intfs = intfDao.loadAll();
        if (0 == intfs.size()) {
            return;
        }
        mInterfaceIndex = new HashMap<>();
        for (Intf intf : intfs) {
            ApkInfo apkInfo = apkInfos.get(intf.getApk());
            if (null != apkInfo) {
                apkInfo.interfaces.put(intf.getIntf(), intf.getImpl());
                mInterfaceIndex.put(intf.getIntf(), apkInfo);
            }
        }
    }

    public void updateConfig(final Context context, final Runnable afterLoad) {
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
                saveConfigToDb(context);
                CacheUtils.invalidateAll();
                if (null != afterLoad) {
                    afterLoad.run();
                }
            }
        });
    }

    public void setConfigFetcher(IConfigFetcher fetcher) {
        mFetcher = fetcher;
    }

    private void loadConfigByAllMeans(Context context, Runnable afterLoad) {
        if (null == afterLoad) {
            return;
        }
        if (null == mInterfaceIndex) {
            loadLocalConfig(context);
            if (null == mInterfaceIndex) {
                updateConfig(context, afterLoad);
                return;
            }
        }
        afterLoad.run();
    }

    private void saveConfigToDb(Context context) {
        initDao(context);

        deleteDb(context);
        updateDb();

        mSession.clear();
    }

    private void updateDb() {
        HashSet<ApkInfo> apkInfos = new HashSet<>();

        for (String intf : mInterfaceIndex.keySet()) {
            ApkInfo apkInfo = mInterfaceIndex.get(intf);
            apkInfos.add(apkInfo);
            Intf intfEntity = new Intf(intf, apkInfo.getImplement(intf), apkInfo.id);
            mIntfDao.insertOrReplace(intfEntity);
        }

        for (ApkInfo apkInfo : apkInfos) {
            Apk old = mApkDao.load(apkInfo.id);
            Apk apk = new Apk(apkInfo.id, apkInfo.url, apkInfo.pkgName, apkInfo.timestamp);
            mApkDao.insertOrReplace(apk);
        }
    }

    private void deleteDb(Context context) {
        for (String id : mApkToDelete) {
            mApkDao.deleteByKey(id);

            QueryBuilder queryBuilder = mIntfDao.queryBuilder();
            queryBuilder.where(new WhereCondition.StringCondition("APK = '" + id + "'"));
            DeleteQuery delete = queryBuilder.buildDelete();
            delete.executeDeleteWithoutDetachingEntities();

            FileUtils.deleteApk(context, id);
            CacheUtils.invalidateCache(id);
        }
    }

    private void initDao(Context context) {
        if (null == mSession) {
            SQLiteOpenHelper helper = new DaoMaster.DevOpenHelper(context, DBConstants.DB_FILE_CONFIG, null);
            DaoMaster daoMaster = new DaoMaster(helper.getReadableDatabase());
            mSession = daoMaster.newSession();
            mApkDao = mSession.getApkDao();
            mIntfDao = mSession.getIntfDao();
        }
    }

    private void downloadAndVerifyApk(final Context context, final ApkInfo info, final File apkFile,
                                     final IApkVerifier verifier, final Runnable afterLoad) {
        if (info.timestamp < apkFile.lastModified()) {
            if (null != afterLoad) {
                afterLoad.run();
            }
            return;
        }
        NetUtils.download(context, info.url, new NetUtils.DownloadListener() {
            @Override
            public void onEnqueue(String url) {

            }

            @Override
            public void onFinish(String url, String file) {
                onFileGot(context, file, apkFile, info, afterLoad, true, verifier);
            }

            @Override
            public void onCached(String url, String file) {
                onFileGot(context, file, apkFile, info, afterLoad, false, verifier);
            }

            @Override
            public void onFail() {
                if (null != afterLoad) {
                    afterLoad.run();
                }
            }
        });
    }

    private void onFileGot(final Context context, final String file, final File apkFile, final ApkInfo info,
                           final Runnable afterLoad, final boolean updatePackage, final IApkVerifier verifier) {
        OnVerifiedListener listener = new OnVerifiedListener() {
            @Override
            public void onVerified(boolean isSecure) {
                if (isSecure) {
                    FileUtils.copy(file, apkFile.getAbsolutePath());
                } else {
                    new File(file).delete();
                }
                if (updatePackage && apkFile.exists()) {
                    LogUtils.debug("download done update");
                    info.pkgName = ResourceUtils.updateApkPackage(context, apkFile);
                }
                CacheUtils.invalidateCache(info.id);
                if (null != afterLoad) {
                    afterLoad.run();
                }
            }
        };
        if (null == verifier) {
            listener.onVerified(true);
            return;
        }
        verifier.verify(context, file, listener);
    }

    public interface LoadApkInfoListener {
        void onApkInfoLoaded(ApkInfo info);
    }

    public interface LoadApkInfoAndFileListener {
        void onApkInfoAndFileListener(ApkInfo info, File apkFile);
    }
}
