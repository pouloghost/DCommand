package gt.research.dc.core.common;

/**
 * Created by ayi.zty on 2016/2/17.
 */
public interface ICache {
    void invalidate(String id);

    void clear();
}
