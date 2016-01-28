package gt.research.dc.core.config.fetcher;

import android.content.Context;

/**
 * Created by ayi.zty on 2016/1/28.
 */
public interface IConfigFetcher {
    void fetch(Context context, OnConfigFetchedListener listener);
}
