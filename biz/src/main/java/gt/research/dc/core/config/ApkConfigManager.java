package gt.research.dc.core.config;

import android.content.Context;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import gt.research.dc.core.config.fetcher.IConfigFetcher;
import gt.research.dc.core.config.fetcher.NetFileFetcher;
import gt.research.dc.core.config.fetcher.OnConfigFetchedListener;
import gt.research.dc.core.config.verifier.IApkVerifier;
import gt.research.dc.core.config.verifier.OnVerifiedListener;
import gt.research.dc.core.config.verifier.original.PackageManagerVerifier;
import gt.research.dc.core.config.verifier.original.ReflectVerifier;
import gt.research.dc.core.db.Apk;
import gt.research.dc.core.db.ApkDao;
import gt.research.dc.core.db.DbManager;
import gt.research.dc.data.Config;
import gt.research.dc.event.CacheEventHandler;
import gt.research.dc.event.NewApkEventHandler;
import gt.research.dc.util.FileUtils;
import gt.research.dc.util.LogUtils;
import gt.research.dc.util.NetUtils;

/**
 * Created by ayi.zty on 2016/1/26.
 */
public class ApkConfigManager {
    public interface LoadApkInfoListener {
        void onApkInfoLoaded(Apk info);
    }

    public interface LoadApkInfoAndFileListener {
        void onApkInfoAndFile(Apk info, File apkFile);
    }

    private volatile static ApkConfigManager sInstance;

    private HashMap<String, Apk> mApks;
    private IConfigFetcher mFetcher;
    private IApkVerifier mVerifier;

    private ApkConfigManager() {
        LogUtils.debug(this, "new ApkConfigManager");
        mVerifier = new PackageManagerVerifier();
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

    public void getApkInfoById(Context context, final String id, boolean forceFetch,
                               final LoadApkInfoListener listener) {
        LogUtils.debug(this, "get apk by id " + id);
        if (null == listener) {
            return;
        }
        Runnable afterLoad = new Runnable() {
            @Override
            public void run() {
                listener.onApkInfoLoaded(mApks.get(id));
            }
        };
        updateConfig(context, afterLoad, forceFetch);
    }

    public void getApkInfoAndFileById(final Context context, final String id, boolean forceFetch,
                                      final LoadApkInfoAndFileListener listener) {
        LogUtils.debug(this, "get apk file by id " + id);
        if (null == listener) {
            return;
        }
        getApkInfoById(context, id, forceFetch,
                new LoadApkInfoListener() {
                    @Override
                    public void onApkInfoLoaded(final Apk info) {
                        if (null == info) {
                            listener.onApkInfoAndFile(null, null);
                            return;
                        }
                        LogUtils.debug(ApkConfigManager.this, "got info " + id);

                        final File apkFile = FileUtils.getCacheApkFile(context, info);
                        downloadAndVerifyApk(context, info, apkFile, listener);
                    }
                });
    }

    public void setConfigFetcher(IConfigFetcher fetcher) {
        mFetcher = fetcher;
    }

    public void setVerifier(IApkVerifier mVerifier) {
        this.mVerifier = mVerifier;
    }

    public void updateFromDb(Context context) {
        LogUtils.debug(this, "from db");
        ApkDao apkDao = DbManager.getInstance(context).getDao(Apk.class);
        if (null == apkDao) {
            return;
        }

        mApks = new HashMap<>();

        List<Apk> apks = apkDao.loadAll();
        if (null == apks || 0 == apks.size()) {
            mApks = null;
            return;
        }
        LogUtils.debug(this, "db size " + apks.size());
        for (Apk apk : apks) {
            mApks.put(apk.getApk(), apk);
        }
    }

    private void updateFromFetcher(final Context context, final Runnable afterLoad) {
        LogUtils.debug(this, "from net");
        mFetcher.fetch(context, new OnConfigFetchedListener() {
            @Override
            public void onConfigFetched(Config config) {
                if (null == config) {
                    return;
                }
                List<Apk> apks = config.update;
                if (null != apks) {
                    if (null == mApks) {
                        updateFromDb(context);
                    }
                    if (null == mApks) {
                        mApks = new HashMap<>();
                    }
                    for (Apk apk : apks) {
                        mApks.put(apk.getApk(), apk);
                    }
                    LogUtils.debug(ApkConfigManager.this, "config from fetcher " + apks.get(0).getApk());
                }

                updateDb(context, config);
                deleteDb(context, config);

                CacheEventHandler.invalidateAll(context);

                if (null != afterLoad) {
                    afterLoad.run();
                }
            }
        });
    }

    private void updateConfig(Context context, Runnable afterLoad, boolean forceFetch) {
        if (null == afterLoad) {
            return;
        }
        if (forceFetch) {
            updateFromFetcher(context, afterLoad);
            return;
        }
        if (null == mApks) {
            updateFromDb(context);
            if (null == mApks) {
                updateFromFetcher(context, afterLoad);
                return;
            }
        }
        afterLoad.run();
    }

    private void updateDb(Context context, Config config) {
        if (null == config || null == config.update) {
            return;
        }
        ApkDao apkDao = DbManager.getInstance(context).getDao(Apk.class);
        if (null == apkDao) {
            return;
        }
        for (Apk apk : config.update) {
            apkDao.insertOrReplace(apk);
        }
    }

    private void deleteDb(Context context, Config config) {
        if (null == config || null == config.delete) {
            return;
        }
        for (String id : config.delete) {
            DbManager.getInstance(context).deleteDataByApkId(id);
            FileUtils.deleteApk(context, id);
        }
    }

    private void downloadAndVerifyApk(final Context context, final Apk info, final File apkFile,
                                      final LoadApkInfoAndFileListener listener) {
        if (info.getTimestamp() < apkFile.lastModified()) {
            //cached file is up-to-date
            notifyLoadApkInfoAndFileListener(listener, info, apkFile);
            return;
        }
        NetUtils.download(context, info.getUrl(), new NetUtils.DownloadListener() {
            @Override
            public void onEnqueue(String url) {

            }

            @Override
            public void onFinish(String url, String file) {
                onFileGot(context, file, apkFile, info, listener);
            }

            @Override
            public void onCached(String url, String file) {
                // downloaded but not loaded
                onFileGot(context, file, apkFile, info, listener);
            }

            @Override
            public void onFail() {
                notifyLoadApkInfoAndFileListener(listener, info, apkFile);
            }
        });
    }

    private void onFileGot(final Context context, final String file, final File apkFile, final Apk info,
                           final LoadApkInfoAndFileListener loadListener) {
        OnVerifiedListener listener = new OnVerifiedListener() {
            @Override
            public void onVerified(boolean isSecure) {
                if (isSecure) {
                    FileUtils.copy(file, apkFile.getAbsolutePath());
                } else {
                    new File(file).delete();
                }
                // keep order
                NewApkEventHandler.onNewApk(context, info, apkFile);
                CacheEventHandler.invalidateCache(context, info.getApk());

                notifyLoadApkInfoAndFileListener(loadListener, info, apkFile);
            }
        };
        if (null == mVerifier) {
            listener.onVerified(true);
            return;
        }
        mVerifier.verify(context, file, listener);
    }

    private void notifyLoadApkInfoAndFileListener(LoadApkInfoAndFileListener listener, Apk info, File apkFile) {
        if (null == listener) {
            return;
        }
        listener.onApkInfoAndFile(info, apkFile);
    }
}
