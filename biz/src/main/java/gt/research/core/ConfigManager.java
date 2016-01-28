package gt.research.core;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

import com.alibaba.fastjson.JSON;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import gt.research.core.db.Apk;
import gt.research.core.db.ApkDao;
import gt.research.core.db.DaoMaster;
import gt.research.core.db.DaoSession;
import gt.research.core.db.Intf;
import gt.research.core.db.IntfDao;
import gt.research.dc.core.DBConstants;
import gt.research.dc.data.ApkInfo;
import gt.research.util.LogUtils;
import gt.research.util.NetUtils;

/**
 * Created by ayi.zty on 2016/1/26.
 */
public class ConfigManager {
    private volatile static ConfigManager sInstance;

    private HashMap<String, ApkInfo> mInterfaceIndex;

    private ConfigManager() {

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
        NetUtils.download(context, "https://os.alipayobjects.com/rmsportal/levEFbWxKrptmkb.json",
                new NetUtils.DownloadListener() {
                    @Override
                    public void onEnqueue(String url) {
                        LogUtils.debug("enqueue");
                    }

                    @Override
                    public void onFinish(String url, String file) {
                        onFileGot(file);
                    }

                    @Override
                    public void onCached(String url, String file) {
                        onFileGot(file);
                    }

                    private void onFileGot(String file) {
                        LogUtils.debug(file);
                        File config = new File(file);
                        if (!config.exists()) {
                            return;
                        }
                        readConfigFromFile(config);
                        if (null != afterLoad) {
                            afterLoad.run();
                        }
                    }
                });
    }

    private void readConfigFromFile(File config) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(config));
            String json = reader.readLine();
            List<ApkInfo> apkInfos = JSON.parseArray(json, ApkInfo.class);
            mInterfaceIndex = new HashMap<>();
            for (ApkInfo apkInfo : apkInfos) {
                for (String intf : apkInfo.interfaces.keySet()) {
                    mInterfaceIndex.put(intf, apkInfo);
                }
            }
            LogUtils.debug(apkInfos.get(0).id);
        } catch (IOException e) {
            LogUtils.exception(e);
        }
    }

    private void saveConfigToDb(Context context) {
        SQLiteOpenHelper helper = new DaoMaster.DevOpenHelper(context, DBConstants.DB_FILE_CONFIG, null);
        DaoMaster daoMaster = new DaoMaster(helper.getReadableDatabase());
        DaoSession session = daoMaster.newSession();
        ApkDao apkDao = session.getApkDao();
        IntfDao intfDao = session.getIntfDao();

        HashSet<ApkInfo> apkInfos = new HashSet<>();

        for (String intf : mInterfaceIndex.keySet()) {
            ApkInfo apkInfo = mInterfaceIndex.get(intf);
            apkInfos.add(apkInfo);
            Intf intfEntity = new Intf(intf, apkInfo.getImplement(intf), apkInfo.id);
            intfDao.insertOrReplace(intfEntity);
        }

        for (ApkInfo apkInfo : apkInfos) {
            Apk apk = new Apk(apkInfo.id, apkInfo.version, apkInfo.url);
            apkDao.insertOrReplace(apk);
        }
    }

    public interface LoadApkInfoListener {
        void onApkLoaded(ApkInfo info);
    }
}
