package gt.research.dc.core.config.fetcher;

import android.content.Context;

import com.alibaba.fastjson.JSON;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import gt.research.dc.data.Config;
import gt.research.dc.util.LogUtils;
import gt.research.dc.util.NetUtils;

/**
 * Created by ayi.zty on 2016/1/28.
 */
public class NetFileFetcher implements IConfigFetcher {
    private String mUrl;

    @Override
    public void fetch(Context context, final OnConfigFetchedListener listener) {
        LogUtils.debug(this, mUrl);
        NetUtils.download(context, mUrl,
                new NetUtils.DownloadListener() {
                    @Override
                    public void onEnqueue(String url) {
                        LogUtils.debug(NetFileFetcher.this, "enqueue");
                    }

                    @Override
                    public void onFinish(String url, String file) {
                        LogUtils.debug(NetFileFetcher.this, "finish");
                        onFileGot(file);
                    }

                    @Override
                    public void onCached(String url, String file) {
                        LogUtils.debug(NetFileFetcher.this, "cached");
                        onFileGot(file);
                    }

                    @Override
                    public void onFail() {
                        listener.onConfigFetched(null);
                    }

                    private void onFileGot(String file) {
                        if (null == file) {
                            listener.onConfigFetched(null);
                            return;
                        }
                        LogUtils.debug(NetFileFetcher.this, file);
                        File config = new File(file);
                        if (!config.exists()) {
                            listener.onConfigFetched(null);
                            return;
                        }
                        Config configEntity = readConfigFromFile(config);
                        if (null != listener) {
                            listener.onConfigFetched(configEntity);
                        }
                    }
                });
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    private Config readConfigFromFile(File config) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(config));
            String json = reader.readLine();
            return JSON.parseObject(json, Config.class);
        } catch (IOException e) {
            LogUtils.exception(NetFileFetcher.this, e);
            return null;
        }
    }
}
