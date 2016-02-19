package gt.research.dc.core.common;

import android.content.Context;

/**
 * Created by ayi.zty on 2016/2/17.
 */
public interface ICache {
    void invalidate(Context context, String id);

    void clear(Context context);
}
