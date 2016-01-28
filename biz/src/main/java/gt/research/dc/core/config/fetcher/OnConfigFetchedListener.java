package gt.research.dc.core.config.fetcher;

import java.util.List;

import gt.research.dc.data.ApkInfo;

/**
 * Created by ayi.zty on 2016/1/28.
 */
public interface OnConfigFetchedListener {
    void onConfigFetched(List<ApkInfo> apkInfos);
}
