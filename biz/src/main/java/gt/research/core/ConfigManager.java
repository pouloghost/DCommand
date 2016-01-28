package gt.research.core;

import android.content.Context;

import com.alibaba.fastjson.JSON;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

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

    public void loadLocalConfig() {

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

    public interface LoadApkInfoListener {
        void onApkLoaded(ApkInfo info);
    }
}
