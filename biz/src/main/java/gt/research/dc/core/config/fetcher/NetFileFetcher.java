package gt.research.dc.core.config.fetcher;

import android.content.Context;

import com.alibaba.fastjson.JSON;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import gt.research.dc.data.ApkInfo;
import gt.research.dc.util.LogUtils;
import gt.research.dc.util.NetUtils;

/**
 * Created by ayi.zty on 2016/1/28.
 */
public class NetFileFetcher implements IConfigFetcher {
    private String mUrl = "https://os.alipayobjects.com/rmsportal/QkAfZPDbQxVqHkP.json";

    @Override
    public void fetch(Context context, final OnConfigFetchedListener listener) {
        NetUtils.download(context, mUrl,
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
                        List<ApkInfo> apkInfos = readConfigFromFile(config);
                        if (null != listener) {
                            listener.onConfigFetched(apkInfos);
                        }
                    }
                });
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    private List<ApkInfo> readConfigFromFile(File config) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(config));
            String json = reader.readLine();
            return JSON.parseArray(json, ApkInfo.class);
        } catch (IOException e) {
            LogUtils.exception(e);
            return null;
        }
    }
}
